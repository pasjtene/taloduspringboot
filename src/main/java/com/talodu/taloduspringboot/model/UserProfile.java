package com.talodu.taloduspringboot.model;

import com.talodu.taloduspringboot.services.AbstractTimestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;


@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile extends AbstractTimestamp {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user_profile")
    private Collection<UserImage> images = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "userId")
    private User user;
    private LocalDate dob;
    @Transient
    private String age;

}
