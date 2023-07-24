package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.ProcessScore;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ProcessServiceTest {
    @Autowired
    private ProcessService processService;
    @Autowired
    private UserService userService;


    @Test
    public void listProcessScoresTest() {
        processService.listProcessScores(1, "1069607508454658048").doOnSuccess(ps -> {
            for (ProcessScore p : ps) {
                log.debug(p.toString());
            }
        }).block();
    }

}
