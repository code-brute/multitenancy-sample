drop TABLE IF EXISTS t_tenant_data_source;
create table t_tenant_data_source (
    tenant_id_ varchar(255) not null,
    db_name_ varchar(255),
    driver_class_name_ varchar(255),
    password_ varchar(255),
    port_ varchar(255),
    schema_ varchar(255),
    server_name_ varchar(255),
    username_ varchar(255),
    url_ varchar(255),
    primary key (tenant_id_)
);
--