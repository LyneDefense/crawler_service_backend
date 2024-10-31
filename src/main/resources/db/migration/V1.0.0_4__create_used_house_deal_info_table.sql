create table sz_used_house_deals_info
(
    id          varchar(255) not null,
    district    varchar(255) not null,
    date        timestamp(6) not null,
    create_time timestamp(6) not null,
    update_time timestamp(6) not null,
    deleted     boolean default false
);

create table sz_used_house_deals_detail
(
    id          varchar(255) not null,
    parent_id   varchar(255) not null,
    category    varchar(255) not null,
    sales_area  double precision,
    sales_count integer,
    create_time timestamp(6) not null,
    update_time timestamp(6) not null,
    deleted     boolean default false
);
