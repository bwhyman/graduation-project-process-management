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
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    // 添加过程
    @PostMapping("processes")
    public Mono<ResultVO> postProcess(@RequestBody Process process,
                                      @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        process.setDepartmentId(depid);
        return teacherService.addProcess(process)
                .flatMap(r -> userService.listProcesses(depid))
                .map(processes -> ResultVO.success(Map.of("processes", processes)));
    }

    @DeleteMapping("processes/{pid}")
    public Mono<ResultVO> delProcess(@PathVariable String pid,
                                     @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.removeProcess(pid, depid)
                .flatMap(r -> userService.listProcesses(depid)
                        .map(processes -> ResultVO.success(Map.of("processes", processes))));
    }

    @PatchMapping("processes")
    public Mono<ResultVO> patchProcess(@RequestBody Process process,
                                       @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.updateProcess(process, depid)
                .flatMap(r -> userService.listProcesses(depid)
                        .map(processes -> ResultVO.success(Map.of("processes", processes))));
    }

    @GetMapping("{tid}/students")
    public Mono<ResultVO> getTeacherStudents(@PathVariable String tid,
                                             @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listStudents(tid, depid)
                .map(students -> ResultVO.success(Map.of("students", students)));
    }

    @GetMapping("students/group")
    public Mono<ResultVO> getGroupStudents(
            @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g,
            @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listUsers(User.ROLE_STUDENT, g, depid)
                .map(users -> ResultVO.success(Map.of("students", users)));
    }

    @GetMapping("students/tutor")
    public Mono<ResultVO> getTutorStudents(
            @RequestAttribute(RequestAttributeConstant.UID) String tid,
            @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listStudents(tid, depid)
                .map(users -> ResultVO.success(Map.of("students", users)));
    }

    @GetMapping("teachers/group")
    public Mono<ResultVO> getGroupTeachers(
            @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g,
            @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listUsers(User.ROLE_TEACHER, g, depid)
                .map(users -> ResultVO.success(Map.of("teachers", users)));
    }

    // 加载组指定过程评分
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

    private final DataBufferFactory factory = new DefaultDataBufferFactory();

    @GetMapping("download/{pname}")
    public Flux<DataBuffer> download(@PathVariable String pname, ServerHttpResponse response) throws IOException {
        Path path = Path.of(uploadDirectory).resolve(pname);
        String name = URLEncoder.encode(path.getFileName().toString(), StandardCharsets.UTF_8);
        HttpHeaders headers = response.getHeaders();
        headers.set("filename", name);
        headers.setContentLength(Files.size(path));
        headers.setContentDisposition(ContentDisposition.attachment().build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return DataBufferUtils.read(path, factory, 1024 * 8);
    }

    @GetMapping("students")
    public Mono<ResultVO> getStudents(@RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listUsers(User.ROLE_STUDENT, depid)
                .map(users -> ResultVO.success(Map.of("students", users)));
    }

    @GetMapping("teachers")
    public Mono<ResultVO> getTeachers(@RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listUsers(User.ROLE_TEACHER, depid)
                .map(users -> ResultVO.success(Map.of("teachers", users)));
    }

    // 加载全部学生评分
    @GetMapping("processscores")
    public Mono<ResultVO> getProcessScores() {
        return teacherService.listProcessScores()
                .map(processScores -> ResultVO.success(Map.of("processScores", processScores)));
    }

    // 统计小组内总成绩
    @GetMapping("processscores/groups")
    public Mono<ResultVO> getProcesses(@RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g) {
        return teacherService.listProcessScores(g)
                .map(processScores -> ResultVO.success(Map.of("processScores", processScores)));
    }

    // 重置密码
    @PutMapping("passwords/{number}")
    public Mono<ResultVO> putPassword(@PathVariable String number) {
        return teacherService.updatePassword(number)
                .map(num -> ResultVO.success(Map.of("number", num)));
    }

    @PostMapping("students")
    public Mono<ResultVO> postStudents(@RequestBody List<User> users,
                                       @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.addUsers(users, User.ROLE_STUDENT, depid)
                .flatMap(r -> userService.listUsers(User.ROLE_STUDENT, depid))
                .map(us -> ResultVO.success(Map.of("students", us)));
    }

    //
    @PatchMapping("students")
    public Mono<ResultVO> patchStudents(@RequestBody List<User> users,
                                        @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.updateStudents(users)
                .flatMap(r -> userService.listUsers(User.ROLE_STUDENT, depid))
                .map(us -> ResultVO.success(Map.of("students", us)));
    }
    //
    @GetMapping("users/{account}")
    public Mono<ResultVO> getStudent(@PathVariable String account,
                                     @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.getUser(account, depid)
                .map(student -> ResultVO.success(Map.of("student", student)));
    }
}
