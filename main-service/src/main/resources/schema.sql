DROP TABLE IF EXISTS compilation_event;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS compilations;

CREATE TABLE IF NOT EXISTS users
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS categories
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    id                 SERIAL PRIMARY KEY,
    title              VARCHAR(120)                NOT NULL,
    annotation         VARCHAR(2000)               NOT NULL,
    description        VARCHAR(7000)               NOT NULL,
    category_id        BIGINT                      NOT NULL REFERENCES categories (id),
    event_date         TIMESTAMP WITH TIME ZONE    NOT NULL,
    initiator_id       BIGINT                      NOT NULL REFERENCES users (id),
    lat                FLOAT                       NOT NULL,
    lon                FLOAT                       NOT NULL,
    paid               BOOLEAN                     NOT NULL,
    participant_limit  BIGINT                      NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT TRUE,
    state              VARCHAR(100)                NOT NULL
);
CREATE TABLE IF NOT EXISTS requests
(
    id           SERIAL PRIMARY KEY,
    requester_id BIGINT                      NOT NULL REFERENCES users (id),
    event_id     BIGINT                      NOT NULL REFERENCES events (id),
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status       VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     SERIAL PRIMARY KEY,
    pinned BOOLEAN       NOT NULL,
    title  VARCHAR(5000) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    compilation_id BIGINT NOT NULL REFERENCES compilations (id) ON DELETE CASCADE,
    event_id       BIGINT NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);