package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.*;
import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        return processScoreRepository.findByGroupNumberAndProcessId(groupNumber, processId).collectList();
    }

    public Mono<ProcessScore> addProcessScore(ProcessScore processScore) {
        return processScoreRepository.save(processScore);
    }

    public Mono<List<ProcessScore>> listProcessScores(String pid, String tid) {
        return processScoreRepository.findByProcessIdAndTeacherId(pid, tid).collectList();
    }

    public Mono<Integer> updateProcessScore(String psid, String detail) {
        return processScoreRepository.updateDetail(psid, detail);
    }

}
