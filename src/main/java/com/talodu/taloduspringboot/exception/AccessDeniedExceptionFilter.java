package com.talodu.taloduspringboot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

//@Slf4j
@Service
public class AccessDeniedExceptionFilter extends OncePerRequestFilter {

    //this filter is not currently working... replaced by Custom accessDeniedhandler

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {

           // log.info("User '" + authentication.getName() + "' attempted to access the URL: " + request.getRequestURI());


        } else {
          //  log.info("User auth is null '");
        }

        try {
          //  log.error("...we are here {}", request.getRequestURL());
            filterChain.doFilter(request, response);
      //  } catch (AccessDeniedException e) {
        } catch (Exception e) {
           // log.error("There is a real exeption...{}");
         //   log.error("Access denied no sufficient permission {}", e.getCause());

        }

        //filterChain.doFilter(request, response);

    }
}
