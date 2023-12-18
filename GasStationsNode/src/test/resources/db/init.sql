create table gas_stations_brands
(
    id serial primary key,
    brand_name varchar not null
);

insert into gas_stations_brands (brand_name)
values ('neste'),
       ('circle_k'),
       ('viada'),
       ('virsi');

