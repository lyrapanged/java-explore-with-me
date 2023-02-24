DROP TABLE IF EXISTS stat;
DROP TABLE IF EXISTS app;

CREATE TABLE IF NOT EXISTS app
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS stat
(
    id        BIGSERIAL PRIMARY KEY,
    app_id    BIGINT REFERENCES app (id) ON DELETE CASCADE NOT NULL,
    uri       VARCHAR(100)                                 NOT NULL,
    ip        VARCHAR(100)                                 NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE
);
