package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Test
    public void listStudentsTest() {
        userService.listUsers(User.ROLE_STUDENT, "1").doOnSuccess(students -> {
            for (User s : students) {
                log.debug(s.toString());
            }
        }).block();
    }

    @Test
    public void listTeachersTest() {
        userService.listUsers(User.ROLE_TEACHER, "1").doOnSuccess(teachers -> {
            for (User t : teachers) {
                log.debug(t.toString());
            }
        }).block();
    }

}
