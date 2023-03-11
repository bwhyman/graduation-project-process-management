package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.exception.Code;
import com.example.graduationprojectprocessmanagement.exception.XException;
import com.example.graduationprojectprocessmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class SelectionService {
    private final UserRepository userRepository;

    @Transactional
    public Mono<User> addSelection(String sid, String tid) {
        Mono<Integer> resultM = userRepository.updateCount(tid);
        Mono<User> studentM = userRepository.findById(sid);
        Mono<User> teacherM = userRepository.findById(tid);
        return resultM.filter(r -> r != 0)
                .switchIfEmpty(Mono.error(new XException(Code.QUANTITY_FULL)))
                .flatMap(r -> studentM.filter(student -> student.getStudent() == null)
                        .switchIfEmpty(Mono.error(new XException(Code.REPEAT_SELECTION)))
                        .flatMap(student -> teacherM.flatMap(teacher -> {
                            String teacherJSON = """
                                    {"teacherId": "%s", "teacherName": "%s"}
                                    """;
                            student.setStudent(teacherJSON.formatted(teacher.getId(), teacher.getName()));
                            return userRepository.save(student);
                        })));
    }
}
