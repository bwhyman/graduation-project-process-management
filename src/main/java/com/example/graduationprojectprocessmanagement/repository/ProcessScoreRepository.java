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
                ps.detail as detail
            from process_score ps, user u
            where ps.student_id=u.id and u.group_number=:groupNumber and ps.process_id=:pid;
            """)
    Flux<ProcessScore> findByGroup(int groupNumber, String pid);

    @Query("""
            select ps.id as id,
                ps.student_id as student_id,
                ps.process_id as process_id,
                ps.detail as detail
            from user u, process_score ps
            where u.id=ps.student_id
            and ps.process_id = :pid
            and u.student ->> '$.teacherId'=:tid;
            """)
    Flux<ProcessScore> findByTeacher(String tid, String pid);

    @Query("""
            select ps.id from process_score ps where ps.process_id=:pid and ps.student_id=:sid;
            """)
    Mono<String> findByProcessIdAndStudentId(String pid, String sid);

    @Modifying
    @Query("""
            update process_score ps
                join JSON_TABLE(
                        ps.detail,
                        '$[*]'
                        columns (
                            `id` for ordinality,
                            `tid` char(19) path '$.teacherId'
                            )
                    ) jt
            set ps.detail=json_set(
                    ps.detail,
                    if(isnull(json_search(ps.detail, 'one', :tid)),
                       concat('$[', json_length(ps.detail), ']'),
                       json_unquote(replace(json_search(ps.detail, 'one', :tid, null, '$**.teacherId'), '.teacherId', ''))),
                    json_object('teacherId', :tid, 'teacherName', :tname,'score', :score)
                )
            where ps.id = :psid;
            """)
    Mono<Integer> updateDetail(String tid, String psid, String tname, float score);
}
