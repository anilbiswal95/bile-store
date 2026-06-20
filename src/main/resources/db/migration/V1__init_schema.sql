-- Users
CREATE TABLE users (
 id BIGSERIAL PRIMARY KEY,
 email VARCHAR(255) NOT NULL UNIQUE,
 password VARCHAR(255) NOT NULL,
 full_name VARCHAR(255) NOT NULL,
 mobile VARCHAR(15),
 enabled BOOLEAN NOT NULL DEFAULT true,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
 user_id BIGINT NOT NULL REFERENCES users(id),
 role VARCHAR(50) NOT NULL,
 PRIMARY KEY (user_id, role)
);

-- Addresses
CREATE TABLE addresses (
 id BIGSERIAL PRIMARY KEY,
 user_id BIGINT NOT NULL REFERENCES users(id),
 street VARCHAR(255) NOT NULL,
 city VARCHAR(100) NOT NULL,
 state VARCHAR(100) NOT NULL,
 pin_code VARCHAR(10) NOT NULL,
 is_default BOOLEAN DEFAULT false,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories
CREATE TABLE categories (
 id BIGSERIAL PRIMARY KEY,
 name VARCHAR(100) NOT NULL UNIQUE,
 description TEXT,
 image_url VARCHAR(500),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products
CREATE TABLE products (
 id BIGSERIAL PRIMARY KEY,
 name VARCHAR(255) NOT NULL,
 description TEXT,
 price DECIMAL(10,2) NOT NULL,
 image_url VARCHAR(500),category_id BIGINT REFERENCES categories(id),
bike_model VARCHAR(100),
stock INT NOT NULL DEFAULT 0,
featured BOOLEAN DEFAULT false,
active BOOLEAN DEFAULT true,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_category ON products(category_id);
CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_product_price ON products(price);
CREATE INDEX idx_product_bike_model ON products(bike_model);
CREATE INDEX idx_product_featured ON products(featured);

-- Carts
CREATE TABLE carts (
 id BIGSERIAL PRIMARY KEY,
 user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cart Items
CREATE TABLE cart_items (
 id BIGSERIAL PRIMARY KEY,
 cart_id BIGINT NOT NULL REFERENCES carts(id),
 product_id BIGINT NOT NULL REFERENCES products(id),
 quantity INT NOT NULL DEFAULT 1,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cart_item_cart ON cart_items(cart_id);

-- Orders
CREATE TABLE orders (
 id BIGSERIAL PRIMARY KEY,
 order_number VARCHAR(50) NOT NULL UNIQUE,
 user_id BIGINT NOT NULL REFERENCES users(id),
 status VARCHAR(20) NOT NULL,
 total_amount DECIMAL(10,2) NOT NULL,
 shipping_address VARCHAR(500),
 payment_method VARCHAR(50),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_order_user ON orders(user_id);
CREATE INDEX idx_order_status ON orders(status);

-- Order Items
CREATE TABLE order_items (
 id BIGSERIAL PRIMARY KEY,
 order_id BIGINT NOT NULL REFERENCES orders(id),
 product_id BIGINT NOT NULL REFERENCES products(id),
 quantity INT NOT NULL,
 price DECIMAL(10,2) NOT NULL,
 product_name VARCHAR(255),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);