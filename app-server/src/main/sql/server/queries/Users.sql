-- fun insertUser(name: String)
INSERT INTO users (name)
VALUES (:name)
ON CONFLICT DO NOTHING;
-- RETURNING id;

-- fun getUserId(name: String)
SELECT id
FROM users
WHERE name = :name;