package com.talodu.taloduspringboot.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.SizeLimitExceededException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    public String handleError1(MultipartException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());

        return "redirect:/uploadStatus";

    }


    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<Object > handleError2(SizeLimitExceededException e) {
        return new ResponseEntity<Object>("File too big", HttpStatus.valueOf(505));

    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String > handleError3(MaxUploadSizeExceededException e) {
        return new ResponseEntity<String>("File too big", HttpStatus.valueOf(505));

    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String > handleUsernameNotFoundException(UsernameNotFoundException e) {


        System.out.println(("User email Not found: "));
        ResponseCookie responseCookie = ResponseCookie.from("user-id","" )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(00)
                //.domain("localhost")
                //.domain(serverName)
                .sameSite("Lax")
                .build();


        ResponseCookie auth_cookie = ResponseCookie.from("isUserAuth","false")
                .secure(false)
                .path("/")
                .maxAge(300)
                //.domain("localhost")
                //.domain(serverName)
                .sameSite("Lax")
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.SET_COOKIE, auth_cookie.toString())
                .build();


        //return new ResponseEntity<String>("File too big", HttpStatus.valueOf(505));

    }

}
