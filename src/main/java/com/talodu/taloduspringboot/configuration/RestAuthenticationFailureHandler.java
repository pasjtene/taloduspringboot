package com.talodu.taloduspringboot.configuration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler
{
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        handleAuthFailure(request);
    }


    public ResponseEntity<?> handleAuthFailure (HttpServletRequest request) {


        final String serverName = request.getServerName();

        //ResponseCookie responseCookie = ResponseCookie.from("user-id",jwt )
        ResponseCookie responseCookie = ResponseCookie.from("user-id","" )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(00)
                //.domain("localhost")
                .domain(serverName)
                .sameSite("Lax")
                .build();


        ResponseCookie auth_cookie = ResponseCookie.from("isUserAuth","false")
                .secure(false)
                .path("/")
                .maxAge(300)
                //.domain("localhost")
                .domain(serverName)
                .sameSite("Lax")
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.SET_COOKIE, auth_cookie.toString())
                .build();

    }
}
