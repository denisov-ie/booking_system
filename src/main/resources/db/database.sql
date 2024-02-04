-- liquibase formatted sql

-- changeset ie-denisov:database
CREATE TABLE "users"
(
    "user_id" BIGINT NOT NULL,
    "login"   VARCHAR(255),
    "pass"    VARCHAR(255),
    "role_id" BIGINT,
    CONSTRAINT "user_pkey" PRIMARY KEY ("user_id")
);

CREATE TABLE "clients"
(
    "client_id" BIGINT NOT NULL,
    "name"      VARCHAR(255),
    "email"     VARCHAR(255),
    "phone"     VARCHAR(255),
    CONSTRAINT "client_pkey" PRIMARY KEY ("client_id")
);

CREATE TABLE "roles"
(
    "role_id" BIGINT NOT NULL,
    "role"    VARCHAR(255),
    CONSTRAINT "roles_pkey" PRIMARY KEY ("role_id")
);

CREATE TABLE "operations"
(
    "operation_id" BIGINT NOT NULL,
    "title"        VARCHAR(255),
    "description"  VARCHAR(255),
    "duration"     BIGINT,
    CONSTRAINT "operation_pkey" PRIMARY KEY ("operation_id")
);

CREATE TABLE "timeslots"
(
    "timeslot_id"  BIGINT NOT NULL,
    "date_for"     date,
    "time_from"    time(6) WITHOUT TIME ZONE,
    "time_to"      time(6) WITHOUT TIME ZONE,
    "is_locked"    BOOLEAN,
    "operation_id" BIGINT,
    "client_id"    BIGINT,
    CONSTRAINT "timeslot_pkey" PRIMARY KEY ("timeslot_id")
    );

CREATE TABLE "hibernate_sequences"
(
    "sequence_name" VARCHAR(255) NOT NULL,
    "next_val"      BIGINT,
    CONSTRAINT "hibernate_sequences_pkey" PRIMARY KEY ("sequence_name")
    );

ALTER TABLE "users"
    ADD CONSTRAINT "login_uk" UNIQUE ("login");

ALTER TABLE "clients"
    ADD CONSTRAINT "phone_uk" UNIQUE ("phone");

ALTER TABLE "roles"
    ADD CONSTRAINT "role_uk" UNIQUE ("role");

ALTER TABLE "operations"
    ADD CONSTRAINT "title_uk" UNIQUE ("title");

ALTER TABLE "timeslots"
    ADD CONSTRAINT "clients_fk" FOREIGN KEY ("client_id") REFERENCES "clients" ("client_id") ON UPDATE SET NULL ON DELETE SET NULL;

ALTER TABLE "timeslots"
    ADD CONSTRAINT "operations_fk" FOREIGN KEY ("operation_id") REFERENCES "operations" ("operation_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "users"
    ADD CONSTRAINT "role_fk" FOREIGN KEY ("role_id") REFERENCES "roles" ("role_id") ON UPDATE SET NULL ON DELETE SET NULL;

CREATE SEQUENCE hibernate_sequence START 1;

