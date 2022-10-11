package com.talodu.taloduspringboot.services;

import com.talodu.taloduspringboot.model.Role;
import com.talodu.taloduspringboot.model.User;
import com.talodu.taloduspringboot.repository.RoleRepository;
import com.talodu.taloduspringboot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service @Transactional
//@Slf4j
public class UserService implements UserInterface{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  RoleRepository roleRepository;

    public boolean userExist(User user) {

        Optional<User> userExist = this.userRepository.findUserByEmail(user.getEmail());

        return userExist.isPresent();
    }



    public User getUserByUser(User user) {

        Optional<User> userExist = this.userRepository.findUserByEmail(user.getEmail());
        final String str = "user with email Does not exist: " + user.getEmail();

        if(userExist.isPresent()) {

            User newUser = this.userRepository.findById(user.getId()).orElseThrow(
                    () -> new IllegalStateException("Student with id Does not exist")
            );
            return newUser;
        }

        return user;
    }


    public User getUserByEmail(User user) {

        Optional<User> userExist = this.userRepository.findUserByEmail(user.getEmail());
        final String str = "user with email Does not exist: " + user.getEmail();

        if(userExist.isPresent()) {

            User newUser = this.userRepository.findUserByEmail(user.getEmail()).orElseThrow(
                    () -> new IllegalStateException(str)
            );
            return newUser;
        }

        return user;
    }



    public User getUserByEmailAddress(String email) {

        Optional<User> userExist = this.userRepository.findUserByEmail(email);
        final String str = "user with email Does not exist: " + email;

        if(userExist.isPresent()) {

            User newUser = this.userRepository.findUserByEmail(email).orElseThrow(
                    () -> new IllegalStateException(str)
            );
            return newUser;
        }

        return new User();
    }

    public Boolean getUserExistByEmailAddress(String email) {

        Optional<User> userExist = this.userRepository.findUserByEmail(email);
        final String str = "user with email Does not exist: " + email;

        return  userExist.isPresent();

    }


    public User getUserByID(Long userId) {

        //Optional<User> userExist = this.userRepository.findUserByEmail(user.getEmail());

        final String str = "user with id Does not exist: " + userId.toString();

        User theuser = this.userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException(str)
        );

        return theuser;
    }

    public Role getRoleByID(Long roleId) {

        //Optional<User> userExist = this.userRepository.findUserByEmail(user.getEmail());

        final String str = "role with id Does not exist: " + roleId.toString();

        Role therole = this.roleRepository.findById(roleId).orElseThrow(
                () -> new IllegalStateException(str)
        );

        return therole;
    }

    public int getAgeYears (LocalDate dob) {
        return  Period.between(dob, LocalDate.now()).getYears();
    }


    @Override
    public User saveUser(User user) {
        //log.info("Saving user {} to the database.. ",user);
        if(this.getUserExistByEmailAddress(user.getEmail())) return null;
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        //User user = userRepository.findByUsername(username);
        //log.info("Adding role {} to user..{} ",roleName,username);

        User user = this.getUserByEmailAddress(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);

    }


    public void removeRoleFromUser(String username, String roleName) {
        //User user = userRepository.findByUsername(username);
        //log.info("Adding role {} to user..{} ",roleName,username);

        User user = this.getUserByEmailAddress(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().remove(role);

    }

    @Override
    public User getUser(String username) {
        return this.getUserByEmailAddress(username);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
