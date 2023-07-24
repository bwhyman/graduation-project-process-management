package com.example.graduationprojectprocessmanagement.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByNumberTest() {
        userRepository.findByNumber("admin").doOnSuccess(user -> log.debug(user.toString())).block();
    }
}
