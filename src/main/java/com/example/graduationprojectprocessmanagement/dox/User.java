package com.example.graduationprojectprocessmanagement.dox;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    public static final String ROLE_STUDENT = "qpCf";
    public static final String ROLE_TEACHER = "kU4T";
    public static final String ROLE_ADMIN = "R2md";
    @Id
    @CreatedBy
    private String id;
    private String name;
    private String number;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String student;
    private String teacher;
    private String description;
    private String departmentId;
    private String role;
    private Integer groupNumber;
    @ReadOnlyProperty
    private LocalDateTime insertTime;
    @ReadOnlyProperty
    private LocalDateTime updateTime;
}
