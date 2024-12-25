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

    Mono<User> findByNumberAndDepartmentId(String number, String depid);

    Flux<User> findByRoleAndDepartmentIdOrderById(String role, String depid);

    @Query("""
            select * from user u
            where u.role=:role and u.group_number=:groupNumber and u.department_id=:depid
            order by cast(u.student ->> '$.queueNumber' as unsigned)
            """)
    Flux<User> findByRoleAndGroupNumber(String depid, String role, int groupNumber);

    @Query("""
            select * from user u where u.student->>'$.teacherId'=:tid and u.department_id=:depid
            """)
    Flux<User> findStudentByTeacherId(String tid, String depid);

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

    @Modifying
    @Query("""
            update user u set u.group_number=:g where u.number=:number and u.department_id=:depid;
            """)
    Mono<Integer> updateGroup(String number, int g, String depid);

    Mono<Integer> countByDepartmentId(String did);
}
