package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.dto.StudentDTO;
import com.example.graduationprojectprocessmanagement.service.AdminService;
import com.example.graduationprojectprocessmanagement.service.ProcessService;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/")
public class AdminController {
    private final AdminService adminService;
    private final ProcessService processService;

    @PostMapping("teachers")
    public Mono<ResultVO> postTeachers(@RequestBody List<User> users) {
        return adminService.addUsers(users, User.ROLE_TEACHER).map(r -> ResultVO.success(Map.of()));
    }

    @PostMapping("students")
    public Mono<ResultVO> postStudents(@RequestBody List<User> users) {
        return adminService.addUsers(users, User.ROLE_STUDENT).map(r -> ResultVO.success(Map.of()));
    }

    @PostMapping("students/projects")
    public Mono<ResultVO> postProjects(@RequestBody List<StudentDTO> studentDTOs) {
        return adminService.updateProjectTitles(studentDTOs).map(r -> ResultVO.success(Map.of()));
    }

    @PostMapping("students/groupnumbers")
    public Mono<ResultVO> postgroupNumbers(@RequestBody List<StudentDTO> studentDTOs) {
        return adminService.updateStudentsGroup(studentDTOs).map(r -> ResultVO.success(Map.of()));
    }

    @PostMapping("processes")
    public Mono<ResultVO> postProcess(@RequestBody Process process) {
        return adminService.addProcess(process)
                .flatMap(r -> processService.listProcesses())
                .map(processes -> ResultVO.success(Map.of("processes", processes)));

    }

    @PutMapping("passwords/{number}")
    public Mono<Void> putPassword(@PathVariable String number) {
        return adminService.updatePassword(number).then();
    }

    @GetMapping("info")
    public Mono<ResultVO> getInfo() {
        return Mono.just(ResultVO.success(Map.of("test", "test")));
    }
}
