DROP TABLE IF EXISTS public.swift_code;

CREATE TABLE public.swift_code (
    id uuid primary key,
    bank_name varchar(256) not null,
    country_ISO2 char(2) not null,
    country_name varchar(256) not null,
    is_headquarter bool not null default false,
    swift_code varchar(11) not null,
    superior_unit uuid references swift_code(id)
);