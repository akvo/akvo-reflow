CREATE TABLE survey
 (id serial PRIMARY KEY,
  created_at bigint NOT NULL DEFAULT 0,
  payload jsonb NOT NULL,
  processed boolean NOT NULL DEFAULT FALSE);
--;;

CREATE UNIQUE INDEX survey_unique_payload
  ON survey (md5(payload::text));
--;;

CREATE TABLE form
 (id serial PRIMARY KEY,
  created_at bigint NOT NULL DEFAULT 0,
  payload jsonb NOT NULL,
  processed boolean NOT NULL DEFAULT FALSE);
--;;

CREATE UNIQUE INDEX form_unique_payload
  ON form (md5(payload::text));
--;;

CREATE TABLE question_group
 (id serial PRIMARY KEY,
  created_at bigint NOT NULL DEFAULT 0,
  payload jsonb NOT NULL,
  processed boolean NOT NULL DEFAULT FALSE);
--;;

CREATE UNIQUE INDEX question_group_unique_payload
  ON question_group (md5(payload::text));
--;;

CREATE TABLE question
 (id serial PRIMARY KEY,
  created_at bigint NOT NULL DEFAULT 0,
  payload jsonb NOT NULL,
  processed boolean NOT NULL DEFAULT FALSE);
--;;

CREATE UNIQUE INDEX question_unique_payload
  ON question (md5(payload::text));
--;;

CREATE TABLE device_file
 (id serial PRIMARY KEY,
  created_at bigint NOT NULL DEFAULT 0,
  payload jsonb NOT NULL,
  processed boolean NOT NULL DEFAULT FALSE);
--;;

CREATE UNIQUE INDEX device_file_unique_payload
  ON device_file (md5(payload::text));
--;;

CREATE TABLE data_point
 (id serial PRIMARY KEY,
  created_at bigint NOT NULL DEFAULT 0,
  payload jsonb NOT NULL,
  processed boolean NOT NULL DEFAULT FALSE);
--;;

CREATE UNIQUE INDEX data_point_unique_payload
  ON data_point (md5(payload::text));
--;;

CREATE TABLE form_instance
 (id serial PRIMARY KEY,
  created_at bigint NOT NULL DEFAULT 0,
  payload jsonb NOT NULL,
  processed boolean NOT NULL DEFAULT FALSE);
--;;

CREATE UNIQUE INDEX form_instance_unique_payload
  ON form_instance (md5(payload::text));
--;;

CREATE TABLE answer
 (id serial PRIMARY KEY,
  created_at bigint NOT NULL DEFAULT 0,
  payload jsonb NOT NULL,
  processed boolean NOT NULL DEFAULT FALSE);
--;;

CREATE UNIQUE INDEX answer_unique_payload
  ON answer (md5(payload::text));
--;;
