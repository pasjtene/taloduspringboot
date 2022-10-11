package com.talodu.taloduspringboot.model;

import com.talodu.taloduspringboot.services.AbstractTimestamp;
import lombok.AllArgsConstructor;
import lombok.Data;


import javax.persistence.*;
import java.time.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Entity
@Table
@Data
@AllArgsConstructor
//@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy  = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    @Column(unique=true)
    private String email;
    private String username;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    //private String roles;
    private Collection<Role> roles = new ArrayList<>();
    //private Collection<Role> roles;
    private String profileImagePath;
    private LocalDate dob;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Collection<UserImage> images = new ArrayList<>();

    private Date created_at;
    private Date updated_at;

    @Transient
    private String age;

    @Transient
    private String fullName;

    @Transient
    private String created;

    @Transient
    private String updated;

    //@Column(name = "created_at")
    //@CreationTimestamp
    //private Date created_at;

    //@Column(name = "updated_at")
    //@UpdateTimestamp
    //private Date updated_at;



    //private List<UserImage> images;


    public User() {

    }


    public User(long id, String firstName, String lastName, String email, String password, String username,
                Collection<Role> roles, String profileImagePath, LocalDate dob,
                Collection<UserImage> images, Date created_at, Date updated_at
                ) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.username = username;
        this.roles = roles;
        this.profileImagePath = profileImagePath;
        this.dob = dob;
        this.images = images;
        this.created_at = created_at;
        this.updated_at = updated_at;
        //this.firstName = fullName;

    }

    public LocalDate getDob() {

        //if(dob.getYear() == 1900) return "none";
        return dob;
    }
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated() {
        if (this.created_at == null)
            return "none";


        String created =  "";


                Date startDate = new Date();
                Date endDate   = new Date();

        //long duration  = endDate.getTime() - startDate.getTime();
        long duration  = endDate.getTime() - this.created_at.getTime();



        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);



        if(diffInSeconds < 60 ) {
            created = diffInSeconds + " seconds ago";
        }

        if(diffInMinutes  < 60 ) {
            created = (int)diffInSeconds/60 + "m " + diffInSeconds%60 + "s ago" ;
        }

        if((diffInMinutes > 60) && (diffInHours < 24) ) {
            created = (int)diffInMinutes/60 + "h " + diffInMinutes%60 + "m ago" ;
        }

        if(diffInHours  > 24 ) {
            created = (int)diffInHours/24 + "d " +
                    diffInHours%24 + "h " +
                    diffInMinutes%1440 + "m ago"  ;
            // 1 days has 1440 minutes
        } // else { created = (int)diffInDays/30 +" Months" +  diffInDays%30 + " Days";}


      return created;
    }

    public String getUpdated() {
        return firstName + " " + lastName;
    }




    public String getAge() {

        String age = "";
        if (dob == null)
            return "";



        if (Period.between(dob, LocalDate.now()).getYears() < 1) {
            if(Period.between(dob, LocalDate.now()).getMonths() < 12) {
                age = Period.between(dob, LocalDate.now()).getMonths() + "M ";
            }

        } else {
            age = Period.between(dob, LocalDate.now()).getYears() + "Y " +
                    Period.between(dob, LocalDate.now()).getMonths()%12 + "M " +
                    Period.between(dob, LocalDate.now()).getDays()%365 + "D"
            ;
        }


        if(dob.getYear() == 1900) { age = "none" ;}



        //return age;
        return age;
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

