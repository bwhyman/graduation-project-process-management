package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dox.ProcessFile;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.exception.Code;
import com.example.graduationprojectprocessmanagement.exception.XException;
import com.example.graduationprojectprocessmanagement.repository.ProcessFileRepository;
import com.example.graduationprojectprocessmanagement.repository.ProcessRepository;
import com.example.graduationprojectprocessmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {
    private final UserRepository userRepository;
    private final ProcessRepository processRepository;
    private final ProcessFileRepository processFileRepository;

    public Mono<Process> getProcess(String pid) {
        return processRepository.findById(pid);
    }

    @Transactional
    public Mono<User> addSelection(String sid, String tid) {
        Mono<Integer> resultM = userRepository.updateCount(tid);
        Mono<User> studentM = userRepository.findById(sid);
        Mono<User> teacherM = userRepository.findById(tid);
        return resultM.filter(r -> r != 0)
                .switchIfEmpty(Mono.error(XException.builder().code(Code.QUANTITY_FULL).build()))
                .flatMap(r -> studentM.filter(student -> student.getStudent() == null)
                        .switchIfEmpty(Mono.error(XException.builder().code(Code.REPEAT_SELECTION).build()))
                        .flatMap(student -> teacherM.flatMap(teacher -> {
                            String teacherJSON = """
                                    {"teacherId": "%s", "teacherName": "%s"}
                                    """;
                            student.setStudent(teacherJSON.formatted(teacher.getId(), teacher.getName()));
                            return userRepository.save(student);
                        })));
    }

    @Transactional
    public Mono<ProcessFile> addProcessFile(ProcessFile processFile) {
        return processFileRepository.findByProcessIdAndStudentIdAndNumber(processFile.getProcessId(), processFile.getStudentId(), processFile.getNumber())
                .flatMap(p -> {
                    p.setDetail(processFile.getDetail());
                    return processFileRepository.save(p);
                })
                .switchIfEmpty(Mono.defer(() -> processFileRepository.save(processFile)));
    }

    public Mono<List<Process>> listProcesses() {
        return processRepository.findStudentsProcesses().collectList();
    }

    public Mono<List<ProcessFile>> listProcessFiles(String pid, String sid) {
        return processFileRepository.findByStudentId(pid, sid).collectList();
    }
}
