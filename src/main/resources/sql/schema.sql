CREATE TABLE IF NOT EXISTS Notes(
    id VARCHAR(60) DEFAULT RANDOM_UUID() PRIMARY KEY,
    version VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    content VARCHAR
);