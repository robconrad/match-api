
CREATE TABLE evolutions (
  id                        BIGINT          NOT NULL UNIQUE,
  created_at                TIMESTAMP       NOT NULL
);

CREATE TABLE api_keys (
  id                        BIGSERIAL       UNIQUE,
  uuid                      UUID            NOT NULL,
  "value"                   VARCHAR(60)     NOT NULL,
  created_at                TIMESTAMP       NOT NULL,
  active                    BOOLEAN         NOT NULL DEFAULT TRUE,
  CONSTRAINT u_api_keys_value               UNIQUE ("value"),
  CONSTRAINT u_api_keys_uuid                UNIQUE (uuid),
  PRIMARY KEY (id)
);

CREATE TABLE users (
  id                        BIGSERIAL       UNIQUE,
  uuid                      UUID            NOT NULL,
  email                     VARCHAR(255)    NOT NULL,
  password_hash             VARCHAR(64)     NOT NULL,
  password_salt             VARCHAR(64)     NOT NULL,
  reset_code                VARCHAR(32)     DEFAULT NULL,
  reset_expires_at          TIMESTAMP       DEFAULT NULL,
  created_at                TIMESTAMP       NOT NULL,
  active                    BOOLEAN         NOT NULL DEFAULT TRUE,
  CONSTRAINT u_users_uuid                   UNIQUE (uuid),
  PRIMARY KEY (id)
);
