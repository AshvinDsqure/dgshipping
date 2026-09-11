--
-- The contents of this file are subject to the license and copyright
-- detailed in the LICENSE and NOTICE files at the root of the source
-- tree and available online at
--
-- http://www.dspace.org/license/
--

-- Bulk Upload Process table for tracking async bulk upload operations

CREATE TABLE IF NOT EXISTS bulk_upload_process (
    uuid UUID PRIMARY KEY,
    legacy_id INT,
    process_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_rows INT DEFAULT 0,
    processed_rows INT DEFAULT 0,
    progress INT DEFAULT 0,
    message TEXT,
    csv_file_path VARCHAR(1000),
    validation_result_json TEXT,
    result_csv TEXT,
    eperson_uuid UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS bulk_upload_process_status_idx ON bulk_upload_process (status);
CREATE INDEX IF NOT EXISTS bulk_upload_process_eperson_idx ON bulk_upload_process (eperson_uuid);
