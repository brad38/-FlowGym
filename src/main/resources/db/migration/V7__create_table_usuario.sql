-- Criação da tabela usuario
CREATE TABLE IF NOT EXISTS public.usuario (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password TEXT NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    CONSTRAINT usuario_username_key UNIQUE (username)
)
TABLESPACE pg_default;

-- Alterar o dono da tabela, caso necessário
ALTER TABLE public.usuario
    OWNER TO postgres;
