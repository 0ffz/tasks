-- fun getOrCreateUserId(name: String)
INSERT INTO users (id, name)
VALUES (?, :name)
ON CONFLICT DO NOTHING
RETURNING id;