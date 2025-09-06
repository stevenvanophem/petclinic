CREATE TABLE journal
(
    id           BIGSERIAL PRIMARY KEY,
    journal_type VARCHAR(100) NOT NULL,
    payload_type VARCHAR(100) NOT NULL,
    payload_json JSONB        NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);
