package com.talodu.taloduspringboot.jwtutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talodu.taloduspringboot.model.MyUserDetails;
import com.talodu.taloduspringboot.services.MyUserDetailService;
import com.talodu.taloduspringboot.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
//@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Autowired
    private UserService userService;

    public Optional<String> readServletCookie(HttpServletRequest request, String name){
        Cookie cookies[]=request.getCookies();
        return Arrays.stream(cookies)
                .filter(cookie->name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny();
    }

    private String getCookieValue(HttpServletRequest req,  String cookieName) {
        if (req.getCookies() == null) return null;

        return Arrays.stream(req.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }


    private void refreshCookies(HttpServletRequest request, HttpServletResponse response, String refresh_t,
                                String access_t, Integer duration_second, Boolean isHttpOnly) throws IOException {

        //log.error("Refreshing cookies...{}",access_t);

        Cookie rcookie = new Cookie("user-id",access_t);
        Cookie refresh_cookie = new Cookie("refresh_t",refresh_t);
        Cookie user_auth = new Cookie("isUserAuth","true");

        final String serverName = request.getServerName();

        rcookie.setMaxAge(600);
        rcookie.setSecure(false);
        rcookie.setHttpOnly(isHttpOnly);
        rcookie.setPath("/");
        rcookie.setDomain(serverName);

        refresh_cookie.setMaxAge(3600);
        refresh_cookie.setSecure(false);
        refresh_cookie.setHttpOnly(isHttpOnly);
        refresh_cookie.setPath("/");
        refresh_cookie.setDomain(serverName);


        user_auth.setMaxAge(3600);
        user_auth.setSecure(false);
       // user_auth.setHttpOnly(isHttpOnly);
        user_auth.setPath("/");
        user_auth.setDomain(serverName);

        //log.error("Exeption during auth {}", e.getMessage());
        //response.setHeader("error auth failled", e.getMessage());
        response.setStatus(HttpServletResponse.SC_ACCEPTED);

        Map<String, String> error = new HashMap<>();
        error.put("Refreshing cookie", "refresh_t");

        response.setContentType("application/json");
        response.addCookie(rcookie);
        response.addCookie(refresh_cookie);
        response.addCookie(user_auth);

        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        //log.info("does user-id, access_token  cookie exist...{} ",getCookieValue(request,"user-id"));
        //log.info("does the refresh_t cookie exist...{} ",getCookieValue(request,"refresh_t"));

        String username = null;
        String jwt = null;
        if(getCookieValue(request,"user-id") != null) {
            //We have a cookie
            jwt = getCookieValue(request,"user-id");
            username = jwtUtil.extractUsername(jwt);
        } else {
            // The access token has expired. We can renew all tokens, if the refresh token is still valid

            if(getCookieValue(request,"refresh_t") != null) {
               // log.error("No access token, but refresh token exist..{}",getCookieValue(request,"refresh_t"));
                //log.info("Sending a redirect now....");
                //We have a cookie

                jwt = getCookieValue(request,"refresh_t");
                username = jwtUtil.extractUsername(jwt);

                //log.info("The username....{}",username);

                //generate new tokens here

                 MyUserDetails myUserDetails = (MyUserDetails) myUserDetailService.loadUserByUsername(username);

                //log.info("The username....{}",username);
                //log.info("The userdetail....{}",myUserDetails);
                final String access_token = jwtUtil.generateToken(myUserDetails,2);
                final String refresh_token = jwtUtil.generateToken(myUserDetails,5);

                refreshCookies(request, response,refresh_token,access_token,90,true);

            }

        }

        //username = uname;

        if((username !=null) && (SecurityContextHolder.getContext().getAuthentication() == null)) {

            try {
                //MyUserDetails myUserDetails = (MyUserDetails) this.myUserDetailService.loadUserByUsername(username);
                MyUserDetails myUserDetails = (MyUserDetails) this.myUserDetailService.loadUserByUsername(username);

                if(jwtUtil.validateToken(jwt, myUserDetails)) {
                   // log.error("The jwt token is validated....");
                    UsernamePasswordAuthenticationToken
                            usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            myUserDetails, null, myUserDetails.getAuthorities());
                    //log.error("The authorities are...{}",myUserDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } catch (Exception e) {
                Cookie rcookie = new Cookie("user-id","");
                Cookie acookie = new Cookie("isUserAuth","false");
                final String serverName = request.getServerName();

                rcookie.setMaxAge(00);
                rcookie.setSecure(false);
                rcookie.setHttpOnly(true);
                rcookie.setPath("/");
                rcookie.setDomain(serverName);

                acookie.setMaxAge(300);
                acookie.setSecure(false);
                //acookie.setHttpOnly(false);
                acookie.setPath("/");
                acookie.setDomain(serverName);


              //  log.error("Exeption during auth {}", e.getMessage());
                response.setHeader("error auth failled", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                Map<String, String> error = new HashMap<>();
                error.put("Error message", e.getMessage());

                response.setContentType("application/json");
                response.addCookie(rcookie);
                response.addCookie(acookie);
                new ObjectMapper().writeValue(response.getOutputStream(), error);

            }

        } else {
            System.out.println("The user auth failed");
        }

        filterChain.doFilter(request, response);

    }


}
