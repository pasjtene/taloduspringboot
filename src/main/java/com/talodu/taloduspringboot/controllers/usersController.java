package com.talodu.taloduspringboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talodu.taloduspringboot.jwtutil.JwtUtil;
import com.talodu.taloduspringboot.model.MyUserDetails;
import com.talodu.taloduspringboot.model.Role;
import com.talodu.taloduspringboot.model.User;
import com.talodu.taloduspringboot.repository.RoleRepository;
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
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@CrossOrigin(origins = {"http://51.68.196.188", "http://localhost:3000","http://localhost:4200"},
        allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api/")
public class usersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("users")
    public List<User> getUsers() {

        return this.userRepository.findAll();
    }

    @GetMapping("roles")
    public List<Role> getRoles() {

        return this.roleRepository.findAll();
    }


    @GetMapping("users2")
        public ResponseEntity<List<User>>getUsers2() {

        return ResponseEntity.ok().body(userService.getUsers());
    }


    @GetMapping("/user/details/{id}")
    public ResponseEntity<User>getUserDetails(@PathVariable Long id) {

        System.out.println("The id is: ");
        System.out.println(id);

        return ResponseEntity.ok().body(userService.getUserByID(id));
    }

    @PostMapping("/user/details")
    public ResponseEntity<User> getTargetUser(@RequestBody Long userid, HttpServletRequest request)
            throws Exception {

        System.out.println("THe user is 1 ,");
        System.out.println("THe user ID is 15 ," + userid);

        //The username string has double quote that we must first remove


        final User target_user = this.userService.getUserByID(userid);

        target_user.setPassword("");

        System.out.println("THe user is ,");
        System.out.println(target_user);

        return ResponseEntity.ok()
                .body(target_user);

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
    public User registerNewUse(@RequestBody User user,
                               HttpServletResponse response) throws IOException {
        System.out.println(user);
        final Collection<Role>  roles = user.getRoles();

        if(this.userService.userExist(user)) {
           log.error("Error code 409, The User already exist: {}", user);
            //SC_CONFLICT will send error status code 409 to the client
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            Map<String, String> error = new HashMap<>();
            error.put("email", user.getEmail());
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
            return user;

        }

        if(this.userService.getUserExistByEmailAddress(user.getEmail())) {
            log.error("Error code 409, The User already exist by email: {}", user.getEmail());
            //SC_CONFLICT will send error status code 409 to the client
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            Map<String, String> error = new HashMap<>();
            error.put("email", user.getEmail());
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
            return user;
        }

        //The user is saved, his id is created and the user is returned
        user.setUsername(user.getEmail());
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProfileImagePath("");
        user.setCreated_at(new Date());
        user.setUpdated_at(new Date());
        user.setImages(new ArrayList<>());
        //user.setDob(LocalDate.of(1900, Month.MARCH, 10));
        //user.setDob(LocalDate.now());

        try {
            this.userRepository.save(user);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        this.userService.addRoleToUser(user.getEmail(), "ROLE_USER");

        return this.userService.getUserByUser(user);

    }

    @PostMapping("update")
    public User updateUser(@RequestBody User user1,
                               HttpServletResponse response) throws IOException {
        System.out.println("The user1 in usercontroller / update");
        System.out.println(user1);
        //final Collection<Role>  roles = user.getRoles();
        user1.setUsername(user1.getEmail());
        User user = this.userService.getUserByEmailAddress(user1.getEmail());

        System.out.println("The user.... in usercontroller / update");
        System.out.println(user);

        log.error("The user is... {}",user);


        if(!this.userService.getUserExistByEmailAddress(user.getEmail())) {
            log.error("Error code 409, The User does not exist by email: {}", user.getEmail());
            //SC_CONFLICT will send error status code 409 to the client
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            Map<String, String> error = new HashMap<>();
            error.put("email", user.getEmail());
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
            return user;
        }

        //The user is saved, his id is created and the user is returned
        //user.setUsername(user.getEmail());
        //user.setRoles(roles);
        user.setFirstName(user1.getFirstName());
        user.setLastName(user1.getLastName());
        //user.setCreated_at(new Date());
        user.setUpdated_at(new Date());
        //user.setImages(new ArrayList<>());
        //user.setDob(LocalDate.of(1900, Month.MARCH, 10));
        user.setDob(user1.getDob());

        try {
            this.userRepository.save(user);
            //this.userRepository.

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

       // this.userService.addRoleToUser(user.getEmail(), "ROLE_USER");
        log.info("user updated {} ", user);

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



    @PostMapping("removeuserrole")
    public ResponseEntity<User>  removeUserRole(@RequestBody Object obj) {

        System.out.println(obj.getClass());

        List l = convertObjectToList(obj);

        System.out.println("The user id is:.." + l.get(l.size()-1).toString());

        //The user Id is the last element of the arrayList
        User user = this.userService.getUserByID(Long.parseLong(l.get(l.size()-1).toString()) );

        System.out.println("The user is..:" + user.toString());

        for (int i = 0; i < l.size()-1; i++) {
            System.out.println("The i22, " + Long.parseLong(l.get(i).toString()) );

            //this.userRepository.delete(userToAddRoles);
            Role role = this.userService.getRoleByID(Long.parseLong(l.get(i).toString()));
            System.out.println("The role 23  is:..."+ role.getName());
            if(user.getRoles().contains(role)) {
                this.userService.removeRoleFromUser(user.getEmail(), role.getName());

            } else {
                System.out.println("The user does not have the role " + role.getName());
            }

        }

        //get the updated user
        User theuser = this.userService.getUserByID(Long.parseLong(l.get(l.size()-1).toString()) );
        theuser.setPassword("");
        //return "users deleted";
        return ResponseEntity.ok().body(theuser);


    }


    @PostMapping("addrolestouser")
    public ResponseEntity<User>  addRolesToUser(@RequestBody Object obj) {

        System.out.println(obj.getClass());

        List l = convertObjectToList(obj);

        System.out.println("The user id is:.." + l.get(l.size()-1).toString());

        //The user id is the last element of the arrayList
        User userToAddRoles = this.userService.getUserByID(Long.parseLong(l.get(l.size()-1).toString()) );

        System.out.println("The user is..:" + userToAddRoles.toString());

        for (int i = 0; i < l.size()-1; i++) {
            System.out.println("The i22, " + Long.parseLong(l.get(i).toString()) );

            //this.userRepository.delete(userToAddRoles);
           Role role = this.userService.getRoleByID(Long.parseLong(l.get(i).toString()));
           System.out.println("The role 23  is:..."+ role.getName());
           if(userToAddRoles.getRoles().contains(role)) {
               System.out.println("The user already has role " + role.getName());
           } else {
               this.userService.addRoleToUser(userToAddRoles.getEmail(), role.getName());
           }

        }

        //get the updated user
        User user = this.userService.getUserByID(Long.parseLong(l.get(l.size()-1).toString()) );
        user.setPassword("");
        //return "users deleted";
        return ResponseEntity.ok().body(user);


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

    @PostMapping("auth_user")
    public ResponseEntity<User> getAuthUser(@RequestBody String username, HttpServletRequest request)
            throws Exception {

        System.out.println("THe user is 1 ,");
        System.out.println("THe user is 1 ," + username);

        //The username string has double quote that we must first remove

        if (username != null && username.length() >= 2
                && username.charAt(0) == '\"' && username.charAt(username.length() - 1) == '\"') {
            username = username.substring(1, username.length() - 1);
        }

        final User auth_user = this.userService.getUserByEmailAddress(username);

        auth_user.setPassword("");

        System.out.println("THe user is ,");
        System.out.println(auth_user);

        return ResponseEntity.ok()
                .body(auth_user );

    }




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

        final String jwt_token = jwtTokenUtil.generateToken(myUserDetails,10);
        final String refresh_token = jwtTokenUtil.generateToken(myUserDetails,30);
        final String access_token = jwtTokenUtil.generateToken(myUserDetails,10);

       // log.error("The myUserdetails is in authenticate controleur is: {}",myUserDetails.getAuthorities());


        final String host = request.getHeader("host");
        final String serverName = request.getServerName();

        System.out.println("The domain is...");
        System.out.println(host);

        //begin set cookie
        /**
        if the user is idle or does not use an authenticated link for more that maxAge (in seconds), the access token will
         expire. When the user tries to login again, if the refresh token is still valid, a new access token is assued in jwtrequestFilter
         */
        ResponseCookie responseCookie = ResponseCookie.from("user-id",jwt_token )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(600)
                //.domain("localhost")
                .domain(serverName)
                //.sameSite("Lax")
                .build();

        ResponseCookie refresh_token_cookie = ResponseCookie.from("refresh_t",refresh_token )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(1800)
                //.domain("localhost")
                .domain(serverName)
                //.sameSite("Lax")
                .build();
//isUserAuth cookie should have the same age or even longer as refresh_Token, and will be checked. if the access token has expired.
// IF it is available, then reflech token is checked, If reflesh token is valid and not expired, then all new tokens are generated

        ResponseCookie auth_cookie = ResponseCookie.from("isUserAuth","true")
                .secure(false)
                .path("/")
                .maxAge(600)
                //.domain("localhost")
                .domain(serverName)
                //.sameSite("Lax")
                .build();


        final User auth_user = this.userService.getUserByEmail(user);

        auth_user.setPassword("");


        ResponseCookie user_name = ResponseCookie.from("un",auth_user.getEmail())
                .secure(false)
                .path("/")
                .maxAge(1800)
                //.domain("localhost")
                .domain(serverName)
                //.sameSite("Lax")
                .build();


       // log.error("Sending refresh response is in authenticate controleur is: {}",myUserDetails.getAuthorities());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.SET_COOKIE, auth_cookie.toString())
                .header(HttpHeaders.SET_COOKIE, user_name.toString())
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