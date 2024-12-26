CREATE TABLE IF NOT EXISTS public.recepcionista (
    id SERIAL PRIMARY KEY,  -- Usando o tipo SERIAL para criar automaticamente a sequência
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    cpf VARCHAR(14) NOT NULL,
    CONSTRAINT recepcionista_cpf_key UNIQUE (cpf)
);

-- Definindo o proprietário da tabela
ALTER TABLE IF EXISTS public.recepcionista
    OWNER TO postgres;

