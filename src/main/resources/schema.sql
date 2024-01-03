create table if not exists `user`
(
    id           char(19)     not null primary key,
    name         varchar(10)  not null,
    number       varchar(15)  not null,
    password     varchar(65)  not null,
    description  varchar(600) null,
    role         tinyint      not null,
    group_number tinyint      null,
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
    id             char(19)    not null primary key,
    name           varchar(20) not null,
    items          json        null comment '[{"number", "name", "point", "description"}]',
    point          tinyint     null,
    auth           char(5)     not null,
    student_attach json        null comment '[{"number", "name", "ext", "description"}]',
    insert_time    datetime    not null default current_timestamp,
    update_time    datetime    not null default current_timestamp on update current_timestamp

);

create table if not exists `process_score`
(
    id          char(19) not null primary key,
    student_id  char(19) not null,
    process_id  char(19) not null,
    teacher_id  char(19) not null,
    detail      json     not null comment '{"teacherName", "score", detail: [{"number", "score"}]}',
    insert_time datetime not null default current_timestamp,
    update_time datetime not null default current_timestamp on update current_timestamp,

    unique (process_id, student_id, teacher_id)
);

create table if not exists `process_file`
(
    id          char(19)    not null primary key,
    detail      varchar(60) null,
    student_id  char(19)    not null,
    process_id  char(19)    not null,
    number      tinyint     not null,
    insert_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp,

    unique (process_id, student_id, number)
);

/*create table if not exists `user_test`
(
    id             char(19)     not null primary key,
    name           varchar(10)  not null,
    number         varchar(15)  not null,
    password       varchar(65)  not null,
    description    varchar(600) null,
    role           tinyint      not null,
    group_number   json         null comment '[1, 4]',
    second_defense tinyint(1)   null,
    student        json         null comment '{"teacherId", "teacherName", "queueNumber", "projectTitle"}',
    teacher        json         null comment '{"count", "total"}',
    insert_time    datetime     not null default current_timestamp,
    update_time    datetime     not null default current_timestamp on update current_timestamp,

    unique (number),
    INDEX (role, (cast(group_number AS unsigned array))),
    index ((cast(student ->> '$.teacherId' as char(19)) collate utf8mb4_bin))
);*/
/*
create table if not exists `process_score_test`
(
    id          char(19) not null primary key,
    student_id  char(19) not null,
    process_id  char(19) not null,
    teacher_id  char(19) not null,
    detail      json     not null comment '{"score", [{"number", "score"}]}',
    insert_time datetime not null default current_timestamp,
    update_time datetime not null default current_timestamp on update current_timestamp,

    unique (process_id, student_id)
);*/