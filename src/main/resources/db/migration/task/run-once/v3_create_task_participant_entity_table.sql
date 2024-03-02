CREATE TABLE IF NOT EXISTS task_participant_entity
(
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    task_id uuid NOT NULL,
    role varchar(64) NOT NULL,
    FOREIGN KEY (task_id)
        REFERENCES task_entity (id)
);
