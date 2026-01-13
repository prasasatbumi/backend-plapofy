# Gap Analysis: FinProv Loan Management System

## Summary

| Category | Implemented | Missing/Partial | Total |
|----------|-------------|-----------------|-------|
| Role-Based Features | 14 | 4 | 18 |
| Backend Technical | 5 | 2 | 7 |
| Frontend Web | 5 | 2 | 7 |
| Mobile Android | 0 | 7 | 7 |

---

## 1. Role-Based Requirements

### ✅ Nasabah (Customer)

| Requirement | Status | Notes |
|-------------|--------|-------|
| Melihat daftar plafon tanpa login | ✅ Done | `GET /api/plafonds` is public |
| Registrasi dengan email & password | ✅ Done | `POST /api/auth/register` |
| Login untuk akses fitur personal | ✅ Done | JWT authentication |
| Input data pengajuan pinjaman | ✅ Done | `POST /api/loans/submit-kyc` |
| Simulasi pinjaman (bunga, biaya) | ⚠️ Partial | `ProductInterest` exists, but no simulation endpoint |
| Melengkapi profil (alamat, HP) | ⚠️ Partial | `Customer` entity exists, but no profile update UI |
| Memantau status pengajuan | ✅ Done | `GET /api/loans` returns status |
| Menerima notifikasi | ⚠️ Partial | `Notification` entity exists, but no push/email |

### ✅ Marketing

| Requirement | Status | Notes |
|-------------|--------|-------|
| Login dengan kredensial Superadmin | ✅ Done | User management by Superadmin |
| Melihat daftar pinjaman untuk review | ✅ Done | Dashboard filtering |
| Review Pinjaman | ✅ Done | `PATCH /api/loans/{id}/review` |

### ✅ Branch Manager

| Requirement | Status | Notes |
|-------------|--------|-------|
| Login | ✅ Done | JWT auth |
| Melihat pinjaman waiting approval | ✅ Done | Dashboard |
| Approval Pinjaman | ✅ Done | `PATCH /api/loans/{id}/approve` |
| Reject Pinjaman | ✅ Done | `PATCH /api/loans/{id}/reject` |

### ✅ Back Office

| Requirement | Status | Notes |
|-------------|--------|-------|
| Login | ✅ Done | JWT auth |
| Melihat pinjaman Menunggu Pencairan | ✅ Done | Dashboard |
| Pencairan Pinjaman | ✅ Done | `PATCH /api/loans/{id}/disburse` |
| Reject Pinjaman | ✅ Done | `PATCH /api/loans/{id}/reject` |

### ⚠️ Superadmin

| Requirement | Status | Notes |
|-------------|--------|-------|
| Akses penuh ke seluruh menu | ✅ Done | Role-based access |
| Manajemen User | ✅ Done | `UserController`, Frontend UI |
| Manajemen Role | ✅ Done | `RoleController` exists |
| Manajemen Menu | ⚠️ Backend Only | `MenuController` exists, **No frontend UI** |
| Manajemen Hak Akses (RBAC) | ⚠️ Backend Only | `PermissionController`, `RoleMappingController` exist, **No frontend UI** |

---

## 2. Technical Requirements

### A. Backend (REST API)

| Requirement | Status | Notes |
|-------------|--------|-------|
| Autentikasi JWT + Spring Security | ✅ Done | `SecurityConfig.java`, `JwtAuthenticationFilter` |
| RBAC Dinamis | ✅ Done | `RolePermission`, `RoleMenu` entities |
| Lupa Password via Email | ❌ Missing | Endpoint exists (`/reset-password`) but **no Spring Mail / email sending** |
| Notifikasi (DB/FCM) | ⚠️ Partial | `Notification` entity & repo exist, **no NotificationController or FCM** |
| Dokumentasi API (Swagger) | ✅ Done | Available at `/swagger-ui.html` |

