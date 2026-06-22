# 🔑 Admin Dashboard Access - Setup Guide

## ❌ Problem You're Experiencing

When you access `http://localhost:8080/admin/dashboard`, you're getting redirected to `http://localhost:8080/login?error`

### Why This Happens

This is **normal and expected behavior** because:
- The admin dashboard requires authentication (you must be logged in)
- You must have the `ROLE_ADMIN` role
- Without proper login, Spring Security redirects you to the login page

---

## ✅ Solution: Login First, Then Access Admin Dashboard

### Step 1: Go to Login Page
```
URL: http://localhost:8080/login
```

### Step 2: Use Admin Credentials
```
Email:    admin@bikestore.com
Password: Admin@123
```

### Step 3: Click Login Button
- You will be logged in and receive a JWT token (stored in cookie)
- You'll be redirected to home page `/`

### Step 4: Access Admin Dashboard
```
URL: http://localhost:8080/admin/dashboard
```
✅ Now you should see the admin dashboard!

---

## 🚨 Troubleshooting

### Issue 1: Login Still Fails with Error
**Solution:**
1. Check that you're using the exact credentials (case-sensitive):
   - Email: `admin@bikestore.com` (lowercase)
   - Password: `Admin@123` (exact capitalization)

2. Verify the admin user exists in database:
```sql
SELECT * FROM users WHERE email = 'admin@bikestore.com';
SELECT * FROM user_roles WHERE user_id = (SELECT id FROM users WHERE email = 'admin@bikestore.com');
```

3. Restart the application (migrations might not have run)

### Issue 2: Admin Dashboard Still Shows 403 Forbidden
**After successful login**, if you still get 403:
1. Clear browser cookies (press F12, Application tab, delete cookies)
2. Logout and login again
3. Restart the application
4. Check that database has ROLE_ADMIN for your user

### Issue 3: Database Doesn't Have Admin User
**If the migration didn't run:**
1. Manually insert the admin user:
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

-- Insert admin role
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM users WHERE email = 'admin@bikestore.com';
```

---

## 🔄 Complete Login & Access Flow

```
Step 1: User visits http://localhost:8080/admin/dashboard
          ↓
          Spring Security checks: Are you authenticated?
          ↓
Step 2a: NO → Redirect to /login?error
          ↓
          User enters email & password
          ↓
Step 2b: Authentication succeeds → JWT token created
          ↓
Step 3: Token stored in HTTP-only cookie
          ↓
Step 4: User redirected to home page (/)
          ↓
Step 5: User visits http://localhost:8080/admin/dashboard
          ↓
          Spring Security checks:
          - Is user authenticated? YES (has token)
          - Does user have ROLE_ADMIN? YES
          ↓
Step 6: ✅ Admin dashboard displayed!
```

---

## 📝 Important Notes

### How Authentication Works
1. When you login with email/password, the `AuthController` authenticates you
2. A JWT token is created and stored in an HTTP-only cookie
3. On every request, `JwtAuthFilter` reads the token and sets authentication
4. If token is invalid or missing for protected routes → redirect to login

### Admin Dashboard Protection
```java
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")  // ← Requires ROLE_ADMIN
public class AdminController { ... }
```

### Token Validity
- JWT tokens expire after 1 day (86400 seconds)
- Stored in secure HTTP-only cookies
- Automatically sent with each request

---

## 🧪 Quick Test

### Using curl to test login:
```bash
# Step 1: Login and get token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@bikestore.com",
    "password": "Admin@123"
  }' \
  -c cookies.txt

# Step 2: Access admin API (token in cookies)
curl http://localhost:8080/api/admin/categories \
  -b cookies.txt
```

---

## 💡 Key Takeaways

✅ **This is NOT an error** - it's the security system working correctly
✅ **You MUST login first** - no unauthenticated access to admin panel
✅ **Use admin credentials** - `admin@bikestore.com` / `Admin@123`
✅ **Cookie-based auth** - token saved in secure HTTP-only cookie
✅ **Auto-redirect** - Spring Security automatically redirects to login

---

## 🔗 Related URLs

| URL | Purpose | Status |
|-----|---------|--------|
| `/login` | Login page | ✅ Public |
| `/register` | Register new user | ✅ Public |
| `/admin/dashboard` | Admin panel | 🔒 ADMIN only |
| `/api/auth/login` | Login API | ✅ Public |
| `/api/admin/**` | Admin APIs | 🔒 ADMIN only |

---

## ✨ What You Can Do After Login

Once logged in as admin, you can:
- ✅ Access http://localhost:8080/admin/dashboard
- ✅ Create products via form or API
- ✅ Create categories via form or API
- ✅ Delete products
- ✅ Delete categories
- ✅ View and manage orders
- ✅ See all registered users' orders

---

## 📞 Still Having Issues?

1. **Check logs** - Look for authentication errors
2. **Verify database** - Ensure admin user exists with ROLE_ADMIN
3. **Clear cookies** - Delete all browser cookies and login again
4. **Restart app** - Sometimes helps with cache/migration issues
5. **Check credentials** - Ensure exact email and password match

---

**Remember:** The `?error` parameter is your security system protecting the admin panel from unauthorized access! Simply login with the admin credentials to gain access. 🔐

**Updated**: June 21, 2026

