CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(12,2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    category_id BIGINT,
    bike_model VARCHAR(100),
    active BOOLEAN DEFAULT true,
    featured BOOLEAN DEFAULT false,
    image_url VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- optional seed data
INSERT INTO products (id, name, description, price, stock, category_id, bike_model, active, featured)
VALUES
    (1, 'Adventure Pannier Set', 'Durable panniers for long trips', 249.99, 10, NULL, 'ADV-100', true, true),
    (2, 'Crash Guard', 'Engine crash guard for protection', 129.50, 5, NULL, 'ADV-100', true, false)
ON CONFLICT (id) DO NOTHING;
