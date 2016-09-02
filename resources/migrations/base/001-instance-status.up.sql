CREATE TABLE IF NOT EXISTS instance_status
 (id serial PRIMARY KEY,
  instance_id varchar(100) NOT NULL,
  created_at timestamptz DEFAULT now(),
  import_done boolean DEFAULT FALSE,
  export_done boolean DEFAULT FALSE,
  kind varchar(20),
  cursor varchar(255),
  error_status smallint,
  error_message text);

CREATE UNIQUE INDEX instance_id_unique_index
  ON instance_status (instance_id);
