package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.dox.ProcessFile;
import com.example.graduationprojectprocessmanagement.exception.Code;
import com.example.graduationprojectprocessmanagement.exception.XException;
import com.example.graduationprojectprocessmanagement.service.StudentService;
import com.example.graduationprojectprocessmanagement.vo.RequestAttributeConstant;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/student/")
public class StudentController {
    private final StudentService studentService;

    @Value("${my.upload}")
    private String uploadDirectory;

    /*@GetMapping("tutors")
    public Mono<ResultVO> getInfo(@RequestAttribute(RequestAttributeConstant.UID) String uid) {
        Mono<User> studentM = userService.getUser(uid);
        Mono<List<User>> teachersM = userService.listUsers(User.ROLE_TEACHER);

        return userService.getStartTime()
                .flatMap(startTime -> {
                    // 未开始
                    if (LocalDateTime.now().isBefore(startTime)) {
                        return teachersM.map(teachers -> ResultVO.success(Code.NOT_START, Map.of("starttime", startTime)));
                    }
                    return studentM.flatMap(user ->
                            // 是否已经选择为json数据，不便于处理。只能判断长度
                            user.getStudent() == null || user.getStudent().length() < 4
                                    ? teachersM.map(teachers -> ResultVO.success(Map.of("teachers", teachers, "starttime", startTime)))
                                    : Mono.just(ResultVO.success(Map.of("starttime", startTime)))
                    );
                });
    }*/

    // 选导师
    /*@PutMapping("tutors/{tid}")
    public Mono<ResultVO> putSelection(@PathVariable String tid, @RequestAttribute(RequestAttributeConstant.UID) String uid) {
        return userService.getStartTime()
                .flatMap(startTime -> {
                    if (LocalDateTime.now().isBefore(startTime)) {
                        return Mono.just(ResultVO.error(Code.NOT_START));
                    }
                    return studentService.addSelection(uid, tid)
                            .map(student -> ResultVO.success(Map.of("user", student)))
                            .onErrorResume(XException.class, x -> {
                                if (x.getCode() == Code.QUANTITY_FULL) {
                                    return userService.listUsers(User.ROLE_TEACHER)
                                            .map(teachers -> ResultVO.success(Code.QUANTITY_FULL, Map.of("teachers", teachers)));
                                }
                                return Mono.just(ResultVO.error(x.getCode()));
                            });
                });
    }*/

    private MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw XException.builder().code(Code.BAD_REQUEST).build();
        }
    }

    @PostMapping(value = "upload/{pid}/numbers/{number}")
    public Mono<ResultVO> upload(@PathVariable String pid,
                                 @PathVariable int number,
                                 @RequestPart String pname,
                                 @RequestAttribute(RequestAttributeConstant.UID) String sid,
                                 @RequestHeader(required = false) String xtoken,
                                 Mono<FilePart> file) {
        if (xtoken == null) {
            return Mono.error(XException.builder().message("文件上传错误").build());
        }
        return file.flatMap(filePart -> {
                    String token = pid + pname + filePart.filename() + number;
                    byte[] encode = Base64.getEncoder().encode(URLEncoder.encode(token, Charset.defaultCharset()).getBytes());
                    var base64 = new String(encode).substring(0, 10);
                    if (!base64.equals(xtoken)) {
                        return Mono.error(XException.builder().message("文件上传错误").build());
                    }
                    ProcessFile pf = ProcessFile.builder()
                            .studentId(sid)
                            .processId(pid)
                            .number(number)
                            .detail(Path.of(pname).resolve(filePart.filename()).toString())
                            .build();
                    Path p = Path.of(uploadDirectory).resolve(pname);
                    return Mono.fromCallable(() -> Files.createDirectories(p))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(path -> {
                                Path finPath = path.resolve(filePart.filename());
                                return filePart.transferTo(finPath);
                            })
                            .then(Mono.defer(() -> studentService.addProcessFile(pf)));
                })
                .flatMap(pf -> studentService.listProcessFiles(pid, sid)
                        .map(processFiles -> ResultVO.success(Map.of("processfiles", processFiles))))
                .onErrorResume(ex -> Mono.just(ResultVO.error(400, "文件上传错误！" + ex.getMessage())));
    }

    @GetMapping("processfiles/{pid}")
    public Mono<ResultVO> getProcessFiles(@PathVariable String pid,
                                          @RequestAttribute(RequestAttributeConstant.UID) String sid) {
        return studentService.listProcessFiles(pid, sid)
                .map(processFiles -> ResultVO.success(Map.of("processfiles", processFiles)));
    }
}
