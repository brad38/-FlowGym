CREATE TABLE IF NOT EXISTS public.tipo_treino (
    id SERIAL PRIMARY KEY,  -- Usando o tipo SERIAL para criar a sequência automaticamente
    tipo character varying(1) NOT NULL,
    ficha_treino_id integer NOT NULL,
    CONSTRAINT tipo_treino_ficha_treino_id_fkey FOREIGN KEY (ficha_treino_id)
        REFERENCES public.ficha_treino (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

-- Definindo o proprietário da tabela
ALTER TABLE IF EXISTS public.tipo_treino
    OWNER TO postgres;
