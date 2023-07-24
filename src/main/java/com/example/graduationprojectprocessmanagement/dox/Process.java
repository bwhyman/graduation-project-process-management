package com.example.graduationprojectprocessmanagement.dox;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Process {
    public static final int TUTOR = 1;
    public static final int REVIEW = 2;
    @Id
    @CreatedBy
    private String id;
    private String name;
    // json
    private String items;
    private int point;
    private Integer auth;
    @ReadOnlyProperty
    @JsonIgnore
    private LocalDateTime insertTime;
    @ReadOnlyProperty
    @JsonIgnore
    private LocalDateTime updateTime;

}
