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
    @Query("""
            update user u set u.teacher=json_set(u.teacher, '$.count', cast(u.teacher -> '$.count' + 1 as unsigned))
            where u.id=:tid and (u.teacher -> '$.total' - u.teacher -> '$.count' > 0);
            """)
    Mono<Integer> updateCount(String tid);

    Flux<User> findByRole(int role);

    Flux<User> findByRoleAndGroupNumber(int role, int groupNumber);

    Flux<User> findByGroupNumber(int groupNumber);

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
}
