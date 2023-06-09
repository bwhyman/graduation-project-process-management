package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class SelectionServiceTest {
    @Autowired
    private SelectionService selectionService;
    @Autowired
    private UserService userService;

    @Test
    public void addSelectionTest() {
        User t1 = userService.getUser("2001").block();
        User t2 = userService.getUser("2002").block();
        assert t1 != null;
        assert t2 != null;

        User s1 = userService.getUser("202001").block();
        User s2 = userService.getUser("202002").block();
        User s3 = userService.getUser("202003").block();
        User s4 = userService.getUser("202004").block();
        assert s1 != null;
        assert s2 != null;
        assert s3 != null;
        assert s4 != null;

        selectionService.addSelection(s1.getId(), t1.getId()).block();
        selectionService.addSelection(s2.getId(), t1.getId()).block();
        selectionService.addSelection(s3.getId(), t2.getId()).block();
        selectionService.addSelection(s4.getId(), t2.getId()).block();
    }

}
