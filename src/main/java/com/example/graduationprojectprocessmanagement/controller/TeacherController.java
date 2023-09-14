package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dox.ProcessScore;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.service.TeacherService;
import com.example.graduationprojectprocessmanagement.service.UserService;
import com.example.graduationprojectprocessmanagement.vo.RequestAttributeConstant;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/teacher/")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    private final UserService userService;
    @Value("${my.upload}")
    private String uploadDirectory;

    @GetMapping("{tid}/students")
    public Mono<ResultVO> getStudents(@PathVariable String tid) {
        return userService.listStudents(tid)
                .map(students -> ResultVO.success(Map.of("students", students)));
    }

    @GetMapping("/infos")
    public Mono<ResultVO> getGroupStudents(@RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g,
                                           @RequestAttribute(RequestAttributeConstant.UID) String tid) {
        Map<String, Object> m = new HashMap<>();
        Mono<List<User>> sM = userService.listUsers(User.ROLE_STUDENT, g)
                .doOnSuccess(users -> m.put("students", users));
        Mono<List<User>> tM = userService.listUsers(User.ROLE_TEACHER, g)
                .doOnSuccess(users -> m.put("teachers", users));
        Mono<List<User>> tuM = userService.listStudents(tid)
                .doOnSuccess(users -> m.put("tutorStudents", users));
        return Mono.when(sM, tM, tuM).thenReturn(ResultVO.success(m));
    }

    @GetMapping("processes/{pid}/types/{auth}")
    public Mono<ResultVO> getProcesScore(@PathVariable String pid, @PathVariable String auth,
                                         @RequestAttribute(RequestAttributeConstant.UID) String tid,
                                         @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g) {
        if (auth.equals(Process.REVIEW)) {
            return teacherService.listProcessScores(g, pid)
                    .map(processScores -> ResultVO.success(Map.of("processScores", processScores)));
        }
        return teacherService.listProcessScores(tid, pid)
                .map(processScores -> ResultVO.success(Map.of("processScores", processScores)));
    }

    // 更新与添加
    @PostMapping("processscores/types/{auth}")
    public Mono<ResultVO> postProcess(
            @PathVariable String auth,
            @RequestBody ProcessScore ps,
            @RequestAttribute(RequestAttributeConstant.UID) String tid,
            @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g) {
        return teacherService.updateProcessScore(ps)
                .then(Mono.defer(() -> {
                    if (auth.equals(Process.REVIEW)) {
                        return teacherService.listProcessScores(g, ps.getProcessId())
                                .map(processScores -> ResultVO.success(Map.of("processScores", processScores)));
                    }
                    return teacherService.listProcessScores(tid, ps.getProcessId())
                            .map(processScores -> ResultVO.success(Map.of("processScores", processScores)));
                }));
    }

    @GetMapping("processfiles/{pid}/types/{auth}")
    public Mono<ResultVO> getProcessFiles(@PathVariable String pid,
                                          @PathVariable String auth,
                                          @RequestAttribute(RequestAttributeConstant.UID) String tid,
                                          @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g) {
        if (auth.equals(Process.REVIEW)) {
            return teacherService.listProcessFiles(g, pid)
                    .map(pf -> ResultVO.success(Map.of("processFiles", pf)));
        }
        return teacherService.listProcessFiles(tid, pid)
                .map(pf -> ResultVO.success(Map.of("processFiles", pf)));
    }

    @GetMapping("download/{pname}")
    public Mono<Void> download(@PathVariable String pname, ServerHttpResponse response) {
        Path path = Path.of(uploadDirectory).resolve(pname);
        Flux<DataBuffer> buffer = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 1024 * 1000);
        String name = URLEncoder.encode(path.getFileName().toString(), StandardCharsets.UTF_8);
        HttpHeaders headers = response.getHeaders();
        headers.set("filename", name);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", name);
        return response.writeWith(buffer);
    }

    @GetMapping("unselected")
    public Mono<ResultVO> getUnselected() {
        return teacherService.listUnSelectedStudents()
                .map(users -> ResultVO.success(Map.of("students", users)));
    }

    @GetMapping("students")
    public Mono<ResultVO> getStudents() {
        return userService.listUsers(User.ROLE_STUDENT)
                .map(users -> ResultVO.success(Map.of("students", users)));
    }

    @GetMapping("teachers")
    public Mono<ResultVO> getTeachers() {
        return userService.listUsers(User.ROLE_TEACHER)
                .map(users -> ResultVO.success(Map.of("teachers", users)));
    }
    @GetMapping("processscores")
    public Mono<ResultVO> getProcessScores() {
        return teacherService.listProcessScores()
                .map(processScores -> ResultVO.success(Map.of("processScores", processScores)));
    }
}
