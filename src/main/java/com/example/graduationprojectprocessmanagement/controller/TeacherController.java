package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dox.ProcessScore;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.service.TeacherService;
import com.example.graduationprojectprocessmanagement.service.UserService;
import com.example.graduationprojectprocessmanagement.vo.RequestAttributeConstant;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "items/studentAttach以json类型存储，前端需将JS对象显式序列化为字符串")
    @PostMapping("processes")
    public Mono<ResultVO> postProcess(@RequestBody Process process,
                                      @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        process.setDepartmentId(depid);
        return teacherService.addProcess(process)
                .then(userService.listProcesses(depid))
                .map(ResultVO::success);
    }

    @DeleteMapping("processes/{pid}")
    public Mono<ResultVO> delProcess(@PathVariable String pid,
                                     @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.removeProcess(pid, depid)
                .then(userService.listProcesses(depid))
                .map(ResultVO::success);
    }

    @PatchMapping("processes")
    public Mono<ResultVO> patchProcess(@RequestBody Process process,
                                       @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.updateProcess(process, depid)
                .then(userService.listProcesses(depid))
                .map(ResultVO::success);
    }

    @GetMapping("{tid}/students")
    public Mono<ResultVO> getTeacherStudents(@PathVariable String tid,
                                             @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listStudents(tid, depid)
                .map(ResultVO::success);
    }

    @GetMapping("students/group")
    public Mono<ResultVO> getGroupStudents(
            @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g,
            @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listUsers(User.ROLE_STUDENT, g, depid)
                .map(ResultVO::success);
    }

    @GetMapping("students/tutor")
    public Mono<ResultVO> getTutorStudents(
            @RequestAttribute(RequestAttributeConstant.UID) String tid,
            @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listStudents(tid, depid)
                .map(ResultVO::success);
    }

    @GetMapping("teachers/group")
    public Mono<ResultVO> getGroupTeachers(
            @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g,
            @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listUsers(User.ROLE_TEACHER, g, depid)
                .map(ResultVO::success);
    }

    @Operation(summary = "传递auth参数可避免检索process类型")
    // 加载组指定过程评分
    @GetMapping("processes/{pid}/types/{auth}")
    public Mono<ResultVO> getProcesScore(@PathVariable String pid, @PathVariable String auth,
                                         @RequestAttribute(RequestAttributeConstant.UID) String tid,
                                         @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g) {
        return Mono.defer(() -> auth.equals(Process.REVIEW)
                ? teacherService.listProcessScores(g, pid)
                : teacherService.listProcessScores(tid, pid))
                .map(ResultVO::success);
    }

    @Operation(summary = """
            评分后，需要基于auth参数决定返回小组/指导学生成绩；
            ProcessScore detail数据以json类型存储
            """)
    // 更新与添加
    @PostMapping("processscores/types/{auth}")
    public Mono<ResultVO> postProcess(
            @PathVariable String auth,
            @RequestBody ProcessScore ps,
            @RequestAttribute(RequestAttributeConstant.UID) String tid,
            @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g) {
        var pid = ps.getProcessId();
        return teacherService.updateProcessScore(ps)
                .then(Mono.defer(() -> auth.equals(Process.REVIEW)
                        ? teacherService.listProcessScores(g, pid)
                        : teacherService.listProcessScores(tid, pid)))
                .map(ResultVO::success);
    }

    @GetMapping("processfiles/{pid}/types/{auth}")
    public Mono<ResultVO> getProcessFiles(@PathVariable String pid,
                                          @PathVariable String auth,
                                          @RequestAttribute(RequestAttributeConstant.UID) String tid,
                                          @RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g) {
        return Mono.defer(() -> auth.equals(Process.REVIEW)
                ? teacherService.listProcessFiles(g, pid)
                : teacherService.listProcessFiles(tid, pid))
                .map(ResultVO::success);
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
                .map(ResultVO::success);
    }

    @GetMapping("teachers")
    public Mono<ResultVO> getTeachers(@RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return userService.listUsers(User.ROLE_TEACHER, depid)
                .map(ResultVO::success);
    }

    // 加载全部学生评分
    @GetMapping("processscores")
    public Mono<ResultVO> getProcessScores(@RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.listProcessScores(depid)
                .map(ResultVO::success);
    }

    // 统计小组内总成绩
    @GetMapping("processscores/groups")
    public Mono<ResultVO> getProcesses(@RequestAttribute(RequestAttributeConstant.GROUP_NUMBER) int g) {
        return teacherService.listProcessScores(g)
                .map(ResultVO::success);
    }

    // 重置密码
    @PutMapping("passwords/{number}")
    public Mono<ResultVO> putPassword(@PathVariable String number) {
        return teacherService.updatePassword(number)
                .map(ResultVO::success);
    }

    @PostMapping("students")
    public Mono<ResultVO> postStudents(@RequestBody List<User> users,
                                       @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.addUsers(users, User.ROLE_STUDENT, depid)
                .then(userService.listUsers(User.ROLE_STUDENT, depid))
                .map(ResultVO::success);
    }

    //
    @PatchMapping("students")
    public Mono<ResultVO> patchStudents(@RequestBody List<User> users,
                                        @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.updateStudents(users)
                .then(userService.listUsers(User.ROLE_STUDENT, depid))
                .map(ResultVO::success);
    }

    //
    @GetMapping("users/{account}")
    public Mono<ResultVO> getStudent(@PathVariable String account,
                                     @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.getUser(account, depid)
                .map(ResultVO::success);
    }

    @PutMapping("users/{account}/groups/{g}")
    public Mono<ResultVO> patchUserGroup(@PathVariable String account,
                                         @PathVariable int g,
                                         @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.updateGroup(account, g, depid)
                .thenReturn(ResultVO.ok());
    }

    //
    @PatchMapping("student")
    public Mono<ResultVO> patchStudent(@RequestBody User user,
                                       @RequestAttribute(RequestAttributeConstant.DEPARTMENT_ID) String depid) {
        return teacherService.updateStudent(user, depid)
                .thenReturn(ResultVO.ok());
    }
}
