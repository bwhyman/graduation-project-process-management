package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.component.JWTComponent;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.exception.Code;
import com.example.graduationprojectprocessmanagement.service.UserService;
import com.example.graduationprojectprocessmanagement.vo.RequestAttributeConstant;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/")
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final JWTComponent jwtComponent;

    @PostMapping("login")
    public Mono<ResultVO> login(@RequestBody User user, ServerHttpResponse response) {
        return userService.getUserByNumber(user.getNumber())
                .filter(u -> encoder.matches(user.getPassword(), u.getPassword()))
                .map(u -> {
                    Map<String, Object> tokenM = new HashMap<>();
                    tokenM.put(RequestAttributeConstant.UID, u.getId());
                    tokenM.put(RequestAttributeConstant.ROLE, u.getRole());
                    tokenM.put(RequestAttributeConstant.DEPARTMENT_ID, u.getDepartmentId());
                    if(u.getGroupNumber() != null) {
                        tokenM.put(RequestAttributeConstant.GROUP_NUMBER, u.getGroupNumber());
                    }
                    
                    String token = jwtComponent.encode(tokenM);
                    response.getHeaders().add(RequestAttributeConstant.TOKEN, token);
                    response.getHeaders().add(RequestAttributeConstant.ROLE, u.getRole());
                    return ResultVO.success(u);
                })
                .defaultIfEmpty(ResultVO.error(Code.LOGIN_ERROR));
    }
}
