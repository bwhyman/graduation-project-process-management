package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.exception.Code;
import com.example.graduationprojectprocessmanagement.exception.XException;
import com.example.graduationprojectprocessmanagement.service.SelectionService;
import com.example.graduationprojectprocessmanagement.service.UserService;
import com.example.graduationprojectprocessmanagement.vo.RequestAttributeConstant;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/student/")
public class SelectionController {
    private final UserService userService;
    private final SelectionService selectionService;

    // 加载全部教师信息
    @GetMapping("teachers")
    public Mono<ResultVO> getTeachers() {
        return userService.listUsers(User.ROLE_TEACHER)
                .map(teachers -> ResultVO.success(Map.of("teachers", teachers)));
    }

    // 选导师
    @PutMapping("teachers/{tid}")
    public Mono<ResultVO> putSelection(@PathVariable String tid, @RequestAttribute(RequestAttributeConstant.UID) String uid) {
        return selectionService.addSelection(uid, tid)
                .map(student -> ResultVO.success(Map.of("teacher", student.getStudent())))
                .onErrorResume(XException.class, x -> {
                    if (x.getCode() == Code.QUANTITY_FULL) {
                        return userService.listUsers(User.ROLE_TEACHER)
                                .map(teachers -> ResultVO.success(Code.QUANTITY_FULL, Map.of("teachers", teachers)));
                    }
                    return Mono.just(ResultVO.error(x.getCode()));
                });
    }
}
