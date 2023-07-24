package com.example.graduationprojectprocessmanagement.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
@Slf4j
public class StartTimeCache {
    private LocalDateTime startTime;

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
