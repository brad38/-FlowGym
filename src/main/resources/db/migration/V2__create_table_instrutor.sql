-- Criação da tabela instrutor
CREATE TABLE IF NOT EXISTS public.instrutor (
     id SERIAL PRIMARY KEY,  -- Esta linha define a chave primária
        nome VARCHAR(255) NOT NULL,
        email VARCHAR(255),
        telefone VARCHAR(20),
        cpf VARCHAR(20),
        CONSTRAINT instrutor_cpf_key UNIQUE (cpf),
        CONSTRAINT instrutor_email_key UNIQUE (email)
)
TABLESPACE pg_default;

-- Alterar o dono da tabela, caso necessário
ALTER TABLE public.instrutor
    OWNER TO postgres;