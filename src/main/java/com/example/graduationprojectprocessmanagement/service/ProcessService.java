package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.*;
import com.example.graduationprojectprocessmanagement.dox.Process;
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
public class ProcessService {
    private final ProcessScoreRepository processScoreRepository;
    private final ProcessRepository processRepository;

    public Mono<Process> getProcess(String processId) {
        return processRepository.findById(processId);
    }

    public Mono<List<Process>> listProcesses() {
        return processRepository.findAll().collectList();
    }

    public Mono<List<ProcessScore>> listProcessScores(int groupNumber, String processId) {
        return processScoreRepository.findByGroup(groupNumber, processId).collectList();
    }

    @Transactional
    public Mono<Integer> updateProcessScore(String pid, String sid, String tid, float score) {
        return processScoreRepository.findByProcessIdAndStudentId(pid, sid)
                .flatMap(psid ->
                        processScoreRepository.updateDetail(tid, psid, score).thenReturn(1)
                ).switchIfEmpty(Mono.defer(() -> {
                    String detail = """
                            {"%s": %s}
                            """;
                    ProcessScore pt = ProcessScore.builder()
                            .processId(pid)
                            .studentId(sid)
                            .detail(detail.formatted(tid, score))
                            .build();
                    return processScoreRepository.save(pt).thenReturn(1);
                }));
    }
}
