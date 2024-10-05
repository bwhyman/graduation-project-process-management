package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.service.UserService;
import com.example.graduationprojectprocessmanagement.vo.RequestAttributeConstant;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
public class UserController {
    private final UserService userService;

    @PostMapping("passwords")
    public Mono<ResultVO> postPassword(@RequestBody User user, @RequestAttribute(RequestAttributeConstant.UID) String uid) {
        return userService.updatePassword(uid, user.getPassword())
                .thenReturn(ResultVO.ok());
    }

    @GetMapping("processes")
    public Mono<ResultVO> getProcesses(@RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listProcesses(depid)
                .map(ResultVO::success);
    }
}
