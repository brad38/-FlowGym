-- Criação da tabela exercicios_tipo
CREATE TABLE IF NOT EXISTS exercicios_tipo (
    tipo_treino_id INTEGER NOT NULL,
    exercicio VARCHAR NOT NULL,
    CONSTRAINT exercicios_tipo_fk FOREIGN KEY (tipo_treino_id)
        REFERENCES public.tipo_treino (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
TABLESPACE pg_default;

-- Alterar o dono da tabela, caso necessário
ALTER TABLE public.exercicios_tipo
    OWNER TO postgres;
