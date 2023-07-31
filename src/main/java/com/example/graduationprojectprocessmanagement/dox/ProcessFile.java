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
public class ProcessFile {
    @Id
    @CreatedBy
    private String id;
    private String detail;
    private String studentId;
    private String processId;
    @ReadOnlyProperty
    @JsonIgnore
    private LocalDateTime insertTime;
    @ReadOnlyProperty
    @JsonIgnore
    private LocalDateTime updateTime;
}
