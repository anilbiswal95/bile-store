# Bike Store - Quick Reference Guide

## 🚀 Getting Started

### Step 0️⃣: Start the Application
```bash
mvn spring-boot:run
```

### Step 1️⃣: Login First (IMPORTANT!)
**Do NOT try to access /admin/dashboard without logging in first!**

1. Open browser: http://localhost:8080/login
2. Login with credentials:
   - **Email**: `admin@bikestore.com`
   - **Password**: `Admin@123`
3. Click "Login"
4. You'll be redirected to home page - **this is normal!**

### Step 3️⃣: Access Admin Dashboard
After successful login:
- **URL**: http://localhost:8080/admin/dashboard
- ✅ You should now see the admin panel!

### Step 4️⃣: Verify You're Logged In
Look for your email in the top-right corner of the page showing you're authenticated.

---

## ⚠️ Important: Why Do I Need to Login?

- `/admin/dashboard` requires `ROLE_ADMIN` role
- Spring Security protects it and redirects unauthenticated users to `/login`
- If you see `?error` in URL, just login with the admin credentials above
- **See**: [`ADMIN_LOGIN_GUIDE.md`](ADMIN_LOGIN_GUIDE.md) for detailed troubleshooting

---

## 📝 Getting Admin Credentials

### 3. Access Points
| Feature | URL | Role Required |
|---------|-----|---------------|
| Home | http://localhost:8080/ | Public |
| Register | http://localhost:8080/register | Public |
| Login | http://localhost:8080/login | Public |
| Products | http://localhost:8080/products | Public |
| Admin Dashboard | http://localhost:8080/admin/dashboard | Admin |
| Cart | http://localhost:8080/cart | Authenticated |
| Orders | http://localhost:8080/orders | Authenticated |

---

## 📌 Key Information

### Admin User Details (Created by Migration)
```
Email: admin@bikestore.com
Password: Admin@123 (BCrypt hashed)
Role: ROLE_ADMIN
```

### Admin Dashboard Features
1. **Products Tab**
   - Create new products
   - View all products with pagination
   - Delete products
   - Edit coming soon

2. **Categories Tab**
   - Create new categories
   - View all categories
   - Delete categories
   - Edit coming soon

3. **Orders Tab**
   - View and manage orders
   - Update order status

---

## 🔑 Important Database Details

### Users Table
```sql
SELECT * FROM users WHERE email = 'admin@bikestore.com';
```

### User Roles
```sql
SELECT u.email, ur.role 
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id;
```

### Orders
```sql
SELECT * FROM orders WHERE user_id = {user_id};
```

---

## 📧 Email Configuration

### For Gmail (Recommended for Testing)
1. Go to: https://myaccount.google.com/apppasswords
2. Generate 16-character app password
3. Update `application-local.properties`:
```properties
spring.mail.username=your-gmail@gmail.com
spring.mail.password=your-16-char-password
```

### For Other Email Providers
Update SMTP settings in `application-local.properties`:
```properties
spring.mail.host=your-smtp-host
spring.mail.port=587
spring.mail.username=your-email@domain.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🔐 API Endpoints Quick Reference

### Authentication
```
POST   /api/auth/register   - Register new user
POST   /api/auth/login      - Login
POST   /api/auth/logout     - Logout
```

### Admin Operations
```
POST   /api/admin/products              - Create product
PUT    /api/admin/products/{id}         - Update product
DELETE /api/admin/products/{id}         - Delete product

GET    /api/admin/categories            - List categories
POST   /api/admin/categories            - Create category
PUT    /api/admin/categories/{id}       - Update category
DELETE /api/admin/categories/{id}       - Delete category
```

### User Operations
```
POST   /api/cart/add                    - Add to cart
GET    /api/cart                        - Get cart
POST   /api/orders                      - Place order (triggers email!)
GET    /api/orders/{id}                 - Get order details
```

### Product Operations (Public)
```
GET    /api/products                    - List products
GET    /api/products/{id}               - Get product details
GET    /api/categories                  - List categories
```

---

## 🧪 Quick Test Commands

### Register a New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "password": "SecurePass123",
    "fullName": "John Doe",
    "mobile": "+1-555-1234"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@bikestore.com",
    "password": "Admin@123"
  }'
```

