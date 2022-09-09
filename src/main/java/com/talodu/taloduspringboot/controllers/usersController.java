package com.talodu.taloduspringboot.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talodu.taloduspringboot.jwtutil.JwtUtil;
import com.talodu.taloduspringboot.model.MyUserDetails;
import com.talodu.taloduspringboot.model.Role;
import com.talodu.taloduspringboot.model.User;
import com.talodu.taloduspringboot.repository.UserRepository;
import com.talodu.taloduspringboot.services.MyUserDetailService;
import com.talodu.taloduspringboot.services.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
//@Slf4j
@CrossOrigin(origins = {"http://51.68.196.188", "http://localhost:3000"}, allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api/")
public class usersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("users")
    public List<User> getUsers() {

        return this.userRepository.findAll();
    }


    @GetMapping("users2")
        public ResponseEntity<List<User>>getUsers2() {

        return ResponseEntity.ok().body(userService.getUsers());
    }


    @PostMapping("user/save")
    public ResponseEntity<User>saveUser(@RequestBody User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        //log.info("The current base path is {} ", )

        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("role/save")
    public ResponseEntity<Role>saveRole(@RequestBody Role role) {

        return ResponseEntity.ok().body(userService.saveRole(role));
    }

    @PostMapping("role/addtouser")
    public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRolename());
        return  ResponseEntity.ok().build();
    }

    @PostMapping("register")
    public User registerNewUse(@RequestBody User user) {
        System.out.println(user);
        final Collection<Role>  roles = user.getRoles();

        if(this.userService.userExist(user)) {
            //The returned user has an id of 0
            return user;
        }

        //The user is saved, his id is created and the user is returned
        user.setUsername(user.getEmail());
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.userRepository.save(user);

        return this.userService.getUserByUser(user);

    }

    @PostMapping("deleteusers")
    public List<User> deleteUsers(@RequestBody Object obj) {
        //public String deleteUsers(@RequestBody Object obj) {
        //An arrayList of user Ids is received
        System.out.println("deleting users....");
        System.out.println(obj.toString());
        System.out.println(obj);
        System.out.println(obj.getClass());

        List l = convertObjectToList(obj);

        for (int i = 0; i < l.size(); i++) {
            System.out.println("The i22, " + Long.parseLong(l.get(i).toString()) );

            User userToDelete = this.userService.getUserByID(Long.parseLong(l.get(i).toString()) );

            this.userRepository.delete(userToDelete);
        }
        //return "users deleted";
        return this.userRepository.findAll();
    }


    //this is called when the ui refreshes after login.. doesn do any magic
    @GetMapping("login")
    public ResponseEntity<?> loginUser (HttpServletRequest request) {

        final String serverName = request.getServerName();
        ResponseCookie responseCookie = ResponseCookie.from("lig-try","" )
                .secure(false)
                .path("/")
                .maxAge(300)
                //.domain("localhost")
                .domain(serverName)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }



    @GetMapping("logout")
    public ResponseEntity<?> logUserOut(HttpServletRequest request) {


        Cookie cookie[]=request.getCookies();
        Cookie cook;
        String uname= null,pass="";

        if (cookie == null) {
            System.out.println("No cookies...");
        } else  {
            System.out.println("There a cookies..");
            System.out.println(cookie.getClass().getName());

            for (int i = 0; i < cookie.length; i++) {
                cook = cookie[i];
                if(cook.getName().equalsIgnoreCase("user-id")) {
                    uname=cook.getValue();
                    System.out.println(cook.getValue());
                } else  {
                    System.out.println(cook.getValue());
                }

            }

        }

        System.out.println("The user jwt cookie");
        System.out.println(uname);

        System.out.println("The Authorization header");

        String username = null;
        String jwt = null;



        if(uname != null) {
            //We have a cookie
            jwt = uname;
          //  username = jwtUtil.extractUsername(jwt);
        }

        final String serverName = request.getServerName();

        //ResponseCookie responseCookie = ResponseCookie.from("user-id",jwt )
        ResponseCookie responseCookie = ResponseCookie.from("user-id","" )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(00)
                //.domain("localhost")
                .domain(serverName)
                //.sameSite("Lax")
                .build();


        ResponseCookie auth_cookie = ResponseCookie.from("isUserAuth","false")
                .secure(false)
                .path("/")
                .maxAge(300)
                //.domain("localhost")
                .domain(serverName)
                //.sameSite("Lax")
                .build();

        ResponseCookie access_token = ResponseCookie.from("access_t","")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(00)
                //.domain("localhost")
                .domain(serverName)
                //.sameSite("Lax")
                .build();

        ResponseCookie refresh_token = ResponseCookie.from("refresh_t","")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(00)
                //.domain("localhost")
                .domain(serverName)
                //.sameSite("Lax")
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.SET_COOKIE, auth_cookie.toString())
                .header(HttpHeaders.SET_COOKIE, access_token.toString())
                .header(HttpHeaders.SET_COOKIE, refresh_token.toString())

                .build();

    }

    public static List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        }
        return list;
    }




    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @PostMapping("authenticate")
    public ResponseEntity<User> createAuthenticationToken(@RequestBody User user, HttpServletRequest request)
            throws Exception {

        System.out.println("The user to auth");
        System.out.println(user.toString());
        //log.info("Authenticating user {}", user);


        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
                    );
        }
        catch (BadCredentialsException e){
            throw new Exception("Incorect username or password", e);
        }

        final MyUserDetails myUserDetails = (MyUserDetails) myUserDetailService.loadUserByUsername(user.getEmail());

        final String jwt_token = jwtTokenUtil.generateToken(myUserDetails,5);
        final String refresh_token = jwtTokenUtil.generateToken(myUserDetails,10);
        final String access_token = jwtTokenUtil.generateToken(myUserDetails,2);

       // log.error("The myUserdetails is in authenticate controleur is: {}",myUserDetails.getAuthorities());


        final String host = request.getHeader("host");
        final String serverName = request.getServerName();

        System.out.println("The domain is...");
        System.out.println(host);




        //begin set cookie
        ResponseCookie responseCookie = ResponseCookie.from("user-id",jwt_token )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(90)
                //.domain("localhost")
                .domain(serverName)
                //.sameSite("Lax")
                .build();

        ResponseCookie refresh_token_cookie = ResponseCookie.from("refresh_t",refresh_token )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(300)
                //.domain("localhost")
                .domain(serverName)
                //.sameSite("Lax")
                .build();
