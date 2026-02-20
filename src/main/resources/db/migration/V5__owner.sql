CREATE SEQUENCE owner_sequence
    START WITH 1
    INCREMENT BY 1 NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE owner
(
    id        BIGINT       NOT NULL DEFAULT nextval('owner_sequence'),
    first_name VARCHAR(30) NOT NULL,
    last_name  VARCHAR(30) NOT NULL,
    address    VARCHAR(255) NOT NULL,
    telephone  VARCHAR(20) NOT NULL,
    city       VARCHAR(80) NOT NULL,
    version    INTEGER      NOT NULL DEFAULT 0,
    CONSTRAINT pk_owner PRIMARY KEY (id),
    CONSTRAINT uq_owner_full_name UNIQUE (first_name, last_name)
);
