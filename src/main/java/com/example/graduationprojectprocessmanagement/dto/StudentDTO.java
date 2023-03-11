package com.example.graduationprojectprocessmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDTO {
    private String number;
    private Integer groupNumber;
    private Integer queueNumber;
    private String projectTitle;
}
