package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.exception.Code;
import com.example.graduationprojectprocessmanagement.exception.XException;
import com.example.graduationprojectprocessmanagement.service.SelectionService;
import com.example.graduationprojectprocessmanagement.service.StartTimeCache;
import com.example.graduationprojectprocessmanagement.service.UserService;
import com.example.graduationprojectprocessmanagement.vo.RequestAttributeConstant;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/student/")
public class SelectionController {
    private final UserService userService;
    private final SelectionService selectionService;
    private final StartTimeCache startTimeCache;

    @GetMapping("tutors")
    public Mono<ResultVO> getInfo(@RequestAttribute("uid") String uid) {
        Mono<User> studentM = userService.getUser(uid);
        Mono<List<User>> teachersM = userService.listUsers(User.ROLE_TEACHER);
        // 未开始
        if (LocalDateTime.now().isBefore(startTimeCache.getStartTime())) {
            return teachersM.map(teachers -> ResultVO.success(Code.NOT_START, Map.of("starttime", startTimeCache.getStartTime())));
        }
        return studentM.flatMap(user ->
                // 是否已经选择为json数据，不便于处理。只能判断长度
                user.getStudent() == null || user.getStudent().length() < 4
                        ? teachersM.map(teachers -> ResultVO.success(Map.of("teachers", teachers, "starttime", startTimeCache.getStartTime())))
                        : Mono.just(ResultVO.success(Map.of("starttime", startTimeCache.getStartTime())))
        );
    }

    // 选导师
    @PutMapping("tutors/{tid}")
    public Mono<ResultVO> putSelection(@PathVariable String tid, @RequestAttribute(RequestAttributeConstant.UID) String uid) {
        if (LocalDateTime.now().isBefore(startTimeCache.getStartTime())) {
            return Mono.just(ResultVO.error(Code.NOT_START));
        }
        return selectionService.addSelection(uid, tid)
                .map(student -> ResultVO.success(Map.of("user", student)))
                .onErrorResume(XException.class, x -> {
                    if (x.getCode() == Code.QUANTITY_FULL) {
                        return userService.listUsers(User.ROLE_TEACHER)
                                .map(teachers -> ResultVO.success(Code.QUANTITY_FULL, Map.of("teachers", teachers)));
                    }
                    return Mono.just(ResultVO.error(x.getCode()));
                });
    }
}
