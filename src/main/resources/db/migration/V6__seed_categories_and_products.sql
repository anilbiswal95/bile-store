-- V4__seed_categories_and_products.sql
-- Seed categories
INSERT INTO categories (id, name, description, image_url, created_at, updated_at)
VALUES
  (1, 'Accessories', 'Bike accessories and add-ons', '/images/categories/accessories.jpg', now(), now()),
  (2, 'Protection', 'Crash guards and protection gear', '/images/categories/protection.jpg', now(), now()),
  (3, 'Apparel', 'Riding apparel and packs', '/images/categories/apparel.jpg', now(), now())
ON CONFLICT (id) DO NOTHING;

-- Seed products (ensure category_id references the categories above)
INSERT INTO products (id, name, description, price, stock, category_id, bike_model, active, featured, created_at, updated_at)
VALUES
  (1, 'Adventure Pannier Set', 'Durable panniers for long trips', 249.99, 10, 1, 'ADV-100', true, true, now(), now()),
  (2, 'Crash Guard', 'Engine crash guard for protection', 129.50, 5, 2, 'ADV-100', true, false, now(), now()),
  (3, 'Hydration Pack 10L', 'Lightweight hydration pack with insulated bladder', 59.99, 25, 3, 'TRK-200', true, false, now(), now()),
  (4, 'LED Headlight Kit', 'High-lumen LED headlight with USB charging', 79.00, 40, 1, 'URB-50', true, true, now(), now()),
  (5, 'Performance Brake Pads', 'Ceramic brake pads for improved stopping power', 34.75, 60, 2, 'MTB-300', true, false, now(), now()),
  (6, 'Carbon Handlebar', 'Ergonomic carbon fiber handlebar, 31.8mm clamp', 129.99, 15, 1, 'RAC-500', true, true, now(), now()),
  (7, 'Comfort Gel Saddle', 'Wide gel saddle for long-distance comfort', 45.50, 30, 1, 'CRU-120', true, false, now(), now()),
  (8, 'Tubeless Tire Set 29\"', 'Durable tubeless-ready tires for off-road', 199.00, 12, 1, 'MTB-300', true, true, now(), now()),
  (9, 'Quick Release Skewer Set', 'Lightweight alloy quick release skewers, pair', 19.99, 80, 1, 'GEN-001', true, false, now(), now()),
  (10, 'Suspension Fork 120mm', 'Air-sprung suspension fork with lockout', 349.00, 8, 2, 'MTB-300', true, true, now(), now()),
  (11, 'Chain Lubricant 250ml', 'Weather-resistant chain lube for smooth shifting', 9.99, 150, 1, 'GEN-001', true, false, now(), now()),
  (12, 'Rear Rack Aluminum', 'Lightweight rear rack with 25kg capacity', 69.50, 22, 1, 'CRU-120', true, false, now(), now())
ON CONFLICT (id) DO NOTHING;
