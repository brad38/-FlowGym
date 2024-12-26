CREATE TABLE IF NOT EXISTS public.aluno (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    nascimento DATE NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    cpf VARCHAR(14),
    menor BOOLEAN DEFAULT false,
    responsavel_cpf VARCHAR(14),
    matricula VARCHAR(255),
    data_vencimento DATE,
    CONSTRAINT aluno_cpf_aluno_key UNIQUE (cpf),
    CONSTRAINT aluno_email_key UNIQUE (email),
    CONSTRAINT aluno_matricula_key UNIQUE (matricula)
);

CREATE UNIQUE INDEX IF NOT EXISTS unique_email
    ON public.aluno (email)
    WHERE cpf IS NOT NULL;