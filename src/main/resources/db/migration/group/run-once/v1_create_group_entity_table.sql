CREATE TABLE IF NOT EXISTS group_entity
(
    id uuid PRIMARY KEY,
    name varchar(64) NOT NULL,
    board_id uuid NOT NULL,
    position_weight integer NOT NULL
);
