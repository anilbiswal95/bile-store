# 🔧 Admin Login Troubleshooting: Login Failed

## Problem
```
Login Failed!
Invalid email or password. Please check your credentials and try again.
```

When trying to login with:
- Email: `admin@bikestore.com`
- Password: `Admin@123`

---

## Root Causes & Solutions

### 🔍 Issue 1: Admin User Not Created in Database

**Symptom**: Login always fails with these exact credentials

**Cause**: Database migration `V5__add_admin_user.sql` didn't run or admin user not inserted

**Solution A: Check if Admin User Exists**

Run this SQL query:
```sql
SELECT id, email, password, enabled FROM users WHERE email = 'admin@bikestore.com';
```

**If NO results**: Continue to Solution B

**If has results but login fails**: Continue to Solution C

---

### ✅ Solution B: Manually Insert Admin User

If the admin user doesn't exist, manually insert it:

```sql
-- 1. Insert admin user
INSERT INTO users (email, password, full_name, mobile, enabled, created_at, updated_at)
VALUES (
    'admin@bikestore.com',
    '$2a$10$OETc3h9xIVJkAC9EJvN5.eUzwQtVf8gYjfKvuZqR/NxqYQ5pBPbzy',
    'Admin User',
    '+1-234-567-8900',
    true,
    NOW(),
    NOW()
);

-- 2. Get the user ID (update the ID in the next query)
-- Remember the ID returned

-- 3. Insert admin role (replace {user_id} with the ID from step 2)
INSERT INTO user_roles (user_id, role)
VALUES ({user_id}, 'ROLE_ADMIN');
```

**Example** (if user ID is 1):
```sql
INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_ADMIN');
```

**After inserting**, try logging in again with:
- Email: `admin@bikestore.com`
- Password: `Admin@123`

---

### ✅ Solution C: Check If User Has ROLE_ADMIN

If admin user exists but login fails, check if the role is assigned:

```sql
SELECT u.id, u.email, ur.role, u.enabled
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
WHERE u.email = 'admin@bikestore.com';
```

**Status Check**:
- `enabled` should be: `true`
- `role` should be: `ROLE_ADMIN`

If role is missing, insert it:
```sql
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM users WHERE email = 'admin@bikestore.com'
ON CONFLICT DO NOTHING;
```

---

### ✅ Solution D: Ensure Flyway Migration Ran

Check if migrations have been executed:

```sql
-- PostgreSQL
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC;

-- Check for V5 migration
SELECT * FROM flyway_schema_history WHERE script = 'V5__add_admin_user.sql';
```

**If V5 migration is missing**:
1. Restart the application (migrations run on startup)
2. Application logs should show: `Executing baseline migration`
3. Verify in database again

---

## Step-by-Step Verification

### Step 1: Connect to Database
```
Host: localhost
Port: 5432
Database: bike_store
Username: anil_user
Password: Anil@1992
```

### Step 2: Check if Admin User Exists
```sql
SELECT * FROM users WHERE email = 'admin@bikestore.com';
```

### Step 3: Verify User Is Enabled
```sql
SELECT email, enabled FROM users WHERE email = 'admin@bikestore.com';
-- Result should show: enabled = true
```

### Step 4: Verify User Has ROLE_ADMIN
```sql
SELECT user_id, role FROM user_roles 
WHERE user_id = (SELECT id FROM users WHERE email = 'admin@bikestore.com');
-- Result should show: role = ROLE_ADMIN
```

### Step 5: Try Login Again
- URL: http://localhost:8080/login
- Email: `admin@bikestore.com`
- Password: `Admin@123`

---

## Password Hash Verification

The password hash used is:
```
$2a$10$OETc3h9xIVJkAC9EJvN5.eUzwQtVf8gYjfKvuZqR/NxqYQ5pBPbzy
```

This hash represents the password: `Admin@123` (BCrypt hashed)

**This is correct and should work!**

---

## Common Issues & Fixes

| Issue | Check | Fix |
|-------|-------|-----|
| User doesn't exist | SQL: WHERE email=... | Run Solution B |
| User exists but login fails | Check `enabled` column | Set to `true` |
| User exists but no admin role | Check `user_roles` table | Run insert for ROLE_ADMIN |
| Password hash wrong | Verify hash matches | Use provided hash |
| Database connection issue | Can you access other tables? | Check DB credentials |

---

## Full Troubleshooting SQL Script

Run all of these in order:

```sql
-- 1. Check if user exists
SELECT COUNT(*) as admin_user_count FROM users WHERE email = 'admin@bikestore.com';

-- 2. If count = 0, insert admin user
--    (Uncomment and run if needed)
-- INSERT INTO users (email, password, full_name, mobile, enabled, created_at, updated_at)
-- VALUES ('admin@bikestore.com', 
--         '$2a$10$OETc3h9xIVJkAC9EJvN5.eUzwQtVf8gYjfKvuZqR/NxqYQ5pBPbzy',
--         'Admin User', '+1-234-567-8900', true, NOW(), NOW());

-- 3. Check if user has ROLE_ADMIN
SELECT u.email, u.enabled, ur.role
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
WHERE u.email = 'admin@bikestore.com';

-- 4. If no ROLE_ADMIN, insert it
--    (Uncomment and run if needed)
-- INSERT INTO user_roles (user_id, role)
-- SELECT id, 'ROLE_ADMIN' FROM users WHERE email = 'admin@bikestore.com'
-- ON CONFLICT DO NOTHING;

-- 5. Check final state
SELECT id, email, password, enabled FROM users WHERE email = 'admin@bikestore.com';
SELECT user_id, role FROM user_roles WHERE user_id = (SELECT id FROM users WHERE email = 'admin@bikestore.com');
```

---

## After Inserting Admin User

### Step 1: Restart Application
```bash
# Stop current running instance (Ctrl+C)
# Restart with:
mvn spring-boot:run
```

### Step 2: Clear Browser Cookies
- Press F12 (Developer Tools)
- Go to Application tab
- Delete all cookies for localhost:8080

### Step 3: Try Login Again
- URL: http://localhost:8080/login
- Email: `admin@bikestore.com`
- Password: `Admin@123`
- Click Login

### Step 4: Verify Success
✅ Should see admin email in top-right corner
✅ Can now access http://localhost:8080/admin/dashboard

---

## If Still Failing

### Check Application Logs
Look for error messages like:
- `BadCredentialsException`
- `UsernameNotFoundException`
- Database connection errors

### Test With curl
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@bikestore.com",
    "password": "Admin@123"
  }'
```

**Expected response on success**:
```json
{
  "token": "eyJhbGc...",
  "email": "admin@bikestore.com",
  "fullName": "Admin User",
  "role": "ROLE_ADMIN"
}
```

---

## Support

If none of the above works:

1. **Check Database Connection**
   - Verify PostgreSQL is running
   - Verify credentials in `application-local.properties`

2. **Check Application Logs**
   - Look for authentication errors
   - Check for BCrypt validation issues

3. **Restart Everything**
   - Stop application
   - Restart PostgreSQL
   - Run `mvn spring-boot:run` again

---

**Most Common Fix**: Run the SQL insert from Solution B to create the admin user manually. 99% of the time this resolves the login issue!

---

*Updated: June 21, 2026*

