create table sz_new_house_main_page_item
(
    id                   varchar(255) not null,
    pre_sale_number      varchar(255) not null,
    pre_sale_number_link varchar(255) not null,
    project_name         varchar(255) not null,
    project_name_link    varchar(255) not null,
    developer            varchar(255) not null,
    district             varchar(255) not null,
    approval_date        timestamp(6) not null,
    create_time          timestamp(6) not null,
    update_time          timestamp(6) not null,
    deleted              boolean default false
);
