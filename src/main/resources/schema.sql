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
    index (role, group_number),
    index ((cast(student ->> '$.teacherId' as char(19)) collate utf8mb4_bin))
);

create table if not exists `process`
(
    id          char(19)    not null primary key,
    name        varchar(20) not null,
    items       json        not null comment '[{"number", "name", "point", "description"}]',
    point       int         not null,
    auth        int         not null,
    insert_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp
);

create table if not exists `process_score`
(
    id          char(19) not null primary key,
    student_id  char(19) not null,
    process_id  char(19) not null,
    detail      json     not null comment '{"teacherId": score}',
    insert_time datetime not null default current_timestamp,
    update_time datetime not null default current_timestamp on update current_timestamp,

    unique (process_id, student_id)
);

create table if not exists `process_file`
(
    id           char(19)    not null primary key,
    name         varchar(20) not null,
    ext          varchar(20) null,
    detail       varchar(60) null,
    group_number int         not null,
    student_id   char(19)    not null,
    process_id   char(19)    not null,
    insert_time  datetime    not null default current_timestamp,
    update_time  datetime    not null default current_timestamp on update current_timestamp,

    index (process_id, group_number, student_id)
);