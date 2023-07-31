package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class StudentServiceTest {
    @Autowired
    private StudentService studentService;
    @Autowired
    private UserService userService;

    @Test
    public void addSelectionTest() {
        User t1 = userService.getUserByNumber("2001").block();
        User t2 = userService.getUserByNumber("2002").block();
        assert t1 != null;
        assert t2 != null;

        User s1 = userService.getUserByNumber("202001").block();
        User s2 = userService.getUserByNumber("202002").block();
        User s3 = userService.getUserByNumber("202003").block();
        User s4 = userService.getUserByNumber("202004").block();
        assert s1 != null;
        assert s2 != null;
        assert s3 != null;
        assert s4 != null;

        studentService.addSelection(s1.getId(), t1.getId()).block();
        studentService.addSelection(s2.getId(), t1.getId()).block();
        studentService.addSelection(s3.getId(), t2.getId()).block();
        studentService.addSelection(s4.getId(), t2.getId()).block();
    }

}
