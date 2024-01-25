-- This script was generated by the ERD tool in pgAdmin 4.
-- Please log an issue at https://redmine.postgresql.org/projects/pgadmin4/issues/new if you find any bugs, including reproduction steps.
BEGIN;


CREATE TABLE IF NOT EXISTS public.users
(
    user_id bigint NOT NULL,
    login character varying(255) COLLATE pg_catalog."default",
    pass character varying(255) COLLATE pg_catalog."default",
    role_id bigint,
    CONSTRAINT user_pkey PRIMARY KEY (user_id),
    CONSTRAINT login_uk UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS public.roles
(
    role_id bigint NOT NULL,
    role character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT roles_pkey PRIMARY KEY (role_id),
    CONSTRAINT role_uk UNIQUE (role)
);

ALTER TABLE IF EXISTS public.users
    ADD CONSTRAINT role_fk FOREIGN KEY (role_id)
    REFERENCES public.roles (role_id) MATCH SIMPLE
    ON UPDATE SET NULL
    ON DELETE SET NULL;

END;