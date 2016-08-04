CREATE TABLE IF NOT EXISTS instance_status
 (id serial PRIMARY KEY,
  instance_id varchar NOT NULL,
  created_at timestamptz DEFAULT now(),
  import_done boolean DEFAULT FALSE,
  export_done boolean DEFAULT FALSE);

CREATE UNIQUE INDEX IF NOT EXISTS instance_id_unique_index
  ON instance_status (instance_id);
