-- liquibase formatted sql

-- changeset ie-denisov:users
INSERT INTO public.roles(role_id, role) VALUES (0, 'ADMIN');

-- Hint: login = ADMIN, password = test
INSERT INTO public.users(user_id, login, pass, role_id) VALUES (0, 'ADMIN', '{bcrypt}$2a$10$LIe2vrJgjwLaIwFtuSNvquXzQF9Zc2BoLRSeWsmUc/.fga0n489Bm', 0);