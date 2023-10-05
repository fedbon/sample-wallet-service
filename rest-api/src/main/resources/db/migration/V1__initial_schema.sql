create table refresh_tokens
(
    id         bigserial primary key,
    created_at timestamp(6) with time zone,
    token      varchar(255)
);

alter table refresh_tokens owner to usr;

create table users
(
    user_id            bigserial primary key,
    created            timestamp(6) with time zone,
    enabled            boolean,
    password           varchar(255),
    user_mobile_number varchar(255)
);

alter table users owner to usr;

create table tokens
(
    id           bigserial primary key,
    expire_date  timestamp(6) with time zone,
    token        varchar(255),
    user_user_id bigint
        constraint uk_lcq84xjh6gonochynkeog43l9
            unique
        constraint fk22ancosgvhuh4upst1n4vn6am
            references users
);

alter table tokens owner to usr;

create table wallets
(
    wallet_id    bigserial primary key,
    balance      double precision,
    created_date timestamp(6) with time zone,
    user_id      bigint
        constraint fkc1foyisidw7wqqrkamafuwn4e
            references users
);

alter table wallets owner to usr;

create table cards
(
    card_id          bigserial primary key,
    card_holder_name varchar(255),
    card_number      varchar(255),
    cvv_code         varchar(255),
    expiry_date      varchar(255),
    user_id          bigint
        constraint fkcmanafgwbibfijy2o5isfk3d5
            references users,
    wallet_id        bigint
        constraint fkhs0840yvjj1hofwmxnd1og8bv
            references wallets
);

alter table cards owner to usr;

create table transactions
(
    transaction_id          bigserial primary key,
    amount                  double precision,
    date_time_processed     timestamp(6) with time zone,
    is_completed            boolean,
    recipient_mobile_number varchar(255),
    transaction_type        smallint
        constraint transactions_transaction_type_check
            check ((transaction_type >= 0) AND (transaction_type <= 1)),
    user_id                 bigint
        constraint fkqwv7rmvc8va8rep7piikrojds
            references users,
    wallet_id               bigint
        constraint fk23bop5lktue0o5q7kr19ti8h
            references wallets
);

alter table transactions owner to usr;

