CREATE SEQUENCE vet_sequence
    START WITH 1
    INCREMENT BY 1 NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE vet
(
    id         BIGINT      NOT NULL DEFAULT nextval('vet_sequence'),
    first_name VARCHAR(30) NOT NULL,
    last_name  VARCHAR(30) NOT NULL,
    version    INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT pk_vet PRIMARY KEY (id),
    CONSTRAINT uq_vet_full_name UNIQUE (first_name, last_name)
);

CREATE TABLE vet_specialty
(
    vet_id       BIGINT NOT NULL,
    specialty_id BIGINT NOT NULL,
    CONSTRAINT pk_vet_specialty PRIMARY KEY (vet_id, specialty_id),
    CONSTRAINT fk_vet_specialty_vet FOREIGN KEY (vet_id) REFERENCES vet (id) ON DELETE CASCADE,
    CONSTRAINT fk_vet_specialty_specialty FOREIGN KEY (specialty_id) REFERENCES specialty (id)
);
