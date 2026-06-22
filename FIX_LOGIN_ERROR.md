# 🔧 Quick Fix: Login Error When Accessing Admin Dashboard

## The Problem
```
Accessing http://localhost:8080/admin/dashboard
         ↓
Redirects to http://localhost:8080/login?error
         ↓
"What went wrong??"
```

## The Solution
This is **NOT an error**. This is Spring Security protecting the admin panel!

### 3-Step Fix

**Step 1**: Open login page
```
http://localhost:8080/login
```

**Step 2**: Enter admin credentials
```
Email:    admin@bikestore.com
Password: Admin@123
```

**Step 3**: Click Login button
```
✅ Success! You're now logged in
```

**Step 4**: Now access admin dashboard
```
http://localhost:8080/admin/dashboard
✅ Admin panel will load!
```

---

## Why This Happens

| What Happened | Why | Solution |
|---------------|-----|----------|
| Visited `/admin/dashboard` without login | Admin dashboard requires `ROLE_ADMIN` | Login first |
| Got redirected to `/login?error` | Spring Security protecting the endpoint | This is correct behavior |
| Login page shows credentials | We added error message display | Use provided credentials |

---

## If Login Still Fails

### Symptom: "Invalid email or password"
1. Check exact email (case-sensitive): `admin@bikestore.com`
2. Check exact password: `Admin@123` (with capital A and number 3)
3. Clear browser cookies: Press F12 → Application → Delete all cookies
4. Try again

### Symptom: Database error in logs
1. Restart the application
2. Database migrations should run automatically
3. Admin user should be created by `V5__add_admin_user.sql`

### Symptom: Still getting 403 after login
1. Application restart
2. Clear cookies
3. Login again
4. Try private/incognito window

---

## ✅ You're Good If You See

- Admin dashboard loads with tabs (Products, Categories, Orders)
- Your email shown in top-right corner
- Forms for adding products and categories work

---

## 🔗 For More Help

- Full guide: [`ADMIN_LOGIN_GUIDE.md`](ADMIN_LOGIN_GUIDE.md)
- General setup: [`QUICK_START.md`](QUICK_START.md)
- All documentation: [`INDEX.md`](INDEX.md)

---

**Remember**: The login page is preventing unauthorized access - which is exactly what we want! 🔐

