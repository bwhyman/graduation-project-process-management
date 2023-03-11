package com.example.graduationprojectprocessmanagement.exception;


import lombok.Getter;

@Getter
public class XException extends RuntimeException{
    private final Code code;

    public XException(Code code) {
        this.code = code;
    }
}
