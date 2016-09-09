CREATE TABLE IF NOT EXISTS instance_status
 (id serial PRIMARY KEY,
  instance_id varchar(100) NOT NULL,
  created_at timestamptz DEFAULT now(),
  import_done boolean DEFAULT FALSE,
  export_done boolean DEFAULT FALSE,
  kind varchar(20),
  cursor varchar(255),
  process_status varchar(255) DEFAULT 'Import not started',
  error_status varchar(50),
  error_message text);

CREATE UNIQUE INDEX instance_id_unique_index
  ON instance_status (instance_id);
