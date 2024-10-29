create table sz_subscription_online_sign_info
(
    id          varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    district    varchar(255)                                not null,
    date        timestamp(6)                                not null,
    create_time timestamp(6)                                not null,
    update_time timestamp(6)                                not null,
    deleted     boolean default false
);

create table sz_subscription_online_sign_detail
(
    id                 varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    parent_id          varchar(255)                                not null,
    category           varchar(255)                                not null,
    subscription_count integer,
    subscription_area  double precision,
    create_time        timestamp(6)                                not null,
    update_time        timestamp(6)                                not null,
    deleted            boolean default false
);

create table sz_contract_online_sign_detail
(
    id                         varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    parent_id                  varchar(255)                                not null,
    category                   varchar(255)                                not null,
    contract_online_sign_count integer,
    contract_online_sign_area  double precision,
    available_sales_count      integer,
    available_sales_area       double precision,
    create_time                timestamp(6)                                not null,
    update_time                timestamp(6)                                not null,
    deleted                    boolean default false
);

create table sz_contract_online_sign_area_detail
(
    id                         varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    parent_id                  varchar(255)                                not null,
    area_range                 varchar(255),
    contract_online_sign_count integer,
    contract_online_sign_area  double precision,
    create_time                timestamp(6)                                not null,
    update_time                timestamp(6)                                not null,
    deleted                    boolean default false
);

create table sz_house_deals_area_detail
(
    id          varchar(255)     not null,
    parent_id   varchar(255)     not null,
    area_range  varchar(255)     not null,
    deal_count  integer          not null,
    deal_area   double precision not null,
    create_time timestamp(6)     not null,
    update_time timestamp(6)     not null,
    deleted     boolean default false
);

create table sz_house_deals_detail
(
    id                   varchar(255)     not null,
    parent_id            varchar(255)     not null,
    deal_count           integer          not null,
    deal_area            double precision not null,
    end_month_sale_count integer          not null,
    end_month_sale_area  double precision not null,
    create_time          timestamp(6)     not null,
    update_time          timestamp(6)     not null,
    deleted              boolean default false,
    category             varchar(255)     not null
);

create table sz_house_deals_info
(
    id          varchar(255) not null,
    district    varchar(255) not null,
    year        integer      not null,
    month       integer      not null,
    update_date timestamp(6) not null,
    messages    text,
    create_time timestamp(6) not null,
    update_time timestamp(6) not null,
    deleted     boolean default false
);
