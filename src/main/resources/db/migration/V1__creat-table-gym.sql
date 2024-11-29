CREATE TABLE Aluno (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    nascimento DATE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    cpf VARCHAR(11) UNIQUE,  -- CPF do aluno (opcional)
    menor BOOLEAN DEFAULT FALSE,  -- Indica se o aluno é menor de idade
    responsavel_Cpf VARCHAR(11)   -- CPF do responsável
);