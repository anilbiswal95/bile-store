# 🔧 Fix: Admin User Exists But Role Is Missing

## The Problem (FOUND!)

✅ Admin user EXISTS in database:
```
Email: admin@bikestore.com
Password hash: $2a$10$OETc3h9xIVJkAC9EJvN5.eUzwQtVf8gYjfKvuZqR/NxqYQ5pBPbzy
Enabled: true
```

❌ But the **ROLE_ADMIN** is NOT assigned in the `user_roles` table!

That's why login fails - the user has no admin role.

---

## The Fix (1 Minute)

### Step 1: Check Current User Roles
```sql
SELECT * FROM user_roles WHERE user_id = 4;
```

**Expected result**: Empty (no rows) - This is the problem!

---

### Step 2: Add ROLE_ADMIN to Admin User

Run this SQL:

```sql
-- Insert ROLE_ADMIN for admin@bikestore.com user
INSERT INTO user_roles (user_id, role)
VALUES (4, 'ROLE_ADMIN');
```

---

### Step 3: Verify It Worked

```sql
-- Check user_roles table
SELECT user_id, role FROM user_roles WHERE user_id = 4;
```

**Expected result**:
```
user_id | role
   4    | ROLE_ADMIN
```

---

### Step 4: Restart Application

```bash
1. Stop app: Ctrl+C
2. Start app: mvn spring-boot:run
3. Clear browser cookies (F12 → Application → Delete cookies)
```

---

### Step 5: Try Login

- Email: `admin@bikestore.com`
- Password: `Admin@123`

✅ **Should work now!**

---

## Why This Happened

The migration `V5__add_admin_user.sql` uses:
```sql
ON CONFLICT (email) DO NOTHING;
```

This means:
- If user already exists → Skip insert
- But the role insert might not have run

So the user was created without the role being assigned.

---

## Verification

After login, you should see:
- Admin email in top-right corner
- Can access http://localhost:8080/admin/dashboard
- See Products, Categories, Orders tabs

---

## Summary of What You Need to Do

```sql
-- 1. Add the ROLE_ADMIN to admin user
INSERT INTO user_roles (user_id, role) VALUES (4, 'ROLE_ADMIN');

-- 2. Verify
SELECT * FROM user_roles WHERE user_id = 4;
```

Then restart the app and try login! ✅

---

**That's it! This small fix will get you in!**