### Create a Product (Admin Only)
```bash
curl -X POST http://localhost:8080/api/admin/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mountain Bike Pro",
    "price": 1499.99,
    "stock": 10,
    "categoryId": 1,
    "bikeModel": "Trek X-Caliber 9.8",
    "description": "Professional mountain bike",
    "featured": true
  }'
```

### Create a Category (Admin Only)
```bash
curl -X POST http://localhost:8080/api/admin/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mountain Bikes",
    "description": "All-purpose mountain bikes",
    "imageUrl": "https://example.com/image.jpg"
  }'
```

### Delete a Product (Admin Only)
```bash
curl -X DELETE http://localhost:8080/api/admin/products/1
```

---

## 📂 Project Structure Overview

```
bike-store/
├── src/main/
│   ├── java/com/bike/store/
│   │   ├── admin/           ← Admin controllers
│   │   ├── common/          ← Email service, security config
│   │   ├── product/         ← Product & category services
│   │   ├── order/           ← Order service (with email)
│   │   ├── user/            ← User service, authentication
│   │   └── cart/            ← Cart service
│   └── resources/
│       ├── db/migration/    ← Database migrations
│       ├── templates/       ← HTML templates
│       │   ├── admin/       ← Admin panel
│       │   └── emails/      ← Email templates
│       └── application*.properties
├── pom.xml                  ← Maven configuration
├── FEATURES_IMPLEMENTATION.md
└── IMPLEMENTATION_SUMMARY.md
```

---

## 🔍 Troubleshooting

### Issue: Admin Dashboard Returns 403 Forbidden
**Solution**: 
1. Verify you're logged in as admin user
2. Check admin user has ROLE_ADMIN in database
3. Clear browser cookies and re-login

### Issue: Email Not Sending
**Solution**:
1. Check email configuration in `application-local.properties`
2. Verify SMTP credentials are correct
3. For Gmail, use App Password (not account password)
4. Check application logs for error messages
5. Verify firewall allows SMTP port 587

### Issue: "Product not found" when deleting
**Solution**:
1. Refresh the products list first (F5)
2. Verify product ID is correct
3. Check database if product still exists

### Issue: Registration fails with "Email already registered"
**Solution**:
1. Use a different email address
2. Or delete the user from database if testing

---

## 📋 Development Checklist

### Before Deployment
- [ ] Change admin password
- [ ] Configure production email settings
- [ ] Test email sending with real email
- [ ] Verify all admin endpoints are secured
- [ ] Run database migrations successfully
- [ ] Test user registration workflow
- [ ] Test order placement and email
- [ ] Review security configuration
- [ ] Update CORS settings if needed
- [ ] Test with different user roles

### During Development
- [ ] Keep admin credentials secure
- [ ] Don't commit sensitive data to Git
- [ ] Test all CRUD operations
- [ ] Verify email templates render correctly
- [ ] Check error messages are user-friendly
- [ ] Monitor application logs
- [ ] Test edge cases (empty cart, no products, etc.)

---

## 🎯 Next Steps

1. **Customize Admin Dashboard**
   - Add more admin features
   - Style according to brand guidelines
   - Add analytics/reporting

2. **Enhance Email Templates**
   - Add order tracking links
   - Include company branding
   - Add social media links

3. **Add More Features**
   - Product edit in UI
   - Bulk operations
   - User management
   - Analytics dashboard

4. **Security Hardening**
   - Add rate limiting
   - Implement CORS properly
   - Add API logging
   - Implement audit trails

---

## 📞 Resources

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Spring Security**: https://spring.io/projects/spring-security
- **Spring Mail**: https://spring.io/guides/gs/sending-email/
- **Thymeleaf**: https://www.thymeleaf.org/
- **Bootstrap**: https://getbootstrap.com/

---

## 📝 Notes

- All timestamps are stored in UTC
- Passwords are hashed with BCrypt (10 rounds)
- JWT tokens expire after 1 day (86400 seconds)
- Email sending is non-blocking (doesn't affect order creation)
- Admin operations are logged and audited

---

**Last Updated**: June 21, 2026
**Quick Start Version**: 1.0.0

