package com.talodu.taloduspringboot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table
@Data
@AllArgsConstructor
//@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique=true)
    private String email;
    private String username;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    //private String roles;
    private Collection<Role> roles = new ArrayList<>();
    private String profileImagePath;


    public User() {

    }


    public User(String firstName, String lastName, String email, String password, String username, Collection<Role> roles, String profileImagePath) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.username = username;
        this.roles = roles;
        this.profileImagePath = profileImagePath;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public String getUsername() {
        //return username;

        return this.getEmail();

    }

    public  String getProfileImagePath() {
        return  profileImagePath;
    }

    public  void  setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public void setUsername(String username) {
        this.username = username;
    }




    public long getId() {
        return id;
    }



    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }




    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles='" + roles + '\'' +
                ", profileImagePath='" + profileImagePath + '\'' +
                '}';
    }


}

