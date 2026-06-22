# ⚡ QUICK FIX: Login Failed - 2 Minutes

## The Problem
Admin user (`admin@bikestore.com`) doesn't exist in the database yet.

## The Solution (Choose One)

### Option 1: Automatic (Restart App) - 30 seconds

**Step 1**: Restart application
```bash
# Stop current instance (press Ctrl+C)
# Start again:
mvn spring-boot:run
```

**Step 2**: Wait for logs to show migrations completed

**Step 3**: Try login again
- Email: `admin@bikestore.com`
- Password: `Admin@123`

✅ **Should work!** (The flyway migration V5 creates the admin user)

---

### Option 2: Manual Insert - 1 Minute

**Step 1**: Open your database tool (DBeaver, pgAdmin, etc.)

**Step 2**: Connect to database:
```
Host: localhost
Port: 5432
Database: bike_store
User: anil_user
Password: Anil@1992
```

**Step 3**: Run this SQL:
```sql
-- Insert admin user
INSERT INTO users (email, password, full_name, mobile, enabled, created_at, updated_at)
VALUES ('admin@bikestore.com', 
        '$2a$10$OETc3h9xIVJkAC9EJvN5.eUzwQtVf8gYjfKvuZqR/NxqYQ5pBPbzy',
        'Admin User', 
        '+1-234-567-8900', 
        true, 
        NOW(), 
        NOW());

-- Get the ID and use it below (probably 1 if first user)
-- Then insert the admin role

INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM users WHERE email = 'admin@bikestore.com';
```

**Step 4**: Try login
- Email: `admin@bikestore.com`
- Password: `Admin@123`

✅ **Should work!**

---

## Verify It Worked

### In Database
```sql
SELECT u.email, u.enabled, ur.role 
FROM users u 
LEFT JOIN user_roles ur ON u.id = ur.user_id 
WHERE u.email = 'admin@bikestore.com';
```

**Should show**:
```
email                  | enabled | role
admin@bikestore.com    | true    | ROLE_ADMIN
```

### In Browser
After login:
- ✅ See admin email in top-right corner
- ✅ Can access `/admin/dashboard`
- ✅ See Products, Categories, Orders tabs

---

## Why This Happened

The database migration `V5__add_admin_user.sql` creates the admin user on application startup. If you started the app before this file was created, the migration didn't run.

**Solution**: Restart the application and migrations will run automatically!

---

## Still Not Working?

See: [`LOGIN_FAILED_TROUBLESHOOTING.md`](LOGIN_FAILED_TROUBLESHOOTING.md) - Complete troubleshooting guide

---

**99% of the time**, just restart the app or run the SQL insert above and you're done! ✅

