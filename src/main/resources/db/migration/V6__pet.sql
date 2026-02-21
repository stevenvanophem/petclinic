CREATE SEQUENCE pet_sequence
    START WITH 1
    INCREMENT BY 1 NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE pet
(
    id         BIGINT      NOT NULL DEFAULT nextval('pet_sequence'),
    name       VARCHAR(30) NOT NULL,
    birth_date DATE        NOT NULL,
    type       VARCHAR(80) NOT NULL,
    owner_id   BIGINT      NOT NULL,
    version    INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT pk_pet PRIMARY KEY (id),
    CONSTRAINT uq_pet_owner_name UNIQUE (owner_id, name),
    CONSTRAINT fk_pet_owner FOREIGN KEY (owner_id) REFERENCES owner (id)
);
