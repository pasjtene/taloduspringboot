package com.talodu.taloduspringboot.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {

           // log.info("User '" + authentication.getName() + "' attempted to access the URL: " + request.getRequestURI());


        }
       // log.error("Access was denied to url " + request.getRequestURI());


        response.setHeader("No permission  for url", request.getRequestURI());
        //response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
       // response.setStatus(HttpServletResponse.);

        Map<String, String> error = new HashMap<>();
        error.put("Access was denied to url", request.getRequestURI());

        response.setContentType("application/json");
        //response.addCookie(rcookie);
        //response.addCookie(acookie);
        new ObjectMapper().writeValue(response.getOutputStream(), error);

       // response.sendRedirect(request.getContextPath() + "/access-denied");
    }
}
