package com.example.graduationprojectprocessmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessScoreDTO {
    private String studentId;
    private String processId;
    private Float score;
}