#### Missing Backend Items:
1. **Spring Mail Configuration** - Need `spring-boot-starter-mail` dependency and `MailService`
2. **NotificationController** - Endpoint to list/read notifications for Nasabah
3. **Firebase Cloud Messaging (FCM)** - For push notifications to Android

---

### B. Frontend Web (Dashboard & Landing Page)

| Requirement | Status | Notes |
|-------------|--------|-------|
| Landing Page | ✅ Done | `landing.component.ts` |
| Dashboard | ✅ Done | `dashboard.component.ts`, `analytical-dashboard.component.ts` |
| Manajemen User | ✅ Done | `user-management.component.ts` |
| Manajemen Role | ❌ Missing | **No `role-management.component.ts`** |
| Manajemen Menu | ❌ Missing | **No `menu-management.component.ts`** |
| Manajemen Permission/Access | ❌ Missing | **No `permission-management.component.ts`** |
| Responsif (Desktop/Mobile) | ✅ Done | TailwindCSS responsive classes |

#### Missing Frontend Items:
1. **Role Management UI** - CRUD for roles
2. **Menu Management UI** - CRUD for menus and role-menu mapping
3. **Permission Management UI** - CRUD for permissions and role-permission mapping
4. **Loan Simulation Page** - Calculate installments before submission
5. **Notification Center** - Display notifications to logged-in users

---

### C. Mobile (Android) ❌ NOT IMPLEMENTED

| Requirement | Status | Notes |
|-------------|--------|-------|
| Offline First (Room Database) | ❌ Missing | `Plapofy/` folder is empty |
| Arsitektur MVVM | ❌ Missing | No Android code |
| Integrasi API (Retrofit) | ❌ Missing | No Android code |
| Profil User | ❌ Missing | No Android code |
| Melihat daftar plafon | ❌ Missing | No Android code |
| Pengajuan pinjaman | ❌ Missing | No Android code |
| Notifikasi (FCM) | ❌ Missing | No Android code |

> **Critical:** The entire Android application is missing. The `Plapofy/` folder exists but contains no code.

---

## Priority Action Items

### 🔴 Critical (Must Have for MVP)

1. **Android App** - Create complete Android application with:
   - MVVM architecture
   - Retrofit for API integration
   - Room for offline support
   - FCM for push notifications

2. **Forgot Password with Email** - Implement:
   ```xml
   <!-- pom.xml -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-mail</artifactId>
   </dependency>
   ```
   - Create `EmailService` using `JavaMailSender`
   - Generate reset token and send via email

### 🟠 High Priority

3. **RBAC Management UI** (Frontend)
   - `role-management.component.ts`
   - `menu-management.component.ts`
   - `permission-management.component.ts`

4. **NotificationController** (Backend)
   - `GET /api/notifications` - List user notifications
   - `PATCH /api/notifications/{id}/read` - Mark as read

### 🟡 Medium Priority

5. **Loan Simulation Endpoint**
   - `POST /api/loans/simulate` - Calculate installment preview

6. **Customer Profile Update**
   - `PUT /api/customers/profile` - Update address & phone

### 🟢 Nice to Have

7. **FCM Integration** - Push notifications to Android
8. **Email Notifications** - On loan status changes

---

## Current Project Structure

```
finprov2/
├── src/main/java/com/finprov/loan/
│   ├── controller/      # 11 controllers ✅
│   ├── service/         # 10 services ✅
│   ├── entity/          # 15 entities ✅
│   ├── repository/      # 13 repositories ✅
│   ├── security/        # JWT, SecurityConfig ✅
│   └── config/          # DataInitializer, etc ✅
├── frontend/src/app/
│   ├── features/
│   │   ├── auth/        ✅
│   │   ├── dashboard/   ✅
│   │   ├── landing/     ✅
│   │   ├── loans/       ✅
│   │   └── users/       ✅ (missing: roles, menus, permissions)
│   └── core/            ✅
└── Plapofy/             ❌ EMPTY (Android app missing)
```
