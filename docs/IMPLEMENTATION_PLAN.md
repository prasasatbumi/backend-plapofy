# FinProv Improvement Plan

> **Focus:** Frontend Web UI + Backend  
> **Excluded:** Android UI/Frontend (backend support included)

---

## Phase 1: Critical Database & Security Fixes

### 1.1 Fix Password Exposure in JSON
#### [MODIFY] [User.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/User.java)
```diff
+ import com.fasterxml.jackson.annotation.JsonIgnore;

  private String email;
+ @JsonIgnore
  private String password;
```

### 1.2 Connect Customer ↔ User
#### [MODIFY] [User.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/User.java)
```diff
+ @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
+ @JsonIgnoreProperties("user")
+ private Customer customer;
```

#### [MODIFY] [Customer.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Customer.java)
```diff
+ @OneToOne
+ @JoinColumn(name = "user_id", unique = true)
+ @JsonIgnore
+ private User user;

+ private String address;  // Add missing field
```

### 1.3 Fix Notification Reserved Words
#### [MODIFY] [Notification.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Notification.java)
```diff
- @Column(name = "\"type\"", nullable = false)
+ @Column(name = "notification_type", nullable = false)

- @Column(name = "\"read\"", nullable = false)
+ @Column(name = "is_read", nullable = false)
```

---

## Phase 2: Backend - Missing Endpoints

### 2.1 Notification Controller
#### [NEW] NotificationController.java
```
- GET /api/notifications - List user notifications
- PATCH /api/notifications/{id}/read - Mark as read
- GET /api/notifications/unread/count - Get unread count
```

### 2.2 Loan Simulation Endpoint
#### [MODIFY] [LoanController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/LoanController.java)
```
+ POST /api/loans/simulate
  Input: { plafondId, amount, tenorMonth }
  Output: { monthlyInstallment, interestRate, totalPayment, fees }
```

### 2.3 Customer Profile Endpoint
#### [MODIFY] CustomerController.java
```
+ GET /api/customers/profile - Get current user's profile
+ PUT /api/customers/profile - Update profile (address, phone)
```

### 2.4 Forgot Password with Email
#### [NEW] EmailService.java
```
- Add spring-boot-starter-mail dependency
- Create EmailService with JavaMailSender
- Generate reset token, save to DB, send via email
```

---

## Phase 3: Frontend - RBAC Management UI

### 3.1 Role Management
#### [NEW] role-management.component.ts
```
Path: frontend/src/app/features/roles/
Features:
  - List all roles
  - Create new role
  - Edit role name
  - Delete role (soft delete)
  - Assign permissions to role
```

### 3.2 Menu Management
#### [NEW] menu-management.component.ts
```
Path: frontend/src/app/features/menus/
Features:
  - List all menus
  - Create/Edit/Delete menu
  - Assign menus to roles
```

### 3.3 Permission Management
#### [NEW] permission-management.component.ts
```
Path: frontend/src/app/features/permissions/
Features:
  - List all permissions
  - Create/Edit/Delete permission
  - View role-permission mappings
```

---

## Phase 4: Frontend - User Experience

### 4.1 Notification Center Component
#### [NEW] notification-center.component.ts
```
Path: frontend/src/app/core/components/
Features:
  - Bell icon in header with unread count
  - Dropdown with notification list
  - Mark as read on click
  - Link to related loan
```

### 4.2 Loan Simulation Page
#### [NEW] loan-simulation.component.ts
```
Path: frontend/src/app/features/loans/
Features:
  - Select plafond
  - Input amount & tenor
  - Show preview: monthly payment, interest, total
  - "Proceed to Application" button
```

### 4.3 Customer Profile Page
#### [NEW] profile.component.ts
```
Path: frontend/src/app/features/profile/
Features:
  - View current profile
  - Edit address & phone
  - Validation before loan submission
```

### 4.4 Forgot Password UI
#### [NEW] forgot-password.component.ts
```
Path: frontend/src/app/features/auth/forgot-password/
Features:
  - Email input form
  - Submit request
  - Success message
  - Reset password form (token from email)
```

---

## Phase 5: Backend - Android Support (No UI)

### 5.1 FCM Integration
#### [NEW] FcmService.java
```
- Add Firebase Admin SDK dependency
- Store device tokens per user
- Send push notifications on loan status change
```

### 5.2 Device Token Endpoint
#### [NEW] DeviceController.java
```
+ POST /api/devices/register - Register FCM token
+ DELETE /api/devices/{token} - Unregister token
```

---

## Implementation Order

| Order | Item | Type | Effort |
|-------|------|------|--------|
| 1 | Password @JsonIgnore | Backend | 5 min |
| 2 | Customer ↔ User relation | Backend | 30 min |
| 3 | Notification column fix | Backend | 10 min |
| 4 | NotificationController | Backend | 1 hr |
| 5 | Loan Simulation endpoint | Backend | 45 min |
| 6 | Role Management UI | Frontend | 2 hr |
| 7 | Menu Management UI | Frontend | 2 hr |
| 8 | Permission Management UI | Frontend | 2 hr |
| 9 | Notification Center UI | Frontend | 1.5 hr |
| 10 | Loan Simulation UI | Frontend | 1.5 hr |
| 11 | Profile Page | Frontend | 1 hr |
| 12 | Forgot Password (Email) | Backend | 2 hr |
| 13 | Forgot Password UI | Frontend | 1 hr |
| 14 | FCM Integration | Backend | 2 hr |

**Total Estimated: ~16-18 hours**

---

## Quick Start

Mau mulai dari mana?
1. **Phase 1** - Critical fixes (Password, Customer-User link)
2. **Phase 2** - Backend endpoints (Notification, Simulation)
3. **Phase 3** - RBAC UI (Role, Menu, Permission)
4. **Full implementation** - Semua sekaligus
