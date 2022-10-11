package com.talodu.taloduspringboot.services;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void shouldReturn11() {
        var userService = new UserService();
        assertEquals(11,userService.getAgeYears(LocalDate.of(2011, Month.SEPTEMBER, 19)));

    }


}