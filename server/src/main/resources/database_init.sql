
DROP TABLE IF EXISTS coordinates cascade;
DROP TABLE IF EXISTS people cascade;
DROP TABLE IF EXISTS study_groups cascade;
DROP TABLE IF EXISTS users cascade ;

DROP TYPE IF EXISTS form_of_education_enum;
DROP TYPE IF EXISTS semester_enum;
DROP TYPE IF EXISTS country_enum;

CREATE TYPE form_of_education_enum as enum('DISTANCE_EDUCATION', 'FULL_TIME_EDUCATION', 'EVENING_CLASSES');

CREATE TYPE semester_enum as enum('FOURTH', 'SIXTH', 'SEVENTH', 'EIGHTH');

CREATE TYPE country_enum as enum('GERMANY', 'FRANCE', 'SPAIN', 'ITALY', 'JAPAN');

CREATE TABLE users(
    id serial PRIMARY KEY,
    user_name varchar(30) NOT NULL,
    password text NOT NULL
);

CREATE TABLE coordinates(
    id serial PRIMARY KEY,
    x double precision,
    y double precision,
    CONSTRAINT x_range check ( x > -313.0 )
);

CREATE TABLE people(
    id serial PRIMARY KEY,
    name varchar(30) NOT NULL,
    birthday date,
    weight double precision,
    passport_id varchar(40) NOT NULL,

    nationality country_enum,

    CONSTRAINT weight_range CHECK ( weight > 0 ),
    CONSTRAINT passport_id_range CHECK ( length(passport_id) >= 7 )
);


CREATE TABLE study_groups(
    id serial PRIMARY KEY,
    name varchar(30) NOT NULL,
    coordinates_id bigint references coordinates NOT NULL,
    creation_date date NOT NULL,
    students_count integer,

    average_mark bigint,

    form_of_education form_of_education_enum,
    semester semester_enum,
    group_admin_id bigint references people

    CONSTRAINT students_count_range CHECK ( students_count > 0 ),
    CONSTRAINT average_mark_range CHECK ( average_mark > 0 ),

    owner bigint references users NOT NULL
);
