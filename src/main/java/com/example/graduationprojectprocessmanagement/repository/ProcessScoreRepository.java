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
            select ps.id as id,
                ps.student_id as student_id,
                ps.process_id as process_id,
                ps.teacher_id as teacher_id,
                ps.detail as detail
            from process_score ps left join user u
            on ps.student_id=u.id
            where u.group_number=:groupNumber and ps.process_id=:pid;
            """)
    Flux<ProcessScore> findByGroupAndProcessId(int groupNumber, String pid);

    @Query("""
            select ps.id as id,
                ps.student_id as student_id,
                ps.process_id as process_id,
                ps.detail as detail,
                ps.teacher_id as teacher_id
            from user u, process_score ps
            where u.id=ps.student_id
            and ps.process_id = :pid
            and u.student ->> '$.teacherId'=:tid;
            """)
    Flux<ProcessScore> findByTeacher(String tid, String pid);

    @Modifying
    @Query("""
            update process_score ps set ps.detail=:detail where ps.id=:psid
            """)
    Mono<Integer> updateDetail(String psid, String detail);

    @Query("""
            select ps.id as id,
                ps.student_id as student_id,
                ps.process_id as process_id,
                ps.teacher_id as teacher_id,
                ps.detail as detail
            from process_score ps, user u
            where ps.student_id=u.id and u.group_number=:groupNumber and u.department_id=:depid;
            """)
    Flux<ProcessScore> findByGroupAndDepId(int groupNumber, String depid);

    @Query("""
            select * from process_score ps, process p
            where ps.process_id=p.id and p.department_id=:depid
            """)
    Flux<ProcessScore> findByDepId(String depid);
}
