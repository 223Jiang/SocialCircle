create table user_center.team
(
    id          varchar(255) not null comment '队伍ID'
        primary key,
    name        varchar(255) not null comment '队伍名称',
    description text null comment '队伍描述',
    num         int null comment '当前人数',
    max_num     int          not null comment '队伍最大人数',
    expire_time datetime null comment '队伍过期时间',
    leader_id   varchar(255) not null comment '队长的用户ID',
    status      int      default 0 null comment '队伍状态：0-公开，1-私有，2-加密',
    password    varchar(255) null comment '队伍密码（加密类型使用）',
    create_time datetime default CURRENT_TIMESTAMP null comment '队伍创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '队伍信息最后更新时间',
    is_delete   tinyint(1) default 0 null comment '删除标识：0-未删除，1-已删除'
);

create table user_center.team_member
(
    user_id   varchar(255) not null comment '用户ID',
    team_id   varchar(255) not null comment '队伍ID',
    join_time datetime default CURRENT_TIMESTAMP null comment '加入队伍时间',
    is_leader tinyint(1) default 0 null comment '是否为队长：0-否，1-是',
    is_delete tinyint(1) default 0 null comment '删除标识：0-未删除，1-已删除',
    constraint unique_member
        unique (user_id, team_id) comment '唯一索引，确保一个用户只能在一个队伍中'
);

create table user_center.user
(
    user_id          varchar(252) not null comment 'id',
    user_name        varchar(126) null comment '用户名',
    user_count       varchar(126) null comment '账户',
    user_password    varchar(126) null comment '密码',
    user_email       varchar(126) null comment '邮箱',
    sex              tinyint null comment '性别 (0-男，1-女)',
    user_phone       varchar(126) null comment '手机号',
    image_url        varchar(1000) null comment '用户头像',
    tags             varchar(252) null comment '用户标签',
    user_description varchar(252) null comment '描述',
    user_status      tinyint  default 0 null comment '账户状态(0-正常，1-封禁)',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete        tinyint  default 0 null comment '删除状态(0-未删除，1-删除)',
    is_admin         tinyint  default 0 null comment '是否为管理员 (0-普通用户，1-管理员)',
    constraint user_id_index
        unique (user_id)
) comment '用户表';

create index user_is_delete_index
    on user_center.user (is_delete);

create index user_tags_index
    on user_center.user (tags);

create index user_user_status_index
    on user_center.user (user_status);

