CREATE TABLE events (
  id serial PRIMARY KEY,
  payload jsonb NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  processed boolean NOT NULL DEFAULT FALSE
);
--;;

CREATE UNIQUE INDEX events_unique_payload
  ON events (md5(payload::text));
--;;
