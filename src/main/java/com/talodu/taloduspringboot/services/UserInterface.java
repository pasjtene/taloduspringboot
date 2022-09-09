package com.talodu.taloduspringboot.services;

import com.talodu.taloduspringboot.model.Role;
import com.talodu.taloduspringboot.model.User;

import java.util.List;

public interface UserInterface {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String role);
    User getUser(String username);
    List<User> getUsers();
}
