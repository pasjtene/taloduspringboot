package com.talodu.taloduspringboot.services;

import com.talodu.taloduspringboot.model.MyUserDetails;
import com.talodu.taloduspringboot.model.User;
import com.talodu.taloduspringboot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
//@Slf4j
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Optional<User> user = userRepository.findUserByEmail(username);
        User user = userService.getUserByEmailAddress(username);

        //user.orElseThrow(()->  new UsernameNotFoundException("Not found: "+username));

       // if(!user.isPresent()) {
            if(user == null) {

            //log.error("Username Not found: {} ", username);


            throw new UsernameNotFoundException
                    ("Not found: "+username);
        }

            /*
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            });

            */


       // return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);

        return new MyUserDetails(user);


      //return user.map(MyUserDetails::new).get();
       // return user;

    }




}
