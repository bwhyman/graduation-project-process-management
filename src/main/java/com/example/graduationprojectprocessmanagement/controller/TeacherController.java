package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.ProcessScore;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.dto.ProcessScoreDTO;
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
@RequestMapping("/api/teacher/")
@RequiredArgsConstructor
public class TeacherController {
    private final ProcessService processService;
    private final UserService userService;

    @GetMapping("{tid}/students")
    public Mono<ResultVO> getStudents(@PathVariable String tid) {
        return userService.listStudents(tid)
                .map(students -> ResultVO.success(Map.of("students", students)));
    }

    @GetMapping("processes")
    public Mono<ResultVO> getProcesses() {
        return processService.listProcesses()
                .map(processes -> ResultVO.success(Map.of("processes", processes)));
    }

    @GetMapping("/groups/{g}")
    public Mono<ResultVO> getGroupStudents(@PathVariable int g) {
        Map<String, Object> m = new HashMap<>();
        Mono<List<User>> studentsM = userService.listUsers(User.ROLE_STUDENT, g)
                .doOnSuccess(students -> m.put("students", students));
        Mono<List<User>> teachersM = userService.listUsers(User.ROLE_TEACHER, g)
                .doOnSuccess(teachers -> m.put("teachers", teachers));
        return Mono.when(studentsM, teachersM).thenReturn(ResultVO.success(m));
    }


    @GetMapping("processes/{pid}/group/{g}")
    public Mono<ResultVO> getProcess(@PathVariable String pid, @PathVariable int g) {
        return processService.listProcessScores(g, pid)
                .map(processScores -> ResultVO.success(Map.of("processScores", processScores)));
    }

    // 更新与添加
    @PostMapping("processscores")
    public Mono<ResultVO> postProcess(@RequestBody ProcessScoreDTO processScoreDTO,
                                      @RequestAttribute(RequestAttributeConstant.UID) String tid,
                                      @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g) {

        return processService.updateProcessScore(processScoreDTO.getProcessId(), processScoreDTO.getStudentId(), tid, processScoreDTO.getScore())
                .flatMap(r -> processService.listProcessScores(g, processScoreDTO.getProcessId())
                        .map(processScores -> ResultVO.success(Map.of("processScores", processScores))));
    }
}
