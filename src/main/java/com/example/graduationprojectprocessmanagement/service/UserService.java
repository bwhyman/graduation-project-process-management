package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.repository.ProcessRepository;
import com.example.graduationprojectprocessmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProcessRepository processRepository;

    public Mono<User> getUserByNumber(String number) {
        return userRepository.findByNumber(number);
    }

    public Mono<User> getUser(String uid) {
        return userRepository.findById(uid);
    }
    public Mono<List<User>> listStudents(String tid) {
        return userRepository.findStudentByTeacherId(tid).collectList();
    }

    public Mono<List<User>> listUsers(int role) {
        return userRepository.findByRoleOrderById(role).collectList();
    }

    //@Cacheable(value = "groupusers", key = "{#role-#groupNumber}")
    public Mono<List<User>> listUsers(int role, int groupNumber) {
        return userRepository.findByRoleAndGroupNumber(role, groupNumber).collectList().cache();
    }
    @Transactional
    public Mono<Integer> updatePassword(String uid, String password) {
        return userRepository.updatePasswordById(uid, passwordEncoder.encode(password));
    }
    //@Cacheable("processes")
    public Mono<List<Process>> listProcesses() {
        return processRepository.findAll().collectList().cache();
    }

    @Cacheable("starttime")
    public Mono<LocalDateTime> getStartTime() {
        return userRepository.findStartTime()
                .map(LocalDateTime::parse).cache();
    }
}
