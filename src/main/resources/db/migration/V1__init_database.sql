create sequence ad_id_seq;

alter sequence ad_id_seq owner to postgres;

create table if not exists users
(
    id       bigserial
        primary key,
    login    varchar(20) not null
        unique,
    email    varchar(30) not null
        unique,
    name     varchar(20) not null,
    password text        not null,
    role     varchar
        constraint users_role_check
            check ((role)::text = ANY ((ARRAY ['USER'::character varying, 'ADMIN'::character varying])::text[])),
    status   varchar(10)
        constraint users_status_check
            check ((status)::text = ANY
                   ((ARRAY ['ACTIVE'::character varying, 'DEACTIVATED'::character varying])::text[]))
);

alter table users
    owner to postgres;

create table if not exists ads
(
    id          bigint    default nextval('ad_id_seq'::regclass) not null
        constraint ad_pkey
            primary key,
    name        varchar(30)                                      not null,
    description text                                             not null,
    category    varchar(20)                                      not null,
    price       numeric(8, 2)                                    not null,
    status      varchar(10)                                      not null
        constraint ad_status_check
            check ((status)::text = ANY
                   ((ARRAY ['ACTIVE'::character varying, 'BLOCKED'::character varying, 'DEACTIVATED'::character varying])::text[])),
    created_at  timestamp default CURRENT_TIMESTAMP              not null,
    user_id     bigint                                           not null
        constraint fk_ad_user
            references users
);

alter table ads
    owner to postgres;

alter sequence ad_id_seq owned by ads.id;

