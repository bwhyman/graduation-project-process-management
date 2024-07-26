package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dto.StudentDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class AdminServiceTest {
    @Autowired
    private AdminService adminService;

    @Test
    public void updateProjectTitleTest() {
        StudentDTO sd1 = StudentDTO.builder()
                .projectTitle("ProjectTitle-1")
                .number("202001")
                .build();

        StudentDTO sd2 = StudentDTO.builder()
                .projectTitle("ProjectTitle-2")
                .number("202002")
                .build();
        StudentDTO sd3 = StudentDTO.builder()
                .projectTitle("ProjectTitle-3")
                .number("202003")
                .build();

        StudentDTO sd4 = StudentDTO.builder()
                .projectTitle("ProjectTitle-4")
                .number("202004")
                .build();
        adminService.updateProjectTitles(List.of(sd1, sd2, sd3, sd4)).block();
    }

    @Test
    public void updateStudentsGroupAndQueueTest() {
        StudentDTO sd1 = StudentDTO.builder()
                .groupNumber(2)
                .queueNumber(21)
                .number("202001")
                .build();

        StudentDTO sd2 = StudentDTO.builder()
                .groupNumber(2)
                .queueNumber(7)
                .number("202002")
                .build();
        StudentDTO sd3 = StudentDTO.builder()
                .groupNumber(1)
                .queueNumber(14)
                .number("202003")
                .build();

        StudentDTO sd4 = StudentDTO.builder()
                .groupNumber(1)
                .queueNumber(5)
                .number("202004")
                .build();
        adminService.updateStudentsGroup(List.of(sd1, sd2, sd3, sd4)).block();
    }
}
