package com.example.graduationprojectprocessmanagement.exception;


import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XException extends RuntimeException{
    private Code code;
}
