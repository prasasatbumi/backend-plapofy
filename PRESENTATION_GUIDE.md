# Panduan Presentasi Proyek: Sistem Manajemen Pinjaman (Plapofy)

Dokumen ini berisi materi untuk dua jenis presentasi: **Presentasi Bisnis** (untuk pemangku kepentingan/manajemen) dan **Presentasi Teknis** (untuk tim developer/CTO).

---

## BAGIAN 1: PRESENTASI BISNIS (Business Presentation)
**Target Audience:** Product Owner, Manager, Investor, User Bisnis.
**Fokus:** Solusi, Alur Proses, Keamanan, dan Nilai Bisnis.

### Slide 1: Judul & Visi
*   **Judul:** Transformasi Digital Layanan Pinjaman (Loan Origination System)
*   **Visi:** Mempercepat proses pengajuan hingga pencairan dana dengan sistem yang aman, transparan, dan terintegrasi AI.

### Slide 2: Masalah & Solusi
*   **Tantangan Saat Ini:** Proses manual, persetujuan lambat, risiko human error dalam penentuan limit (plafond), dan layanan pelanggan yang terbatas jam kerja.
*   **Solusi Kami:**
    *   **Otomatisasi Alur:** Digitalisasi end-to-end dari registrasi nasabah hingga pencairan.
    *   **Smart Scoring:** Penentuan plafond otomatis berdasarkan gaji dan skor kredit.
    *   **24/7 AI Support:** Integrasi OpenAI untuk menjawab pertanyaan nasabah kapan saja.

### Slide 3: Fitur Utama (Key Features)
1.  **Multi-Role Workflow (Separation of Duties):**
    *   *Nasabah:* Mengajukan pinjaman mandiri via aplikasi.
    *   *Marketing:* Validasi awal data nasabah.
    *   *Branch Manager:* Pemberi persetujuan akhir (Approval).
    *   *Back Office:* Eksekusi pencairan dana (Disbursement).
    *   *Super Admin:* Manajemen sistem penuh.
2.  **Manajemen Plafond Dinamis:** Sistem otomatis merekomendasikan limit pinjaman berdasarkan profil risiko.
3.  **AI Assistant:** Chatbot cerdas untuk membantu nasabah memahami produk pinjaman.

### Slide 4: Keamanan & Kepatuhan
*   **Data Protection:** Password terenkripsi, akses data dibatasi sesuai jabatan (Role-Based Access Control).
*   **Audit Trail:** Setiap perubahan status pinjaman tercatat (siapa yang menyetujui, kapan dicairkan).
*   **Secure Access:** Menggunakan standar keamanan token (JWT) untuk mencegah akses tidak sah.

### Slide 5: Alur Bisnis (Business Flow Demo)
1.  **Registrasi:** Nasabah mendaftar -> Sistem memvalidasi.
2.  **Pengajuan:** Nasabah input data gaji -> Sistem menghitung Plafond.
3.  **Review:** Marketing memverifikasi dokumen.
4.  **Approval:** Manager menyetujui berdasarkan analisis risiko.
5.  **Pencairan:** Back Office mentransfer dana -> Status "Disbursed".

---

## BAGIAN 2: PRESENTASI TEKNIS (Technical Presentation)
**Target Audience:** CTO, Tech Lead, Developer, Auditor IT.
**Fokus:** Arsitektur, Stack, Security Implementation, Scalability.

### Slide 1: Technology Stack
*   **Backend:** Java 17, Spring Boot 3.4.1 (Framework standar enterprise).
*   **Database:** SQL Server (Relational DB yang kuat untuk data finansial).
*   **ORM:** Hibernate/JPA (Manajemen data object-relational).
*   **Caching:** Redis (Untuk performa tinggi - *via Docker*).
*   **Security:** Spring Security 6, JWT (JSON Web Token), BCrypt.
*   **AI Integration:** OpenAI API (GPT Model).
*   **API Documentation:** OpenAPI 3.0 / Swagger UI.

### Slide 2: System Architecture (Layered Architecture)
*   **Controller Layer:** Menangani HTTP Request/Response (REST API).
*   **Service Layer:** Berisi logika bisnis (Loan calculation, State transitions).
*   **Repository Layer:** Abstraksi akses data ke SQL Server.
*   **Security Layer:** Filter chain untuk validasi Token JWT sebelum request masuk ke Controller.

### Slide 3: Database Schema (ERD Highlight)
*   **Users & RBAC:** Tabel `users`, `roles`, `permissions`, `menus` (Many-to-Many relationships) untuk manajemen akses yang granular.
*   **Core Business:**
    *   `loans`: Menyimpan state pinjaman (`SUBMITTED`, `APPROVED`, `DISBURSED`).
    *   `plafonds`: Master data limit kredit.
    *   `customers`: Data detail nasabah (terhubung ke Users).

### Slide 4: Security Implementation (Deep Dive)
*   **Authentication:** Stateless JWT. Token digenerate saat login, berisi `userId` dan `roles`.
*   **Authorization:** Menggunakan `@PreAuthorize("hasRole('BRANCH_MANAGER')")` di level method. Memastikan hanya role yang tepat yang bisa akses endpoint spesifik.
*   **Data Security:**
    *   Password di-hash menggunakan **BCrypt**.
    *   Validasi input menggunakan **Bean Validation** (mencegah SQL Injection & Bad Data).
    *   CORS & CSRF configured for API stateless usage.

### Slide 5: Key Algorithms & Logic
*   **Loan State Machine:** Logika transisi status pinjaman yang ketat. Tidak bisa lompat dari `SUBMITTED` langsung ke `DISBURSED` tanpa `APPROVED`.
*   **OpenAI Integration:** Controller khusus yang meneruskan prompt ke OpenAI API dan mengembalikan respons terstruktur, diamankan hanya untuk role tertentu (saat ini Super Admin/Internal).

### Slide 6: Dev Tools & Deployment
*   **Build Tool:** Maven.
*   **Environment:** Konfigurasi terpisah via `application.yml` dan Environment Variables (aman untuk Credential).
*   **Containerization:** Docker Compose support untuk Redis dan tools pendukung.

---

## SCRIPT DEMO SINGKAT (Untuk Presenter)

**Skenario:** Siklus Hidup Pinjaman.

1.  **Buka Postman/Swagger.**
2.  **Login Super Admin:** Tunjukkan endpoint `/auth/login`, dapatkan `access_token`.
3.  **Cek Swagger:** Buka `http://localhost:8081/swagger-ui/index.html` untuk menunjukkan dokumentasi API yang auto-generated.
4.  **Simulasi Pinjaman:**
    *   Login sebagai **Nasabah**, hit `POST /api/loans` (Create Loan).
    *   Login sebagai **Manager**, hit `PUT /api/loans/{id}/approve` (Approve).
    *   Tunjukkan perubahan status di database atau response API.
5.  **Fitur AI:** Kirim chat ke endpoint AI tanya "Bagaimana cara menghitung bunga?", tunjukkan respons cerdas dari sistem.
