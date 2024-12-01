
-- CREATE TABLE IF NOT EXISTS public.exercicios_tipo (
--     tipo_treino_id INTEGER NOT NULL,
--     exercicio VARCHAR NOT NULL,
--     CONSTRAINT exercicios_tipo_fk FOREIGN KEY (tipo_treino_id)
--         REFERENCES public.tipotreino (id)
--         ON UPDATE CASCADE
--         ON DELETE CASCADE
-- );

-- CREATE TABLE IF NOT EXISTS public.instrutor (
--     id SERIAL PRIMARY KEY,
--     nome VARCHAR(255) NOT NULL,
--     email VARCHAR(255) UNIQUE,
--     telefone VARCHAR(20),
--     cpf VARCHAR(20) UNIQUE
-- );

-- CREATE TABLE IF NOT EXISTS public.recepcionista (
--     id SERIAL PRIMARY KEY,
--     nome VARCHAR(255) NOT NULL,
--     email VARCHAR(255) NOT NULL,
--     telefone VARCHAR(20) NOT NULL,
--     cpf VARCHAR(11) UNIQUE NOT NULL
-- );

-- CREATE TABLE IF NOT EXISTS public.tipotreino (
--     id SERIAL PRIMARY KEY,
--     tipo VARCHAR(1) NOT NULL,
--     ficha_treino_id INTEGER NOT NULL,
--     CONSTRAINT tipotreino_ficha_treino_id_fkey FOREIGN KEY (ficha_treino_id)
--         REFERENCES public.fichatreino (id)
--         ON UPDATE NO ACTION
--         ON DELETE CASCADE
-- );

-- CREATE TABLE IF NOT EXISTS public.usuario (
--     id SERIAL PRIMARY KEY,
--     username VARCHAR(50) UNIQUE NOT NULL,
--     password TEXT NOT NULL,
--     role VARCHAR(20) NOT NULL,
--     enabled BOOLEAN DEFAULT TRUE
-- );