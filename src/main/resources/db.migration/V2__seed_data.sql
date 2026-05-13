-- Admin user (password: Admin123)
INSERT INTO users (email, password, full_name, mobile, enabled) VALUES
('admin@bikestore.com', 'S2a510SN9q8oU1OicKqx2MRz0MyeJjZagcF17p92ldGxad68LJZdL171hWy', 'Admin User', '99999999999', true);

--INSERT INTO user_roles (user_id, role) VALUES ((1, 'ROLE_ADMIN));
INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_ADMIN');

-- Categories
INSERT INTO categories (name, description) VALUES
('Panniers & Luggage', 'Saddlebags, top cases, and luggage solutions'),
('Crash Protection', 'Crash guards, frame sliders, and engine protection'),
('Lighting', 'Auxiliary lights, LED upgrades, and indicators'),
('Exhaust Systems', 'Performance and aftermarket exhaust systems'),
('Comfort & Ergonomics', 'Seats, handlebar risers, and wind screens'),
('Electronics', 'GPS mounts, USB chargers, and communication systems');

-- Products
INSERT INTO products (name, description, price, image_url, category_id, bike_model, stock, featured, active) VALUES
('SW-Mottech TRAX ADV Panniers 45L', 'Premium aluminium panniers with 45L capacity. Waterproof and impact resistant.', 42999.00, '/images/pannierstrax.jpg', 1, 'Royal Enfield Himalayan', 25, true, true),
('Civil Trekker Outback Top Case 58L', 'Large capacity top case with MONKEY mounting system.', 35999.00, '/images/topcase-givi.jpg', 1, 'BMW R1250GS', 15, true, true),
('RDM Motor Crash Guard - Steel', 'Heavy duty steel crash guard with engine protection.', 8999.00, '/images/crashguard-rd.jpg', 2, 'Royal Enfield Classic 350', 50, true, true),
('Denali D4 LED Auxiliary Lights', 'High performance LED auxiliary light, 4200 lumens per light.', 18999.00, '/images/lightdenali.jpg', 3, 'Universal', 40, true, true),
('Akrapovic Slip-On Exhaust', 'Titanium slip-on exhaust with carbon end cap. ECE approved.', 65000.00, '/images/exhaust-akrap.jpg', 4, 'Kawasaki Z900', 10, true, true),
('Puig Touring Windscreen', 'Tall touring windscreen for improved wind protection.', 7999.00, '/images/windscreen-puig.jpg', 5, 'Suzuki V-Strom 650', 35, false, true),
('RAM X-Grip Phone Mount', 'Universal phone mount with X-Grip holder and ball mount.', 3499.00, '/images/mount-ram.jpg', 6, 'Universal', 100, false, true),
('Hepco & Becker Xplorer Panniers 40L', 'Rugged aluminum side cases with quick-lock system.', 38999.00, '/images/panniers-hepco.jpg', 1, 'Honda Africa Twin', 20, false, true),
('Oxford Heated Grips', 'Premium heated grips with 5 heat settings and LED indicator.', 4999.00, '/images/grips-oxford.jpg', 5, 'Universal', 60, false, true),
('Cardo Packtalk Edge', 'Premium mesh communication system, 1600m range.', 32999.00, '/images/cardo-packtalk.jpg', 6, 'Universal', 25, true, true),
('Zeta Pro Armor Hand Guards', 'Wrap-around hand guards with aluminum backbone.', 5499.00, '/images/handguard-zeta.jpg', 2, 'Universal', 45, false, true);