//isUserAuth cookie should have the same age or even longer as refresh_Token, and will be checked. if the access token has expired.
// IF it is available, then reflech token is checked, If reflesh token is valid and not expired, then all new tokens are generated

        ResponseCookie auth_cookie = ResponseCookie.from("isUserAuth","true")
                .secure(false)
                .path("/")
                .maxAge(300)
                //.domain("localhost")
                .domain(serverName)
                //.sameSite("Lax")
                .build();


        final User auth_user = this.userService.getUserByEmail(user);

        auth_user.setPassword("");

       // log.error("Sending refresh response is in authenticate controleur is: {}",myUserDetails.getAuthorities());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.SET_COOKIE, auth_cookie.toString())
                .header(HttpHeaders.SET_COOKIE, refresh_token_cookie.toString())
                .body(auth_user);
                //.build();
        //return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }


    private String getCookieValue(HttpServletRequest req,  String cookieName) {
        if (req.getCookies() == null) return null;

        return Arrays.stream(req.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }


    @GetMapping("/token/refresh")
    //public void refreshToken(HttpServletRequest request, HttpServletResponse response)
    public ResponseEntity<User>  refreshToken(HttpServletRequest request, HttpServletResponse response)

            throws IOException {

       // log.info("Refreshing tokens...");
        String jwt = null;
        String username = null;

        if (getCookieValue(request, "refresh_t") != null) {
          //  log.error("Refreshing token in userController No access token, but refresh token exist..{}", getCookieValue(request, "refresh_t"));
            //log.info("Sending a redirect now....");
            //We have a cookie


            jwt = getCookieValue(request, "refresh_t");
            username = jwtTokenUtil.extractUsername(jwt);
        }

            //generate new tokens here

            MyUserDetails myUserDetails = (MyUserDetails) myUserDetailService.loadUserByUsername(username);

            final String jwt_token = jwtTokenUtil.generateToken(myUserDetails, 5);
            final String refresh_token = jwtTokenUtil.generateToken(myUserDetails, 10);
            final String access_token = jwtTokenUtil.generateToken(myUserDetails, 2);

          //  log.error("The myUserdetails is in userController controleur is: {}", myUserDetails.getAuthorities());

        refreshCookies(request, response,"user-id", access_token, 90,true);


            final String host = request.getHeader("host");
            final String serverName = request.getServerName();

            System.out.println("The domain is...");
            System.out.println(host);


            //begin set cookie
            ResponseCookie responseCookie = ResponseCookie.from("user-id", jwt_token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(90)
                    //.domain("localhost")
                    .domain(serverName)
                    //.sameSite("Lax")
                    .build();

            ResponseCookie refresh_token_cookie = ResponseCookie.from("refresh_t", refresh_token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(300)
                    //.domain("localhost")
                    .domain(serverName)
                    //.sameSite("Lax")
                    .build();
//isUserAuth cookie should have the same age or even longer as refresh_Token, and will be checked. if the access token has expired.
// IF it is available, then reflech token is checked, If reflesh token is valid and not expired, then all new tokens are generated

            ResponseCookie auth_cookie = ResponseCookie.from("isUserAuth", "true")
                    .secure(false)
                    .path("/")
                    .maxAge(300)
                    //.domain("localhost")
                    .domain(serverName)
                    //.sameSite("Lax")
                    .build();

            final User auth_user = this.userService.getUserByEmailAddress(username);

            auth_user.setPassword("");

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, auth_cookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refresh_token_cookie.toString())
                    .body(auth_user);


       // }



   }


    private void refreshCookies(HttpServletRequest request, HttpServletResponse response,
                                String cookie_name, String cookie_value, Integer duration_second, Boolean isHttpOnly) throws IOException {

       // log.error("Refreshing cookies...{}",cookie_name);

        Cookie rcookie = new Cookie(cookie_name,cookie_value);

        final String serverName = request.getServerName();

        rcookie.setMaxAge(duration_second);
        rcookie.setSecure(false);
        rcookie.setHttpOnly(isHttpOnly);
        rcookie.setPath("/");
        rcookie.setDomain(serverName);

        //log.error("Exeption during auth {}", e.getMessage());
        //response.setHeader("error auth failled", e.getMessage());
        response.setStatus(HttpServletResponse.SC_ACCEPTED);

        Map<String, String> error = new HashMap<>();
        error.put("Refreshing cookie", cookie_name);

        response.setContentType("application/json");
        response.addCookie(rcookie);

      //  log.info("The cookie to refresh is...{}",rcookie);


        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }








}






@Data
class RoleToUserForm {
    private String username;
    private String rolename;
}