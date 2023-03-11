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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
                    Map<String, Object> tokenM = u.getGroupNumber() != null ?
                            Map.of(RequestAttributeConstant.UID, u.getId(),
                                    RequestAttributeConstant.ROLE, u.getRole(),
                                    RequestAttributeConstant.GROUP_NUMBER, u.getGroupNumber())
                            : Map.of(RequestAttributeConstant.UID, u.getId(),
                            RequestAttributeConstant.ROLE, u.getRole());
                    String token = jwtComponent.encode(tokenM);
                    response.getHeaders().add(RequestAttributeConstant.TOKEN, token);

                    String role = switch (u.getRole()) {
                        case User.ROLE_STUDENT -> "Yo87M";
                        case User.ROLE_TEACHER -> "nU0vt";
                        case User.ROLE_ADMIN -> "ppYMg";
                        default -> "";
                    };
                    response.getHeaders().add(RequestAttributeConstant.ROLE, role);
                    return ResultVO.success(Map.of());
                })
                .defaultIfEmpty(ResultVO.error(Code.LOGIN_ERROR));
    }

    @GetMapping("info")
    public Mono<ResultVO> getInfo(@RequestAttribute(RequestAttributeConstant.UID) String uid) {
        return userService.getUser(uid).map(user -> ResultVO.success(Map.of("user", user)));
    }
}
