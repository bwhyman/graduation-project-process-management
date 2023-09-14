package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dox.ProcessFile;
import com.example.graduationprojectprocessmanagement.dox.ProcessScore;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.repository.ProcessFileRepository;
import com.example.graduationprojectprocessmanagement.repository.ProcessRepository;
import com.example.graduationprojectprocessmanagement.repository.ProcessScoreRepository;
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

    public Mono<List<ProcessFile>> listProcessFiles(int groupNumber, String processId) {
        return processFileRepository.findByGroup(processId, groupNumber).collectList();
    }

    public Mono<List<ProcessFile>> listProcessFiles(String tid, String processId) {
        return processFileRepository.findByTeacher(processId, tid).collectList();
    }

    public Mono<List<User>> listUnSelectedStudents() {
        return userRepository.findAllUnSelected().collectList();
    }

    @Transactional
    public Mono<Integer> updateProcessScore(ProcessScore processScore) {
        if (processScore.getId() != null) {
            return processScoreRepository.updateDetail(processScore.getId(), processScore.getDetail());
        }
        return processScoreRepository.save(processScore).thenReturn(1);
    }

    public Mono<List<ProcessScore>> listProcessScores() {
        return processScoreRepository.findAll().collectList();
    }
}
