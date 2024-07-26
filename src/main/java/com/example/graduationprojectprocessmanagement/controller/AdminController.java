package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.Department;
import com.example.graduationprojectprocessmanagement.service.AdminService;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("departments")
    public Mono<ResultVO> getDepartments() {
        return adminService.listDepartments()
                .map(departments -> ResultVO.success(Map.of("departments", departments)));
    }

    // 添加，并返回全部专业
    @PostMapping("departments")
    public Mono<ResultVO> postDepartment(@RequestBody Department department) {
        return adminService.addDepartment(department)
                .flatMap(r -> adminService.listDepartments())
                .map(departments -> ResultVO.success(Map.of("departments", departments)));
    }
    @DeleteMapping("departments/{did}")
    public Mono<ResultVO>delDepartment(@PathVariable String did) {
        return adminService.removeDepartment(did)
                .flatMap(r -> adminService.listDepartments())
                .map(departments -> ResultVO.success(Map.of("departments", departments)));
    }
}
