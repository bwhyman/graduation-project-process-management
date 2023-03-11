package com.example.graduationprojectprocessmanagement.repository;

import com.example.graduationprojectprocessmanagement.dox.ProcessScore;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProcessScoreRepository extends ReactiveCrudRepository<ProcessScore, String> {

    @Query("""
            select * from process_score ps, user u
            where ps.process_id=:processId and u.group_number=:groupNumber and u.id=ps.student_id
            """)
    Flux<ProcessScore> findByGroupNumberAndProcessId(int groupNumber, String processId);

    @Modifying
    @Query("""
            update process_score ps set ps.detail=:detail where ps.id=:psid
            """)
    Mono<Integer> updateDetail(String psid, String detail);

    Flux<ProcessScore> findByProcessIdAndTeacherId(String pid, String tid);
}
