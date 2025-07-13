package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dox.ProcessFile;
import com.example.graduationprojectprocessmanagement.dox.ProcessScore;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.dto.StudentDTO;
import com.example.graduationprojectprocessmanagement.repository.ProcessFileRepository;
import com.example.graduationprojectprocessmanagement.repository.ProcessRepository;
import com.example.graduationprojectprocessmanagement.repository.ProcessScoreRepository;
import com.example.graduationprojectprocessmanagement.repository.UserRepository;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
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
    private final PasswordEncoder passwordEncoder;
    private final DatabaseClient client;

    @Transactional
    public Mono<Void> addProcess(Process process) {
        return processRepository.save(process).then();
    }

    @Transactional
    public Mono<Void> removeProcess(String pid, String depid) {
        return processRepository.deleteByIdAndDepartmentId(pid, depid).then();
    }

    @Transactional
    public Mono<Void> updateProcess(Process process, String depid) {
        return processRepository.findByIdAndDepartmentId(process.getId(), depid)
                .flatMap(p -> {
                    p.setAuth(process.getAuth());
                    p.setItems(process.getItems());
                    p.setPoint(process.getPoint());
                    p.setName(process.getName());
                    p.setStudentAttach(process.getStudentAttach());
                    return processRepository.save(p);
                }).then();
    }

    public Mono<List<ProcessScore>> listProcessScores(int groupNumber, String processId) {
        return processScoreRepository.findByGroupAndProcessId(groupNumber, processId).collectList();
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
    public Mono<Void> updateProcessScore(ProcessScore processScore) {

        return Mono.justOrEmpty(processScore.getId())
                .flatMap(id -> processScoreRepository.updateDetail(id, processScore.getDetail()))
                .switchIfEmpty(processScoreRepository.save(processScore).thenReturn(1))
                .then();
    }

    public Mono<List<ProcessScore>> listProcessScores(String depid) {
        return processScoreRepository.findByDepId(depid).collectList();
    }

    public Mono<List<ProcessScore>> listDepartProcessScores(int groupNumber, String depid) {
        return processScoreRepository.findByGroupAndDepId(groupNumber, depid).collectList();
    }

    @Transactional
    public Mono<Integer> updatePassword(String number) {
        return userRepository.updatePasswordByNumber(number, passwordEncoder.encode(number));
    }

    @Transactional
    public Mono<Void> addUsers(List<User> users, String role, String depid) {
        for (User user : users) {
            user.setDepartmentId(depid);
            user.setPassword(passwordEncoder.encode(user.getNumber()));
            user.setRole(role);
        }
        return userRepository.saveAll(users).then();
    }

    @Transactional
    public Mono<Void> updateStudentsGroups(List<StudentDTO> users) {
        var sql = "update user u set u.student=json_set(u.student, '$.queueNumber', ?), u.group_number=? where u.number=?";
        return client.inConnection(conn -> {
            Statement statement = conn.createStatement(sql);
            for (int i = 0; i < users.size(); i++) {
                statement.bind(0, users.get(i).getQueueNumber());
                statement.bind(1, users.get(i).getGroupNumber());
                statement.bind(2, users.get(i).getNumber());
                if (i < users.size() - 1) {
                    statement.add();
                }
            }
            return Flux.from(statement.execute()).collectList();
        }).then();
    }
    @Transactional
    public Mono<Void> updateStudentsTeachers(List<User> users) {
        var sql = "update user u set u.student=? where u.number=?";
        return client.inConnection(conn -> {
            Statement statement = conn.createStatement(sql);
            for (int i = 0; i < users.size(); i++) {
                statement.bind(0, users.get(i).getStudent());
                statement.bind(1, users.get(i).getNumber());
                if (i < users.size() - 1) {
                    statement.add();
                }
            }
            return Flux.from(statement.execute()).collectList();
        }).then();
    }

    public Mono<User> getUser(String account, String depid) {
        return userRepository.findByNumberAndDepartmentId(account, depid);
    }

    @Transactional
    public Mono<Integer> updateGroup(String number, int g, String depid) {
        return userRepository.updateGroup(number, g, depid);
    }

    @Transactional
    public Mono<Integer> updateStudent(User user, String depid) {
        return userRepository.findByNumberAndDepartmentId(user.getNumber(), depid)
                .flatMap(u -> {
                    if (user.getGroupNumber() != null) {
                        u.setGroupNumber(user.getGroupNumber());
                    }
                    if (user.getStudent() != null) {
                        u.setStudent(user.getStudent());
                    }
                    return userRepository.save(u);
                }).thenReturn(1);
    }

    public Mono<Void> updateProjects(List<StudentDTO> users) {
        var sql = "update user u set u.student=json_set(u.student, '$.projectTitle', ?) where u.number=?";
        return client.inConnection(conn -> {
            Statement statement = conn.createStatement(sql);
            for (int i = 0; i < users.size(); i++) {
                statement.bind(0, users.get(i).getProjectTitle());
                statement.bind(1, users.get(i).getNumber());
                if (i < users.size() - 1) {
                    statement.add();
                }
            }
            return Flux.from(statement.execute()).collectList();
        }).then();
    }
}
