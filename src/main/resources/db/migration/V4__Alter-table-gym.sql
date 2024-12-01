DROP TABLE IF EXISTS public.aluno CASCADE;
CREATE TABLE IF NOT EXISTS public.aluno
(
    id integer NOT NULL DEFAULT nextval('aluno_id_seq'::regclass),
    nome character varying(255) COLLATE pg_catalog."default" NOT NULL,
    nascimento date NOT NULL,
    email character varying(255) COLLATE pg_catalog."default",
    telefone character varying(20) COLLATE pg_catalog."default" NOT NULL,
    cpf character varying(11) COLLATE pg_catalog."default",
    menor boolean DEFAULT false,
    responsavel_cpf character varying(11) COLLATE pg_catalog."default",
    matricula character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT aluno_pkey PRIMARY KEY (id),
    CONSTRAINT aluno_cpf_key UNIQUE (cpf),
    CONSTRAINT aluno_email_key UNIQUE (email),
    CONSTRAINT aluno_matricula_key UNIQUE (matricula)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.aluno
    OWNER to postgres;
-- Index: unique_cpf

-- DROP INDEX IF EXISTS public.unique_cpf;

CREATE UNIQUE INDEX IF NOT EXISTS unique_cpf
    ON public.aluno USING btree
    (email COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default
    WHERE cpf IS NOT NULL;
-- Index: unique_email

-- DROP INDEX IF EXISTS public.unique_email;

CREATE UNIQUE INDEX IF NOT EXISTS unique_email
    ON public.aluno USING btree
    (email COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default
    WHERE email IS NOT NULL;

DROP TABLE IF EXISTS public.fichatreino CASCADE;

CREATE TABLE IF NOT EXISTS public.fichatreino
(
    id integer NOT NULL DEFAULT nextval('fichatreino_id_seq'::regclass),
    aluno_id integer NOT NULL,
    objetivo character varying(255) COLLATE pg_catalog."default" NOT NULL,
    data_inicio date NOT NULL,
    data_termino date,
    numero_impressoes integer NOT NULL DEFAULT 0,
    max_impressoes integer NOT NULL DEFAULT 0,
    CONSTRAINT fichatreino_pkey PRIMARY KEY (id),
    CONSTRAINT fichatreino_aluno_id_fkey FOREIGN KEY (aluno_id)
        REFERENCES public.aluno (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.fichatreino
    OWNER to postgres;

DROP TABLE IF EXISTS public.exercicios_tipo CASCADE;

CREATE TABLE IF NOT EXISTS public.exercicios_tipo
(
    tipo_treino_id integer NOT NULL,
    exercicio character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT exercicios_tipo_fk FOREIGN KEY (tipo_treino_id)
        REFERENCES public.tipotreino (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.exercicios_tipo
    OWNER to postgres;

DROP TABLE IF EXISTS public.instrutor CASCADE;

CREATE TABLE IF NOT EXISTS public.instrutor
(
    id integer NOT NULL DEFAULT nextval('instrutor_id_seq'::regclass),
    nome character varying(255) COLLATE pg_catalog."default" NOT NULL,
    email character varying(255) COLLATE pg_catalog."default",
    telefone character varying(20) COLLATE pg_catalog."default",
    cpf character varying(20) COLLATE pg_catalog."default",
    CONSTRAINT instrutor_pkey PRIMARY KEY (id),
    CONSTRAINT instrutor_cpf_key UNIQUE (cpf),
    CONSTRAINT instrutor_email_key UNIQUE (email)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.instrutor
    OWNER to postgres;

DROP TABLE IF EXISTS public.recepcionista CASCADE;

CREATE TABLE IF NOT EXISTS public.recepcionista
(
    id integer NOT NULL DEFAULT nextval('recepcionista_id_seq'::regclass),
    nome character varying(255) COLLATE pg_catalog."default" NOT NULL,
    email character varying(255) COLLATE pg_catalog."default" NOT NULL,
    telefone character varying(20) COLLATE pg_catalog."default" NOT NULL,
    cpf character varying(11) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT recepcionista_pkey PRIMARY KEY (id),
    CONSTRAINT recepcionista_cpf_key UNIQUE (cpf)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.recepcionista
    OWNER to postgres;

DROP TABLE IF EXISTS public.tipotreino CASCADE;

CREATE TABLE IF NOT EXISTS public.recepcionista
(
    id integer NOT NULL DEFAULT nextval('recepcionista_id_seq'::regclass),
    nome character varying(255) COLLATE pg_catalog."default" NOT NULL,
    email character varying(255) COLLATE pg_catalog."default" NOT NULL,
    telefone character varying(20) COLLATE pg_catalog."default" NOT NULL,
    cpf character varying(11) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT recepcionista_pkey PRIMARY KEY (id),
    CONSTRAINT recepcionista_cpf_key UNIQUE (cpf)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.recepcionista
    OWNER to postgres;

DROP TABLE IF EXISTS public.usuario CASCADE;

CREATE TABLE IF NOT EXISTS public.usuario
(
    id integer NOT NULL DEFAULT nextval('usuario_id_seq'::regclass),
    username character varying(50) COLLATE pg_catalog."default" NOT NULL,
    password text COLLATE pg_catalog."default" NOT NULL,
    role character varying(20) COLLATE pg_catalog."default" NOT NULL,
    enabled boolean DEFAULT true,
    CONSTRAINT usuario_pkey PRIMARY KEY (id),
    CONSTRAINT usuario_username_key UNIQUE (username)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.usuario
    OWNER to postgres;
