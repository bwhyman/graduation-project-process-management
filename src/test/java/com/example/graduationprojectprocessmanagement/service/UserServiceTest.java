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
        userService.listUsers(1, User.ROLE_STUDENT).doOnSuccess(students -> {
            for (User s : students) {
                log.debug(s.toString());
            }
        }).block();
    }

    @Test
    public void listTeachersTest() {
        userService.listUsers(1, User.ROLE_TEACHER).doOnSuccess(teachers -> {
            for (User t : teachers) {
                log.debug(t.toString());
            }
        }).block();
    }
    @Test
    public void listStudentsByTidTest() {
        User t1 = userService.getUser("2001").block();
        assert t1 != null;

        userService.listStudents(t1.getId()).doOnSuccess(users -> {
            for (User user : users) {
                log.debug(user.toString());
            }
        }).block();
    }
}
