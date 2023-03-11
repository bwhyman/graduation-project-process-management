create table if not exists `user`
(
    id           char(19)     not null primary key,
    name         varchar(10)  not null,
    number       varchar(15)  not null,
    password     varchar(65)  not null,
    description  varchar(600) null,
    role         int          not null,
    group_number int          null,
    student      json         null comment '{"teacherId", "teacherName", "queueNumber", "projectTitle"}',
    teacher      json         null comment '{"count", "total"}',
    insert_time  datetime     not null default current_timestamp,
    update_time  datetime     not null default current_timestamp on update current_timestamp,

    unique (number),
    index (role),
    index (group_number),
    index ((cast(student ->> '$.teacherId' as char(19)) collate utf8mb4_bin))

);

create table if not exists `process`
(
    id          char(19)    not null primary key,
    name        varchar(20) not null,
    items       json        not null comment '[{"number", "name", "point", "description"}]',
    auth        int         not null,
    insert_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp
);

create table if not exists `process_score`
(
    id          char(19)   not null primary key,
    student_id  char(19)   not null,
    teacher_id  char(19)   not null,
    detail      json       not null comment '{"scores", "details": [{"number", "score"}]}',
    process_id  bigint(19) not null,
    insert_time datetime   not null default current_timestamp,
    update_time datetime   not null default current_timestamp on update current_timestamp,

    unique (process_id, student_id, teacher_id)
)