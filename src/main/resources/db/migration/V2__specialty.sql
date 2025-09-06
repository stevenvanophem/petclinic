CREATE SEQUENCE specialty_sequence
    START WITH 1
    INCREMENT BY 1 NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE specialty
(
    id      BIGINT      NOT NULL DEFAULT nextval('specialty_sequence'),
    name    VARCHAR(80) NOT NULL,
    version INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT pk_specialty PRIMARY KEY (id),
    CONSTRAINT uq_specialty_name UNIQUE (name)
);