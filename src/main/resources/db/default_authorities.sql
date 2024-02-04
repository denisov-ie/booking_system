-- liquibase formatted sql

-- changeset ie-denisov:roles_users
INSERT INTO public.roles(role_id, role) VALUES (0, 'ADMIN');
INSERT INTO public.roles(role_id, role) VALUES (1, 'USER');
INSERT INTO public.roles(role_id, role) VALUES (2, 'SUPPORT');

-- Hint: login = ADMIN, password = test
INSERT INTO public.users(user_id, login, pass, role_id) VALUES (0, 'ADMIN', '{bcrypt}$2a$10$LIe2vrJgjwLaIwFtuSNvquXzQF9Zc2BoLRSeWsmUc/.fga0n489Bm', 0);