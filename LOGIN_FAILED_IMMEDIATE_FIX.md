# 🎯 LOGIN FAILED - IMMEDIATE ACTION REQUIRED

**The good news**: This is a quick fix! ✅

## What Happened
The admin user `admin@bikestore.com` doesn't exist in the database yet.

## Why
The database migration that creates the admin user (`V5__add_admin_user.sql`) probably didn't run on your first startup.

---

## ⚡ INSTANT FIX (Choose 1):

### FIX 1: Restart Application (Easiest - 30 seconds)
```bash
1. Stop application (Ctrl+C in terminal)
2. Run: mvn spring-boot:run
3. Wait for startup to complete
4. Try login again with admin@bikestore.com / Admin@123
```

**Why**: Migrations run automatically on startup

---

### FIX 2: Manually Create Admin User (1 minute)

**Step 1**: Open your database client (DBeaver, pgAdmin, etc.)

**Step 2**: Connect with:
```
Host: localhost
Port: 5432
Database: bike_store
Username: anil_user
Password: Anil@1992
```

**Step 3**: Copy & paste this SQL (run all 4 parts):

```sql
-- PART 1: Check if user already exists
SELECT COUNT(*) as exists FROM users WHERE email = 'admin@bikestore.com';

-- PART 2: If count = 0, insert admin user
INSERT INTO users (email, password, full_name, mobile, enabled, created_at, updated_at)
VALUES ('admin@bikestore.com', 
        '$2a$10$OETc3h9xIVJkAC9EJvN5.eUzwQtVf8gYjfKvuZqR/NxqYQ5pBPbzy',
        'Admin User', 
        '+1-234-567-8900', 
        true, 
        NOW(), 
        NOW());

-- PART 3: Verify insert worked
SELECT id, email, enabled FROM users WHERE email = 'admin@bikestore.com';

-- PART 4: Insert ROLE_ADMIN (use the id from part 3)
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM users WHERE email = 'admin@bikestore.com';
```

**Step 4**: Try login at http://localhost:8080/login
- Email: `admin@bikestore.com`
- Password: `Admin@123`

✅ **Should work now!**

---

## ✅ Verify It Worked

After login, you should see:
- Admin email in top-right corner
- Can access http://localhost:8080/admin/dashboard
- See Products, Categories, Orders tabs

---

## 📖 For More Detailed Help

If the above doesn't work, see:
- [`QUICK_LOGIN_FIX.md`](QUICK_LOGIN_FIX.md) - Extended 2-minute fix
- [`LOGIN_FAILED_TROUBLESHOOTING.md`](LOGIN_FAILED_TROUBLESHOOTING.md) - Complete troubleshooting

---

## What NOT To Do

❌ Don't change the password
❌ Don't modify the email
❌ Don't use different credentials

**Use exactly**:
- Email: `admin@bikestore.com`
- Password: `Admin@123`

---

## 🚀 Next Steps

1. **Try FIX 1** (restart app) - takes 30 seconds
2. **If that doesn't work**, try **FIX 2** (manual SQL) - takes 1 minute
3. **If still failing**, check [`LOGIN_FAILED_TROUBLESHOOTING.md`](LOGIN_FAILED_TROUBLESHOOTING.md)

---

**You've got this!** Choose one fix above and you'll be logged in in minutes! ✅

