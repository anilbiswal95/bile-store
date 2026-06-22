-- Migration: Add admin user
-- Description: Insert default admin user for system access

-- Insert admin user (email: admin@bikestore.com, password: Admin@123)
-- Password is hashed using BCrypt: $2a$10$OETc3h9xIVJkAC9EJvN5.eUzwQtVf8gYjfKvuZqR/NxqYQ5pBPbzy
INSERT INTO users (email, password, full_name, mobile, enabled, created_at, updated_at)
VALUES ('admin@bikestore.com',
        '$2a$10$OETc3h9xIVJkAC9EJvN5.eUzwQtVf8gYjfKvuZqR/NxqYQ5pBPbzy',
        'Admin User',
        '+1-234-567-8900',
        true,
        NOW(),
        NOW())
ON CONFLICT (email) DO NOTHING;

-- Insert admin role for the admin user
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN'
FROM users
WHERE email = 'admin@bikestore.com'
ON CONFLICT DO NOTHING;

