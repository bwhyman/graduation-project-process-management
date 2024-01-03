package com.example.graduationprojectprocessmanagement.repository;

import com.example.graduationprojectprocessmanagement.dox.ProcessFile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProcessFileRepository extends ReactiveCrudRepository<ProcessFile, String> {

    Mono<ProcessFile> findByProcessIdAndStudentIdAndNumber(String pid, String sid, int number);

    @Query("""
            select pf.id as id, pf.detail as detail, pf.student_id as student_id, pf.process_id as process_id,
            pf.number as number
            from process_file pf, user u
            where pf.student_id=u.id and u.group_number=:group and pf.process_id=:pid;
            """)
    Flux<ProcessFile> findByGroup(String pid, int group);

    @Query("""
            select pf.id as id, pf.detail as detail, pf.student_id as student_id, pf.process_id as process_id
            from process_file pf, user u
            where pf.student_id=u.id
            and pf.process_id=:pid
            and u.student ->> '$.teacherId'=:tid;
            """)
    Flux<ProcessFile> findByTeacher(String pid, String tid);

    @Query("select pf.id, pf.number from process_file pf where pf.student_id=:sid and pf.process_id=:pid")
    Flux<ProcessFile> findByStudentId(String pid, String sid);
}
