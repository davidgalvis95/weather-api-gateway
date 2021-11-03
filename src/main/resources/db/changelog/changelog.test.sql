--liquibase formatted sql

--changeset davidgalvis:1
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--changeset davidgalvis:2
CREATE TABLE IF NOT EXISTS api_route
(
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    path character varying COLLATE pg_catalog."default" NOT NULL,
    method character varying(10) COLLATE pg_catalog."default",
    uri character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT api_route_pkey PRIMARY KEY (id)
);