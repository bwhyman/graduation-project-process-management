package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.dto.StudentDTO;
import com.example.graduationprojectprocessmanagement.repository.ProcessRepository;
import com.example.graduationprojectprocessmanagement.repository.UserRepository;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProcessRepository processRepository;
    private final ConnectionFactory connectionFactory;

    @Transactional
    public Mono<Void> addUsers(List<User> users, int role) {
        for (User user : users) {
            user.setPassword(passwordEncoder.encode(user.getNumber()));
            user.setRole(role);
        }
        return userRepository.saveAll(users).then();
    }

    @Transactional
    public Mono<Process> addProcess(Process process) {
        return processRepository.save(process);
    }

    /**
     * 参数，projectTitle, number
     *
     * @param studentDTOs
     * @return
     */
    @Transactional
    public Mono<Void> updateProjectTitles(List<StudentDTO> studentDTOs) {
        String sql = """
                update user u set u.student=json_set(u.student, '$.projectTitle', ?) where u.number=?
                """;
        return DatabaseClient.create(connectionFactory).sql(sql).filter(statement -> {
            for (StudentDTO s : studentDTOs) {
                statement.bind(0, s.getProjectTitle()).bind(1, s.getNumber()).add();
            }
            return statement;
        }).then();
    }

    /**
     * 参数，number, groupNumber, queueNumber
     *
     * @param studentDTOs
     * @return
     */
    @Transactional
    public Mono<Void> updateStudentsGroup(List<StudentDTO> studentDTOs) {
        String sql = """
                update user u set u.group_number=?, u.student=json_set(u.student, '$.queueNumber', ?) 
                where u.number=?
                """;
        return DatabaseClient.create(connectionFactory).sql(sql).filter(statement -> {
            for (StudentDTO s : studentDTOs) {
                statement.bind(0, s.getGroupNumber()).bind(1, s.getQueueNumber()).bind(2, s.getNumber()).add();
            }
            return statement;
        }).then();
    }

    @Transactional
    public Mono<Integer> updatePassword(String number) {
        return userRepository.updatePasswordByNumber(number, passwordEncoder.encode(number));
    }
}
