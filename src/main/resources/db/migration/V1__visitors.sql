DROP TABLE IF EXISTS visitors;

CREATE TABLE visitors (
  id      BIGSERIAL PRIMARY KEY NOT NULL,
  "name" TEXT                  NOT NULL
);