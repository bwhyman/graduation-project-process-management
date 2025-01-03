package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.Department;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.service.AdminService;
import com.example.graduationprojectprocessmanagement.service.TeacherService;
import com.example.graduationprojectprocessmanagement.service.UserService;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/")
public class AdminController {
    private final AdminService adminService;
    private final TeacherService teacherService;
    private final UserService userService;

    @PostMapping("teachers/{depid}")
    public Mono<ResultVO> postTeachers(@RequestBody List<User> users,
                                       @PathVariable String depid) {
        return teacherService.addUsers(users, User.ROLE_TEACHER, depid)
                .then(userService.listUsers(User.ROLE_TEACHER, depid))
                .map(ResultVO::success);
    }

    @GetMapping("departments")
    public Mono<ResultVO> getDepartments() {
        return adminService.listDepartments()
                .map(ResultVO::success);
    }

    // 添加，并返回全部专业
    @PostMapping("departments")
    public Mono<ResultVO> postDepartment(@RequestBody Department department) {
        return adminService.addDepartment(department)
                .then(adminService.listDepartments())
                .map(ResultVO::success);
    }
    @DeleteMapping("departments/{did}")
    public Mono<ResultVO>delDepartment(@PathVariable String did) {
        return adminService.removeDepartment(did)
                .then(adminService.listDepartments())
                .map(ResultVO::success);
    }
}
