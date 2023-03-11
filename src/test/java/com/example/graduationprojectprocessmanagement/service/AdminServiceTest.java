package com.example.graduationprojectprocessmanagement.service;

import com.example.graduationprojectprocessmanagement.dox.Process;
import com.example.graduationprojectprocessmanagement.dox.User;
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
    public void addTeachersTest() {
        String teacherJSON = """
                {"total": %s, "count": 0}
                """;
        User t1 = User.builder()
                .name("BO")
                .number("2001")
                .teacher(teacherJSON.formatted(10))
                .groupNumber(1)
                .build();
        User t2 = User.builder()
                .name("SUN")
                .number("2002")
                .teacher(teacherJSON.formatted(5))
                .groupNumber(2)
                .build();
        List<User> teacherDTOS = List.of(t1, t2);
        adminService.addUsers(teacherDTOS, User.ROLE_TEACHER).block();
    }

    @Test
    public void addStudentsTest() {
        User sd1 = User.builder()
                .name("wang-1")
                .number("202001")
                .build();

        User sd2 = User.builder()
                .name("wang-2")
                .number("202002")
                .build();
        User sd3 = User.builder()
                .name("zhang-1")
                .number("202003")
                .build();

        User sd4 = User.builder()
                .name("zhang-2")
                .number("202004")
                .build();
        adminService.addUsers(List.of(sd1, sd2, sd3, sd4), User.ROLE_STUDENT).block();
    }

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

    @Test
    public void addProcessTest() {
        String items = """
                [
                    {
                      "number": 0,
                      "name": "2.1",
                      "point": 50,
                      "description": "选题依据（选题意义，国内外动态，初步设想及创新点等）及可行性论述。"
                    },
                    {
                      "number": 1,
                      "name": "3.3",
                      "point": 50,
                      "description": "开题答辩过程中能否清楚陈述自己对毕业设计题目的深入理解，表达思路是否清晰，重点是否突出，能否正确回答与毕设工作相关的提问，开题报告结构组织是否清晰合理、行文语言是否流畅准确、撰写格式是否符合规范要求。"
                    }
                  ]
                """;
        Process process = Process.builder()
                .name("开题")
                .items(items)
                .auth(Process.REVIEW)
                .build();
        adminService.addProcess(process).block();
    }
}
