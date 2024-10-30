create table sz_house_deal_statistic_data
(
    id                       varchar(255) not null,
    date                     timestamp(6) not null,
    new_house_deal_area      double precision,
    new_house_deal_set_count integer,
    used_house_deal_area     double precision,
    used_house_deal_count    integer,
    create_time              timestamp(6) not null,
    update_time              timestamp(6) not null,
    deleted                  boolean default false
);
