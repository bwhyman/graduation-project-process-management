package com.example.graduationprojectprocessmanagement.dox;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    public static final int ROLE_STUDENT = 1;
    public static final int ROLE_TEACHER = 5;
    public static final int ROLE_ADMIN = 10;
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
    @JsonIgnore
    private Integer role;
    private Integer groupNumber;
    @ReadOnlyProperty
    private LocalDateTime insertTime;
    @ReadOnlyProperty
    private LocalDateTime updateTime;
}
