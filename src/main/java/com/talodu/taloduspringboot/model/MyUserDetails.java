package com.talodu.taloduspringboot.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class MyUserDetails implements UserDetails {

    private String userName;
    private String password;
    private boolean isActive;
    private List<GrantedAuthority> authorities;

    public MyUserDetails(User user) {
        this.userName = user.getEmail();
        this.password = user.getPassword();
        this.isActive = true;
        this.authorities =  user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
              //  Arrays.stream(user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }

    public MyUserDetails(String username) {
        this.userName = username;
    }



    public Collection<? extends GrantedAuthority> getAuthorities() {
      // return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
       // return Arrays.asList(new SimpleGrantedAuthority(new ArrayList<>()));

        return  authorities;
    }



    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
