CREATE TABLE notes
(
    id   BLOB PRIMARY KEY,
    data BLOB
) STRICT;


CREATE VIEW mutators_json AS
SELECT json(data), length(data)
FROM mutators