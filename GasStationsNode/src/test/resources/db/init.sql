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

create table circle_k
(
    id serial primary key,
    brand_id int references gas_stations_brands(id),
    gas_type varchar,
    price varchar,
    location varchar
);

create table viada
(
    id serial primary key,
    brand_id int references gas_stations_brands(id),
    gas_type varchar,
    price varchar,
    location varchar
);

create table virsi
(
    id serial primary key,
    brand_id int references gas_stations_brands(id),
    gas_type varchar,
    price varchar,
    location varchar
);

create table neste
(
    id serial primary key,
    brand_id int references gas_stations_brands(id),
    gas_type varchar,
    price varchar,
    location varchar
);

insert into circle_k (gas_type, price, location)
values ('95E', '1.20', 'Riga'),
       ('98E', '1.40', 'Ventspils'),
       ('Diesel', '1.15', 'Riga');

insert into viada (gas_type, price, location)
values ('95E', '1.25', 'Riga'),
       ('98E', '1.45', 'Ventspils'),
       ('Diesel', '1.17', 'Daugavpils');

insert into virsi (gas_type, price, location)
values ('95E', '1.22', 'Riga'),
       ('98E', '1.53', 'Ventspils'),
       ('Diesel', '1.15', 'Riga');

insert into neste (gas_type, price, location)
values ('95E', '1.10', 'Riga'),
       ('98E', '1.14', 'Ventspils'),
       ('Diesel', '1.00', 'Riga');