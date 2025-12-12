-- fun insertUser(name: String)
INSERT INTO users (name)
VALUES (:name)
ON CONFLICT DO NOTHING;

-- fun getUserId(name: String)
SELECT id
FROM users
WHERE name = :name;