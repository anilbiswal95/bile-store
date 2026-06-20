# Bike Store - New Features Implementation Guide

## Overview
This document describes the three new features that have been implemented in the Bike Store application:

1. **Admin Panel** for Product and Category Management (CRUD operations)
2. **User Registration & Login** with Database Storage
3. **Email Notifications** on Order Placement

---

## Feature 1: Admin Panel - Product and Category Management

### Access
- **URL**: `http://localhost:8080/admin/dashboard`
- **Required Role**: `ROLE_ADMIN`
- **Default Admin Credentials**:
  - Email: `admin@bikestore.com`
  - Password: `Admin@123`

### Functionality

#### Products Management
- **Create**: Add new products with name, price, stock, category, bike model, description, and featured status
- **View**: List all products with pagination
- **Update**: Edit existing product details (coming soon in UI)
- **Delete**: Remove products from the catalog

**API Endpoints**:
```
POST   /api/admin/products           - Create product
PUT    /api/admin/products/{id}      - Update product
DELETE /api/admin/products/{id}      - Delete product
```

#### Categories Management
- **Create**: Add new product categories
- **View**: List all categories
- **Update**: Edit category details (coming soon in UI)
- **Delete**: Remove categories

**API Endpoints**:
```
GET    /api/admin/categories         - List all categories
GET    /api/admin/categories/{id}    - Get category by ID
POST   /api/admin/categories         - Create category
PUT    /api/admin/categories/{id}    - Update category
DELETE /api/admin/categories/{id}    - Delete category
```

### Dashboard Interface
The admin dashboard (`/admin/dashboard`) provides:
- **Tabbed Interface**: Separate tabs for Products, Categories, and Orders
- **Add Forms**: Quick add forms for both products and categories
- **Management Tables**: View and manage all items with Action buttons
- **Real-time Updates**: Items update immediately after add/delete operations
- **Responsive Design**: Bootstrap-based responsive layout

---

## Feature 2: User Registration & Login with Database Storage

### Registration
- **URL**: `http://localhost:8080/register`
- **Required Fields**:
  - Email (unique)
  - Password (hashed with BCrypt)
  - Full Name
  - Mobile (optional)

### Login
- **URL**: `http://localhost:8080/login`
- **Authentication Method**: JWT token stored as HTTP-only cookie
- **Default Role**: `ROLE_USER` for all new registrations

### Database Storage
All user details are automatically stored in the `users` table:
- `id` - Primary key
- `email` - Unique identifier
- `password` - BCrypt hashed password
- `full_name` - User's full name
- `mobile` - Contact number
- `enabled` - Account status
- `created_at` - Registration timestamp
- `updated_at` - Last modification timestamp

**User Roles Table**: `user_roles`
- `user_id` - Foreign key to users
- `role` - Role enum (ROLE_USER, ROLE_ADMIN)

### API Endpoints
```
POST /api/auth/register  - Register new user
POST /api/auth/login     - Login user
POST /api/auth/logout    - Logout user
```

---

## Feature 3: Email Notifications on Order Placement

### Configuration
Update your `application-local.properties` with your SMTP settings:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=false
```

**For Gmail**:
1. Enable 2-Factor Authentication
2. Generate an App Password: https://myaccount.google.com/apppasswords
3. Use the 16-character password in `spring.mail.password`

### Email Template
The order confirmation email includes:
- Customer name
- Order number
- Order date
- Ordered items with quantities and prices
- Total amount
- Shipping address
- Payment method
- Professional HTML styling

**Template Location**: `src/main/resources/templates/emails/order-confirmation.html`

### How It Works
1. When a user places an order via `/api/orders`
2. OrderService automatically triggers email sending
3. Thymeleaf renders the email template with order details
4. Email is sent asynchronously (doesn't block order creation)
5. If email fails, order is still created successfully (graceful degradation)

### API Endpoint
```
POST /api/orders - Place new order (triggers email)
```

---

## Database Migrations

### Migration Scripts

#### V5__add_admin_user.sql
Creates the default admin user:
- **Email**: admin@bikestore.com
- **Password**: Admin@123 (BCrypt hashed)
- **Role**: ROLE_ADMIN
- **Mobile**: +1-234-567-8900

**To run migrations**:
- Migrations run automatically on application startup
- Flyway manages all schema updates
- Baseline version: 1

---

## Project Structure

### New Files Created

```
src/main/java/com/bike/store/
├── common/
│   ├── config/
│   │   └── SecurityConfig.java          (Updated with admin routes)
│   └── email/
│       └── EmailService.java            (Email sending service)
├── product/
│   ├── service/
│   │   └── CategoryService.java         (Category CRUD operations)
│   ├── dto/
│   │   ├── CategoryCreateUpdateDto.java
│   │   └── CategoryResponseDto.java
│   └── controller/
│       └── (existing ProductController)
├── admin/
│   ├── controller/
│   │   ├── AdminController.java         (UI routing)
│   │   └── AdminApiController.java      (API endpoints - updated)
│   └── (dashboard.html updated)
└── order/
    └── service/
        └── OrderService.java            (Updated with email integration)

