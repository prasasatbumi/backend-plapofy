# Plapofy API Documentation

## Ringkasan
- Aplikasi menggunakan autentikasi JWT yang bersifat stateless dan RBAC (Role-Based Access Control).
- Dokumentasi OpenAPI tersedia via Swagger UI.
- Soft delete diaktifkan pada entitas utama agar data terhapus tidak muncul di query reguler namun tetap tercatat.

Referensi kode:
- Keamanan: [SecurityConfig.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/security/SecurityConfig.java#L32-L51)
- JWT Filter: [JwtAuthenticationFilter.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/security/jwt/JwtAuthenticationFilter.java#L32-L55)
- OpenAPI: [OpenApiConfig.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/config/OpenApiConfig.java#L1-L25)

## Akses Dokumentasi
- Swagger UI: http://localhost:8081/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs
- Postman Collection: [Plapofy.postman_collection.json](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/postman/Plapofy.postman_collection.json)

## Autentikasi
- Skema: Bearer Token (JWT). Header: `Authorization: Bearer <jwt>`
- Login menghasilkan token yang memuat username, userId, role dan roles.

Endpoints (AuthController):
- POST `/api/auth/login`
  - Body: `{ "username": "string", "password": "string" }`
  - Response: `{ success, data: { token, userId, role, roles } }`
  - [AuthController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/AuthController.java#L24-L34)
- POST `/api/auth/register`
  - Body: `{ "username": "string", "email": "string", "password": "string" }`
  - Response: `{ success, data: { userId, username, email } }`
  - [AuthController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/AuthController.java#L34-L47)
- POST `/api/auth/reset-password`
  - Body: `{ "email": "string", "newPassword": "string" }`
  - Response: `{ success, message: "Password reset" }`
  - [AuthController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/AuthController.java#L47-L53)
- POST `/api/auth/logout` (auth required)
  - Header: `Authorization: Bearer <jwt>`
  - Body: tidak diperlukan
  - Response: `{ success, message: "Logged out" }`
  - [AuthController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/AuthController.java#L53-L62)

## Public
- GET `/api/public/landing`
  - Response: info publik aplikasi
  - [PublicController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/PublicController.java#L12-L28)

## Plafond
- GET `/api/plafonds`
  - Response: daftar plafon pinjaman (public)
  - [PlafondController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/PlafondController.java#L14-L32)

## User Management (SUPER_ADMIN)
- GET `/api/users` — list users
- POST `/api/users` — create user
- DELETE `/api/users/{id}` — soft delete user
  - [UserController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/UserController.java#L18-L48)
  - Model: [User.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/User.java#L9-L41)

## Customer Management (SUPER_ADMIN)
- GET `/api/customers` — list customers
- POST `/api/customers` — create customer
- DELETE `/api/customers/{id}` — soft delete customer
  - [CustomerController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/CustomerController.java#L18-L49)
  - Model: [Customer.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Customer.java#L9-L38)

## Role Management (SUPER_ADMIN)
- GET `/api/roles` — list roles
- POST `/api/roles` — create role
- PUT `/api/roles/{id}` — update role
- DELETE `/api/roles/{id}` — soft delete role
- PUT `/api/roles/{id}/restore` — restore role
  - [RoleController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/RoleController.java#L18-L68)
  - Model: [Role.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Role.java#L9-L35)
  - Service: [RoleServiceImpl.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/service/impl/RoleServiceImpl.java#L36-L75)

## Permission Management (SUPER_ADMIN)
- GET `/api/permissions` — list permissions
- POST `/api/permissions` — create permission
  - [PermissionController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/PermissionController.java#L15-L59)
  - Model: [Permission.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Permission.java#L1-L22)

## Role Mapping (SUPER_ADMIN)
- POST `/api/role-mappings/roles/{roleId}/permissions/{permissionId}` — tambah permission ke role
- DELETE `/api/role-mappings/role-permissions/{id}` — hapus mapping permission
- POST `/api/role-mappings/roles/{roleId}/menus/{menuId}` — tambah menu ke role
- DELETE `/api/role-mappings/role-menus/{id}` — hapus mapping menu
  - [RoleMappingController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/RoleMappingController.java#L22-L65)
  - Join entities: [RolePermission.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/RolePermission.java#L1-L26), [RoleMenu.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/RoleMenu.java#L1-L26)

## Menu Management (SUPER_ADMIN)
- GET `/api/menus` — list menus
- POST `/api/menus` — create menu
- PUT `/api/menus/{id}` — update menu
- DELETE `/api/menus/{id}` — delete menu
  - [MenuController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/MenuController.java#L18-L58)
  - Model: [Menu.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Menu.java#L1-L25)

## Loan Management
- POST `/api/loans` — submit loan (NASABAH)
- PATCH `/api/loans/{id}/review` — review loan (MARKETING)
- PATCH `/api/loans/{id}/approve` — approve loan (BRANCH_MANAGER)
- PATCH `/api/loans/{id}/disburse` — disburse loan (BACK_OFFICE)
- GET `/api/loans` — list loans (NASABAH hanya melihat miliknya; role lain semua)
- GET `/api/loans/{id}` — detail loan
  - [LoanController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/LoanController.java#L22-L81)
  - Service: [LoanServiceImpl.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/service/impl/LoanServiceImpl.java#L139-L210)
  - Model: [Loan.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Loan.java#L1-L36)
  - Audit: [LoanApproval.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/LoanApproval.java#L1-L37), [LoanDisbursement.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/LoanDisbursement.java#L1-L34)

## OpenAI (SUPER_ADMIN)
- POST `/api/openai/chat` — kirim prompt dan terima respon dari AI
  - [OpenAIController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/OpenAIController.java#L18-L36)

## Soft Delete Policy
- User, Role, Customer menggunakan `@SQLDelete` untuk update kolom `deleted=1` pada operasi delete.
- `@Where(clause = "deleted = 0 OR deleted IS NULL")` menyaring data terhapus dari query reguler.
- Role mendukung restore melalui service.
  - [User.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/User.java#L9-L21)
  - [Role.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Role.java#L9-L35)
  - [Customer.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Customer.java#L9-L38)

## Skema & Relasi Database (Inti)
- User
  - Field utama: id, username, email, password, isActive, deleted
  - Relasi: ManyToMany ke Role melalui `user_roles`
  - [User.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/User.java#L21-L41)
- Role
  - Field utama: id, name (role_name), deleted
  - Relasi: ManyToMany ke User; ManyToMany implisit ke Permission dan Menu via join entities
  - [Role.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Role.java#L18-L35)
- Permission
  - Field: id, code (unik), name
  - [Permission.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Permission.java#L1-L22)
- Menu
  - Field: id, code (unik), name, path
  - [Menu.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Menu.java#L1-L25)
- RolePermission (join)
  - Unique: (role_id, permission_id)
  - [RolePermission.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/RolePermission.java#L1-L26)
- RoleMenu (join)
  - Unique: (role_id, menu_id)
  - [RoleMenu.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/RoleMenu.java#L1-L26)
- Customer
  - Field: id, firstName, lastName, nik (unik), phoneNumber, deleted
  - [Customer.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Customer.java#L21-L38)
- Plafond
  - Field: id, code (unik), name, maxAmount
  - [Plafond.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Plafond.java#L1-L26)
- Loan
  - Field: id, applicant (User), plafond (Plafond), amount, currentStatus, createdAt
  - [Loan.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/Loan.java#L1-L36)
- Audit Loan
  - LoanApproval: status setelah aksi, actor (User), role, remarks, timestamp
  - LoanDisbursement: amount, actor, timestamp
  - [LoanApproval.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/LoanApproval.java#L1-L37)
  - [LoanDisbursement.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/entity/LoanDisbursement.java#L1-L34)

## Alur Bisnis Kredit (End-to-End)
- Onboarding Operasional
  - SUPER_ADMIN membuat role, permission, menu; mapping di role-mappings untuk kontrol akses UI/aksi.
  - Membuat user operasional: MARKETING, BRANCH_MANAGER, BACK_OFFICE.
- Registrasi Nasabah
  - Calon nasabah register (role NASABAH). Login untuk mendapatkan JWT.
- Pengajuan Pinjaman (NASABAH)
  - Submit loan dengan plafon dan jumlah. Sistem menyimpan applicant = user saat ini dan status awal (mis. submitted).
- Review (MARKETING)
  - Memeriksa dokumen, menambahkan catatan, mengubah status ke reviewed; tercatat di LoanApproval.
- Approval (BRANCH_MANAGER)
  - Menyetujui/menolak; status menjadi approved/rejected; tercatat di LoanApproval.
- Pencairan (BACK_OFFICE)
  - Melakukan disbursement; menyimpan nominal dan waktu di LoanDisbursement; status menjadi disbursed.
- Monitoring
  - Notifikasi dan audit dapat diperluas; listing loans role-aware: NASABAH melihat miliknya, role lain melihat semua sesuai kebijakan.

## Contoh Payload
- Login: `{ "username": "superadmin", "password": "admin123" }`
- Create User: `{ "username": "newuser", "password": "password123", "email": "newuser@example.com", "roles": [{ "name": "USER" }] }`
- Submit Loan: `{ "plafondId": 1, "amount": 3000000 }`

## Catatan Implementasi
- Semua endpoint selain public, login, register, reset-password memerlukan JWT.
- Logout membutuhkan JWT namun tidak memerlukan body.
- Soft delete memastikan integritas relasi dan audit tetap aman.

## Arsitektur Proyek & Flow Folder
- Entry Point
  - Menjalankan aplikasi Spring Boot: [LoanManagementApplication.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/LoanManagementApplication.java)
- Controller
  - Menangkap HTTP request dan memanggil Service, menghasilkan ApiResponse.
  - Contoh: [AuthController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finprov/loan/controller/AuthController.java), [LoanController.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finprov2%20-%20Copy/src/main/java/com/finrov/loan/controller/LoanController.java)
- Service
  - Mengimplementasikan logika bisnis dan koordinasi antar repository.
  - Contoh: [LoanServiceImpl.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/service/impl/LoanServiceImpl.java)
- Repository
  - Akses data ke database menggunakan JPA.
  - Contoh: [LoanRepository.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/repository/LoanRepository.java)
- Entity
  - Model domain yang dipetakan ke tabel database.
  - Contoh: [User.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/entity/User.java), [Loan.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/entity/Loan.java)
- DTO
  - Struktur data untuk request dan response.
  - Contoh: [LoginRequest.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/dto/LoginRequest.java), [LoginResponse.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/dto/LoginResponse.java), [ApiResponse.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/dto/ApiResponse.java)
- Security
  - Konfigurasi HTTP security, filter JWT, dan pemuatan user dari database.
  - [SecurityConfig.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/security/SecurityConfig.java), [JwtAuthenticationFilter.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/security/jwt/JwtAuthenticationFilter.java), [CustomUserDetailsService.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/security/CustomUserDetailsService.java)
- Config
  - Konfigurasi umum aplikasi seperti OpenAPI, Redis, inisialisasi data, RestTemplate.
  - [OpenApiConfig.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/config/OpenApiConfig.java), [RedisConfig.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/config/RedisConfig.java), [DataInitializer.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/config/DataInitializer.java), [AppConfig.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/config/AppConfig.java)
- Exception
  - Penanganan error global untuk respons konsisten.
  - [GlobalExceptionHandler.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/exception/GlobalExceptionHandler.java)

## Request Lifecycle
- Autentikasi
  - Filter JWT memverifikasi token, memuat user dan authorities, menyetel SecurityContext. [JwtAuthenticationFilter.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/security/jwt/JwtAuthenticationFilter.java#L32-L55)
  - Aturan akses didefinisikan di SecurityConfig (permitAll untuk login/register/public; lainnya authenticated/hasRole). [SecurityConfig.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/security/SecurityConfig.java#L32-L51)
- Orkestrasi
  - Controller menerima request dan memanggil Service.
  - Service menjalankan logika, akses data via Repository, lalu mengembalikan hasil ke Controller.
  - Controller membungkus hasil ke ApiResponse DTO.
- Validasi & Error
  - Kesalahan ditangkap oleh GlobalExceptionHandler untuk menghasilkan struktur error seragam. [GlobalExceptionHandler.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/exception/GlobalExceptionHandler.java)

## Caching & Redis
- Cache konfigurasi default dengan JSON serializer. [RedisConfig.java](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/config/RedisConfig.java)
- Penggunaan nyata contoh:
  - Cache daftar roles: [RoleServiceImpl.getAllRoles](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/service/impl/RoleServiceImpl.java#L36-L44)
  - Evict cache saat create/update/delete/restore role: [RoleServiceImpl](file:///c:/Users/prasa/Desktop/BootcampITDP12/finrov2%20-%20Copy/src/main/java/com/finrov/loan/service/impl/RoleServiceImpl.java#L46-L75)
- Tujuan: mengurangi beban database untuk data yang sering diakses dan berubah relatif jarang.

## Data Layer & Soft Delete
- Entitas yang disoft-delete (User, Role, Customer) disaring otomatis oleh Hibernate melalui `@Where`.
- Operasi delete memicu `@SQLDelete` yang mengubah kolom `deleted` tanpa menghilangkan record.
- Impact:
  - Query reguler tidak menampilkan data terhapus.
  - Restore dimungkinkan (contoh Role melalui service).
  - Audit/riwayat tetap terjaga.

