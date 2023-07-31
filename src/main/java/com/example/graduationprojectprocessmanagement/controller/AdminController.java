package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.dto.StudentDTO;
import com.example.graduationprojectprocessmanagement.service.AdminService;
import com.example.graduationprojectprocessmanagement.service.UserService;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/")
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;

    @PutMapping("starttime/{time}")
    public Mono<ResultVO> putStartTime(@PathVariable String time, @RequestAttribute("uid") String uid) {
        LocalDateTime startTime = LocalDateTime.parse(time);
        return adminService.addStartTime(startTime, uid)
                .then(Mono.just(ResultVO.success(Map.of("startTime", startTime))));
    }

    @PostMapping("teachers")
    public Mono<ResultVO> postTeachers(@RequestBody List<User> users) {
        return adminService.addUsers(users, User.ROLE_TEACHER).thenReturn(ResultVO.success(Map.of()));
    }

    @PostMapping("students")
    public Mono<ResultVO> postStudents(@RequestBody List<User> users) {
        return adminService.addUsers(users, User.ROLE_STUDENT).thenReturn(ResultVO.success(Map.of()));
    }

    @PostMapping("students/projects")
    public Mono<ResultVO> postProjects(@RequestBody List<StudentDTO> studentDTOs) {
        return adminService.updateProjectTitles(studentDTOs).thenReturn(ResultVO.success(Map.of()));
    }

    @GetMapping("grouping")
    public Mono<ResultVO> getGroupInfo() {
        Map<String, Object> m = new HashMap<>();
        Mono<List<User>> studentsM = userService.listUsers(User.ROLE_STUDENT)
                .doOnSuccess(users -> m.put("students", users));
        Mono<List<User>> teachersM = userService.listUsers(User.ROLE_TEACHER)
                .doOnSuccess(users -> m.put("teachers", users));
        return Mono.when(studentsM, teachersM).thenReturn(ResultVO.success(m));
    }

    @PostMapping("grouping")
    public Mono<ResultVO> postgroupNumbers(@RequestBody List<StudentDTO> studentDTOs) {
        return adminService.updateStudentsGroup(studentDTOs).thenReturn(ResultVO.success(Map.of()));
    }

    @PostMapping("processes")
    public Mono<ResultVO> postProcess(@RequestBody Process process) {
        return adminService.addProcess(process)
                .flatMap(r -> userService.listProcesses())
                .map(processes -> ResultVO.success(Map.of("processes", processes)));
    }

    @GetMapping("processes")
    public Mono<ResultVO> getProcesses(){
        return adminService.listProcesses()
                .map(processes -> ResultVO.success(Map.of("processes", processes)));
    }

    @PutMapping("passwords/{number}")
    public Mono<ResultVO> putPassword(@PathVariable String number) {
        return adminService.updatePassword(number).thenReturn(ResultVO.success(Map.of()));
    }

    @PostMapping("resetdata")
    public Mono<ResultVO> postData() {
        return adminService.updateData().thenReturn(ResultVO.success(Map.of()));
    }
}
