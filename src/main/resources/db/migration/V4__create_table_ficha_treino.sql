CREATE TABLE IF NOT EXISTS public.ficha_treino (
    id SERIAL PRIMARY KEY,  -- Usando o tipo SERIAL para criar automaticamente a sequência
    aluno_id integer NOT NULL,
    objetivo VARCHAR(255) NOT NULL,
    data_inicio DATE NOT NULL,
    data_termino DATE,
    numero_impressoes integer NOT NULL DEFAULT 0,
    max_impressoes integer NOT NULL DEFAULT 0,
    CONSTRAINT ficha_treino_aluno_id_fkey FOREIGN KEY (aluno_id)
        REFERENCES public.aluno (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

-- Definindo o proprietário da tabela
ALTER TABLE IF EXISTS public.ficha_treino
    OWNER TO postgres;
