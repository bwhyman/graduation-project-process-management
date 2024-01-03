package com.example.graduationprojectprocessmanagement.repository;

import com.example.graduationprojectprocessmanagement.dox.Process;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProcessRepository extends ReactiveCrudRepository<Process, String> {

    @Query("select * from process p where p.student_attach is not null")
    Flux<Process> findStudentsProcesses();
}
