-- Tabela Aluno
CREATE TABLE Aluno (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    nascimento DATE NOT NULL,
    email VARCHAR(255) UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    cpf VARCHAR(11) UNIQUE,
    menor BOOLEAN DEFAULT FALSE,
    responsavel_Cpf VARCHAR(11),
    matricula VARCHAR(255) UNIQUE,
    data_vencimento DATE
);

-- Índice único condicional para CPF
CREATE UNIQUE INDEX unique_cpf
ON Aluno(email)
WHERE cpf IS NOT NULL;

-- Índice único condicional para email
CREATE UNIQUE INDEX unique_email
ON Aluno(email)
WHERE cpf IS NOT NULL;

-- Tabela FichaTreino
CREATE TABLE FichaTreino (
    id SERIAL PRIMARY KEY,
    aluno_id INTEGER NOT NULL,
    objetivo VARCHAR(255) NOT NULL,
    data_inicio DATE NOT NULL,
    data_termino DATE,
    numero_impressoes INTEGER NOT NULL DEFAULT 0,
    max_impressoes INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (aluno_id) REFERENCES Aluno(id) ON DELETE CASCADE
);

-- Tabela TipoTreino
CREATE TABLE TipoTreino (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(1) NOT NULL,
    ficha_treino_id INT NOT NULL,
    FOREIGN KEY (ficha_treino_id) REFERENCES FichaTreino(id) ON DELETE CASCADE
);

-- Tabela auxiliar para exercícios associados a tipos de treino
CREATE TABLE IF NOT EXISTS public.exercicios_tipo (
    tipo_treino_id INTEGER NOT NULL,
    exercicio VARCHAR NOT NULL,
    CONSTRAINT exercicios_tipo_fk FOREIGN KEY (tipo_treino_id)
        REFERENCES public.tipotreino (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Tabela Usuário (login e papéis)
CREATE TABLE IF NOT EXISTS public.usuario (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);

-- Tabela Instrutor
CREATE TABLE Instrutor (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    telefone VARCHAR(20),
    cpf VARCHAR(20) UNIQUE
);

-- Tabela Recepcionista
CREATE TABLE IF NOT EXISTS Recepcionista (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    cpf VARCHAR(11) UNIQUE NOT NULL
);
