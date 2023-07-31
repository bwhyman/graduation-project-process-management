package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.*;
import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dto.ProcessScoreDTO;
import com.example.graduationprojectprocessmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherService {
    private final ProcessScoreRepository processScoreRepository;
    private final ProcessRepository processRepository;
    private final ProcessFileRepository processFileRepository;
    private final UserRepository userRepository;

    public Mono<Process> getProcess(String processId) {
        return processRepository.findById(processId);
    }

    public Mono<List<ProcessScore>> listProcessScores(int groupNumber, String processId) {
        return processScoreRepository.findByGroup(groupNumber, processId).collectList();
    }
    public Mono<List<ProcessScore>> listProcessScores(String tid, String processId) {
        return processScoreRepository.findByTeacher(tid, processId).collectList();
    }

    @Transactional
    public Mono<Integer> updateProcessScore(ProcessScoreDTO p, String tid) {
        return processScoreRepository.findByProcessIdAndStudentId(p.getProcessId(), p.getStudentId())
                .flatMap(psid -> processScoreRepository.updateDetail(tid, psid, p.getTeacherName(),p.getScore()).thenReturn(1))
                .switchIfEmpty(Mono.defer(() -> {
                    String detail = """
                            [{"teacherId": "%s", "teacherName": "%S","score": %s}]
                            """;
                    ProcessScore pt = ProcessScore.builder()
                            .processId(p.getProcessId())
                            .studentId(p.getStudentId())
                            .detail(detail.formatted(tid, p.getTeacherName(), p.getScore()))
                            .build();
                    return processScoreRepository.save(pt).thenReturn(1);
                }));
    }

    public Mono<List<ProcessFile>> listProcessFiles(int groupNumber, String processId) {
        return processFileRepository.findByGroup(processId, groupNumber).collectList();
    }
    public Mono<List<ProcessFile>> listProcessFiles(String tid, String processId) {
        return processFileRepository.findByTeacher(processId, tid).collectList();
    }

    public Mono<List<User>> listUnSelectedStudents() {
        return userRepository.findAllUnSelected().collectList();
    }
}
