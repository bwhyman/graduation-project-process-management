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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherService {
    private final ProcessScoreRepository processScoreRepository;
    private final ProcessRepository processRepository;
    private final ProcessFileRepository processFileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    //@CacheEvict(value = "processes", allEntries = true)
    public Mono<Process> addProcess(Process process) {
        return processRepository.save(process);
    }

    @Transactional
    public Mono<Integer> removeProcess(String pid, String depid) {
        return processRepository.deleteByIdAndDepartmentId(pid, depid).thenReturn(1);
    }

    @Transactional
    public Mono<Integer> updateProcess(Process process, String depid) {
        return processRepository.findByIdAndDepartmentId(process.getId(), depid)
                .flatMap(p -> {
                    p.setAuth(process.getAuth());
                    p.setItems(process.getItems());
                    p.setPoint(process.getPoint());
                    p.setName(process.getName());
                    p.setStudentAttach(process.getStudentAttach());
                    return processRepository.save(p);
                }).thenReturn(1);
    }

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
    public Mono<List<ProcessScore>> listProcessScores(int groupNumber) {
        return processScoreRepository.findByGroup(groupNumber).collectList();
    }

    @Transactional
    public Mono<Integer> updatePassword(String number) {
        return userRepository.updatePasswordByNumber(number, passwordEncoder.encode(number));
    }

    @Transactional
    public Mono<Integer> addUsers(List<User> users, String role, String depid) {
        for (User user : users) {
            user.setDepartmentId(depid);
            user.setPassword(passwordEncoder.encode(user.getNumber()));
            user.setRole(role);
        }
        return userRepository.saveAll(users).then(Mono.just(1));
    }

    @Transactional
    public Mono<Integer> updateStudents(List<User> users) {
        List<Mono<User>> list = new ArrayList<>();
        for (User user : users) {
            Mono<User> byNumber = userRepository.findByNumber(user.getNumber()).flatMap(u -> {
                if(user.getGroupNumber() != null) {
                    u.setGroupNumber(user.getGroupNumber());
                }
                if(user.getStudent() != null) {
                    u.setStudent(user.getStudent());
                }
                return userRepository.save(u);
            });
            list.add(byNumber);
        }
        return Flux.merge(list).collectList().thenReturn(1);
    }

    public Mono<User> getUser(String account, String depid) {
        return userRepository.findByNumberAndDepartmentId(account, depid);
    }

    @Transactional
    //@CacheEvict(value = "groupusers", allEntries = true)
    public Mono<Integer> updateGroup(String number, int g, String depid) {
        return userRepository.updateGroup(number, g, depid);
    }
    @Transactional
    public Mono<Integer> updateStudent(User user, String depid) {
        return userRepository.findByNumberAndDepartmentId(user.getNumber(), depid)
                .flatMap(u -> {
                    if(user.getGroupNumber() != null) {
                        u.setGroupNumber(user.getGroupNumber());
                    }
                    if (user.getStudent() != null) {
                        u.setStudent(user.getStudent());
                    }
                    return userRepository.save(u);
                }).thenReturn(1);
    }
}