src/main/resources/
├── db/migration/
│   └── V5__add_admin_user.sql
├── templates/
│   ├── admin/
│   │   └── dashboard.html               (Enhanced admin panel)
│   └── emails/
│       └── order-confirmation.html
└── application-local.properties          (Email configuration)
```

### Modified Files

```
pom.xml                              (Added spring-boot-starter-mail)
SecurityConfig.java                  (Added /admin/** and /api/admin/** routes)
ProductService.java                  (Added deleteProduct method)
OrderService.java                    (Added email sending)
AdminApiController.java              (Added category endpoints and delete operations)
```

---

## Security Configuration

### Authorization Rules

| Route | Access | Required Role |
|-------|--------|---------------|
| `/` | Public | None |
| `/login`, `/register` | Public | None |
| `/products/**` | Public | None |
| `/api/auth/**` | Public | None |
| `/admin/**` | Restricted | ROLE_ADMIN |
| `/api/admin/**` | Restricted | ROLE_ADMIN |
| `/cart`, `/orders` | Authenticated | ROLE_USER or ROLE_ADMIN |
| `/api/cart/**`, `/api/orders/**` | Authenticated | ROLE_USER or ROLE_ADMIN |

### Method-Level Security
- `@PreAuthorize("hasRole('ADMIN')")` annotations on admin controllers
- SecondaryDefault authorization at class and method level
- @EnableGlobalMethodSecurity(prePostEnabled = true) enabled

---

## Testing the Features

### 1. Test Admin Access
```bash
# Login with admin credentials
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@bikestore.com",
    "password": "Admin@123"
  }'

# Access admin dashboard
curl -H "Cookie: token=<JWT_TOKEN>" \
  http://localhost:8080/admin/dashboard
```

### 2. Test Product Management
```bash
# Create a product (admin only)
curl -X POST http://localhost:8080/api/admin/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mountain Bike",
    "price": 1299.99,
    "stock": 50,
    "categoryId": 1,
    "bikeModel": "Trek X-Caliber",
    "description": "High-performance mountain bike",
    "featured": true
  }'

# Delete a product
curl -X DELETE http://localhost:8080/api/admin/products/1
```

### 3. Test User Registration
```bash
# Register new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Pass@123",
    "fullName": "John Doe",
    "mobile": "+1-555-1234"
  }'
```

### 4. Test Order Email
```bash
# Place an order (requires authentication)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Cookie: token=<JWT_TOKEN>" \
  -d '{
    "shippingAddress": "123 Main St, City, State 12345",
    "paymentMethod": "Credit Card"
  }'
```

---

## Troubleshooting

### Email Not Sending
1. Check email configuration in `application-local.properties`
2. Verify SMTP server credentials
3. For Gmail, use App Password (not account password)
4. Check firewall/network for SMTP port 587
5. Review application logs for error messages

### Admin Not Able to Access Dashboard
1. Verify user has `ROLE_ADMIN` in database
2. Check that user is logged in (verify JWT token in cookies)
3. Ensure `@PreAuthorize("hasRole('ADMIN')")` is properly configured
4. Check SecurityConfig for `/admin/**` routing

### Products Not Showing in Admin Panel
1. Check browser console for JavaScript errors
2. Verify `/api/products` endpoint is accessible
3. Ensure database has at least one category
4. Check that products exist in database

---

## Future Enhancements

### Planned Features
- [ ] Product edit functionality in admin UI
- [ ] Category edit functionality in admin UI
- [ ] User management interface
- [ ] Email templates for other events (password reset, order shipped, etc.)
- [ ] Email verification on registration
- [ ] Admin audit logs
- [ ] Advanced product filtering and search in admin
- [ ] Bulk operations (bulk import, bulk delete)
- [ ] Reports and analytics dashboard

---

## Support
For issues or questions regarding these features:
1. Check the application logs
2. Review the Bike Store documentation
3. Examine the Spring Boot and Spring Security documentation

---

**Last Updated**: June 21, 2026
**Version**: 1.0.0

