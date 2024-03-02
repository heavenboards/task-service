CREATE TABLE IF NOT EXISTS task_entity
(
    id uuid PRIMARY KEY,
    name varchar(64) NOT NULL,
    description varchar(256),
    ordinal_number integer NOT NULL DEFAULT 1,
    group_id uuid NOT NULL,
    FOREIGN KEY (group_id)
        REFERENCES group_entity (id)
);
