package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dox.ProcessScore;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.exception.Code;
import com.example.graduationprojectprocessmanagement.service.ProcessService;
import com.example.graduationprojectprocessmanagement.service.UserService;
import com.example.graduationprojectprocessmanagement.vo.RequestAttributeConstant;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/teacher/process/")
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessService processService;
    private final UserService userService;

    @GetMapping("processes")
    public Mono<ResultVO> getProcesses() {
        return processService.listProcesses()
                .map(processes -> ResultVO.success(Map.of("processes", processes)));
    }

    @GetMapping("processes/{pid}")
    public Mono<ResultVO> getProcess(@PathVariable String pid,
                                     @RequestAttribute(RequestAttributeConstant.UID) String tid,
                                     @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int groupNumber) {
        Map<String, Object> map = new HashMap<>();
        return processService.getProcess(pid)
                .flatMap(process -> switch (process.getAuth()) {
                    case Process.TUTOR -> {
                        Mono<List<User>> studentsM = userService.listStudents(tid)
                                .doOnSuccess(students -> map.put("students", students));
                        Mono<List<ProcessScore>> processScoresM = processService.listProcessScores(pid, tid)
                                .doOnSuccess(processScores -> map.put("processScores", processScores));
                        yield Mono.when(studentsM, processScoresM).thenReturn(ResultVO.success(map));
                    }
                    case Process.REVIEW -> {
                        Mono<List<User>> studentsM = userService.listUsers(groupNumber, User.ROLE_STUDENT)
                                .doOnSuccess(students -> map.put("students", students));
                        Mono<List<User>> teachersM = userService.listUsers(groupNumber, User.ROLE_TEACHER)
                                .doOnSuccess(teachers -> map.put("teachers", teachers));
                        Mono<List<ProcessScore>> processScoresM = processService.listProcessScores(groupNumber, pid)
                                .doOnSuccess(processScores -> map.put("processScores", processScores));
                        yield Mono.when(studentsM, teachersM, processScoresM).thenReturn(ResultVO.success(map));
                    }
                    default -> Mono.just(ResultVO.error(Code.BAD_REQUEST));
                });
    }

    @PostMapping("processscores")
    public Mono<ResultVO> postProcessScore(@RequestBody ProcessScore processScore,
                                           @RequestAttribute(RequestAttributeConstant.UID) String tid,
                                           @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int groupNumber) {
        processScore.setTeacherId(tid);
        return processService.addProcessScore(processScore)
                .flatMap(ps -> processService.listProcessScores(groupNumber, ps.getProcessId()))
                .map(processScores -> ResultVO.success(Map.of("processScores", processScores)));
    }
}
