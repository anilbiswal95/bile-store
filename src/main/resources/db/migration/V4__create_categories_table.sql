CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- optional seed data
INSERT INTO categories (id, name, description)
VALUES
    (1, 'Luggage', 'Storage solutions for bikes'),
    (2, 'Protection', 'Protective gear and accessories')
ON CONFLICT (id) DO NOTHING;
