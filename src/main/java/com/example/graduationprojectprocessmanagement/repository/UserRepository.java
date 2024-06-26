package com.example.graduationprojectprocessmanagement.repository;

import com.example.graduationprojectprocessmanagement.dox.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Mono<User> findByNumber(String number);

    @Modifying
    @Query("update user u set u.description=:time where u.number='admin'")
    Mono<Integer> updateStartTime(String time);

    @Modifying
    @Query("""
            update user u set u.teacher=json_set(u.teacher, '$.count', cast(u.teacher -> '$.count' + 1 as unsigned))
            where u.id=:tid and (u.teacher -> '$.total' - u.teacher -> '$.count' > 0);
            """)
    Mono<Integer> updateCount(String tid);

    Flux<User> findByRoleOrderById(int role);

    @Query("""
            select * from user u where u.role=:role and u.group_number=:groupNumber
            order by u.student -> '$.queueNumber'
            """)
    Flux<User> findByRoleAndGroupNumber(int role, int groupNumber);

    @Query("""
            select * from user u where u.student->>'$.teacherId'=:tid
            """)
    Flux<User> findStudentByTeacherId(String tid);

    @Modifying
    @Query("""
            update user u set u.password=:password where u.id=:uid;
            """)
    Mono<Integer> updatePasswordById(String uid, String password);

    @Modifying
    @Query("""
            update user u set u.password=:password where u.number=:number;
            """)
    Mono<Integer> updatePasswordByNumber(String number, String password);

    @Query("""
            select * from user u where u.student is null and role=1;
            """)
    Flux<User> findAllUnSelected();

    @Modifying
    @Query("""
            update user u set u.student=null where u.role=1;
            """)
    Mono<Integer> updateStudentData();

    @Modifying
    @Query("""
            update user u set u.teacher=json_set(u.teacher, '$.count', 0) where u.role=5;
            """)
    Mono<Integer> updateTeacherData();

    @Modifying
    @Query("""
            update user u set u.group_number=:g where u.number=:number;
            """)
    Mono<Integer> updateGroup(String number, int g);

    @Query("select u.description from user u where u.number='admin'")
    Mono<String> findStartTime();

    @Modifying
    @Query("""
            update user u set u.student=:student where u.id=:sid
            """)
    Mono<Integer> updateStudent(String sid, String student);
}
