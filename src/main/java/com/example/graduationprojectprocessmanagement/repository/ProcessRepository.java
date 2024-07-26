package com.example.graduationprojectprocessmanagement.repository;

import com.example.graduationprojectprocessmanagement.dox.Process;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProcessRepository extends ReactiveCrudRepository<Process, String> {
    Flux<Process> findByDepartmentId(String depid);

    Mono<Process> findByIdAndDepartmentId(String id, String depid);

    @Modifying
    Mono<Integer> deleteByIdAndDepartmentId(String id, String depid);
}
