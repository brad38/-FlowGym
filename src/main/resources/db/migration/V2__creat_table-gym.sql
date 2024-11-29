-- CREATE TABLE Aluno (
--     id SERIAL PRIMARY KEY,
--     nome VARCHAR(255) NOT NULL,
--     nascimento DATE NOT NULL,
--     email VARCHAR(255) UNIQUE NOT NULL,
--     telefone VARCHAR(20),
--     cpf VARCHAR(11) UNIQUE,  -- CPF do aluno (opcional)
--     menor BOOLEAN DEFAULT FALSE,  -- Indica se o aluno é menor de idade
--     responsavel_Cpf VARCHAR(11)   -- CPF do responsável
-- );

CREATE TABLE FichaTreino (
    id SERIAL PRIMARY KEY,
    aluno_id INTEGER NOT NULL, -- Relaciona a ficha ao aluno
    objetivo VARCHAR(255) NOT NULL,
    exercicios TEXT NOT NULL, -- Lista de exercícios
    data_inicio DATE NOT NULL,
    data_termino DATE, -- Data opcional
    FOREIGN KEY (aluno_id) REFERENCES Aluno(id) ON DELETE CASCADE
);