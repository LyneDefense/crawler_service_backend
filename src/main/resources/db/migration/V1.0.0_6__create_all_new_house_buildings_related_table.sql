create table sz_new_house_sales_building_info
(
    id                              varchar(255) PRIMARY KEY,
    project_id                      varchar(255) not null,
    project_name                    varchar(255) not null,
    building_name                   varchar(255) not null,
    engineering_planning_permit     varchar(255) not null,
    engineering_construction_permit varchar(255),
    detail_link                     varchar(255) not null,
    create_time                     timestamp(6) not null,
    update_time                     timestamp(6) not null,
    deleted                         boolean default false

);

CREATE TABLE public.sz_new_house_sales_info
(
    id                             VARCHAR(255) PRIMARY KEY,
    project_id                     VARCHAR(255) not null,
    project_name                   VARCHAR(255) not null,
    plot_number                    VARCHAR(255),
    plot_location                  VARCHAR(255),
    transfer_date                  DATE,
    district                       VARCHAR(255),
    ownership_source               VARCHAR(255),
    approving_authority            VARCHAR(255),
    contract_number                VARCHAR(255),
    use_years                      VARCHAR(255),
    supplementary_agreement        VARCHAR(255),
    land_planning_permit           VARCHAR(255),
    house_usage                    VARCHAR(255),
    land_usage                     VARCHAR(255),
    land_grade                     VARCHAR(255),
    base_area                      DOUBLE PRECISION,
    plot_area                      DOUBLE PRECISION,
    total_construct_area           DOUBLE PRECISION,
    presale_total_sets             INTEGER,
    presale_total_area             DOUBLE PRECISION,
    onsale_total_sets              INTEGER,
    onsale_total_area              DOUBLE PRECISION,
    engineering_supervision_agency VARCHAR(255),
    property_management_company    VARCHAR(255),
    management_fee                 VARCHAR(255),
    create_time                    timestamp(6) not null,
    update_time                    timestamp(6) not null,
    deleted                        boolean default false
);

CREATE TABLE public.sz_new_house_unit_info
(
    id                               VARCHAR(255) PRIMARY KEY,
    block_id                         VARCHAR(255) not null,
    status                           VARCHAR(255),
    block_number                     VARCHAR(255),
    floor_number                     INTEGER,
    unit_source_number               VARCHAR(255),
    unit_number                      VARCHAR(255),
    usage                            VARCHAR(255),
    accessibility                    BOOLEAN,
    construction_price               DOUBLE PRECISION,
    gross_floor_price                DOUBLE PRECISION,
    pre_sale_construction_area       DOUBLE PRECISION,
    pre_sale_gross_floor_area        DOUBLE PRECISION,
    pre_sale_shared_area             DOUBLE PRECISION,
    pre_completion_construction_area DOUBLE PRECISION,
    pre_completion_gross_floor_area  DOUBLE PRECISION,
    pre_completion_shared_area       DOUBLE PRECISION,
    create_time                      timestamp(6) not null,
    update_time                      timestamp(6) not null,
    deleted                          boolean default false
);

CREATE TABLE public.sz_new_house_block_info
(
    id          VARCHAR(255) PRIMARY KEY,
    building_id VARCHAR(255) not null,
    block_name  VARCHAR(255),
    create_time timestamp(6) not null,
    update_time timestamp(6) not null,
    deleted     boolean default false
);

CREATE TABLE public.sz_new_house_building_info
(
    id            VARCHAR(255) PRIMARY KEY,
    project_id    VARCHAR(255) not null,
    building_name VARCHAR(255),
    create_time   timestamp(6) not null,
    update_time   timestamp(6) not null,
    deleted       boolean default false
);
