-- This script was generated by the ERD tool in pgAdmin 4.
-- Please log an issue at https://redmine.postgresql.org/projects/pgadmin4/issues/new if you find any bugs, including reproduction steps.
BEGIN;


CREATE TABLE IF NOT EXISTS public.operations
(
    operation_id bigint NOT NULL,
    title character varying(255) COLLATE pg_catalog."default",
    description character varying(255) COLLATE pg_catalog."default",
    duration bigint,
    CONSTRAINT operation_pkey PRIMARY KEY (operation_id),
    CONSTRAINT title_uk UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS public.timeslots
(
    timeslot_id bigint NOT NULL,
    date_for date,
    time_from time without time zone,
    time_to time without time zone,
    is_locked boolean,
    operation_id bigint,
    client_id bigint,
    CONSTRAINT timeslot_pkey PRIMARY KEY (timeslot_id)
);

CREATE TABLE IF NOT EXISTS public.clients
(
    client_id bigint NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    email character varying(255) COLLATE pg_catalog."default",
    phone character varying(255) COLLATE pg_catalog."default",
    password character varying COLLATE pg_catalog."default",
    CONSTRAINT client_pkey PRIMARY KEY (client_id),
    CONSTRAINT phone_uk UNIQUE (phone)
);

ALTER TABLE IF EXISTS public.timeslots
    ADD CONSTRAINT clients_fk FOREIGN KEY (client_id)
    REFERENCES public.clients (client_id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public.timeslots
    ADD CONSTRAINT operations_fk FOREIGN KEY (operation_id)
    REFERENCES public.operations (operation_id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

END;