package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.ProcessScore;
import com.example.graduationprojectprocessmanagement.dox.User;
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
    public void addProcessScoreTest() {
        String detail1 = """
                {
                "scores": 80, 
                "details": [{"number": 0, "score": 40}, {"number": 1, "score": 40}]
                }
                """;
        User s1 = userService.getUser("202003").block();
        User t1 = userService.getUser("2001").block();
        assert s1 != null;
        assert t1 != null;

        ProcessScore ps1 = ProcessScore.builder()
                .processId("1069607508454658048")
                .studentId(s1.getId())
                .teacherId(t1.getId())
                .detail(detail1)
                .build();
        processService.addProcessScore(ps1).doOnSuccess(ps -> log.debug(ps.toString())).block();

        String detail2 = """
                {
                "scores": 70, 
                "detail": [{"number": 0, "score": 35}, {"number": 1, "score": 35}]
                }
                """;
        User s2 = userService.getUser("202004").block();
        assert s2 != null;

        ProcessScore ps2 = ProcessScore.builder()
                .processId("1069607508454658048")
                .studentId(s2.getId())
                .teacherId(t1.getId())
                .detail(detail2)
                .build();

        processService.addProcessScore(ps2).doOnSuccess(ps -> log.debug(ps.toString())).block();
    }

    @Test
    public void listProcessScoresTest() {
        processService.listProcessScores(1, "1069607508454658048").doOnSuccess(ps -> {
            for (ProcessScore p : ps) {
                log.debug(p.toString());
            }
        }).block();
    }
}
