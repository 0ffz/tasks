-- fun getOrCreateUserId(name: String)
INSERT INTO users (name)
VALUES (:name)
ON CONFLICT DO NOTHING
RETURNING id;