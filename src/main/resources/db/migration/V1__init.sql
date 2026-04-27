CREATE TABLE authors (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    birth_date  DATE         NOT NULL CHECK (birth_date <= CURRENT_DATE)
);

CREATE TYPE publication_status AS ENUM ('UNPUBLISHED', 'PUBLISHED');
CREATE TABLE books (
    id                  BIGSERIAL          PRIMARY KEY,
    title               VARCHAR(255)       NOT NULL,
    price               INTEGER            NOT NULL CHECK (price >= 0),
    publication_status  publication_status NOT NULL
);

CREATE TABLE book_authors (
    book_id    BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    author_id  BIGINT NOT NULL REFERENCES authors(id),
    PRIMARY KEY (book_id, author_id)
);
CREATE INDEX idx_book_authors_author_id ON book_authors(author_id);
