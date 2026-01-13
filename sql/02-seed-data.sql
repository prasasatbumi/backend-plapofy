USE Plapofy;
GO

IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'SUPER_ADMIN') INSERT INTO roles(role_name, deleted) VALUES ('SUPER_ADMIN', 0);
IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'MARKETING') INSERT INTO roles(role_name, deleted) VALUES ('MARKETING', 0);
IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'BRANCH_MANAGER') INSERT INTO roles(role_name, deleted) VALUES ('BRANCH_MANAGER', 0);
IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'BACK_OFFICE') INSERT INTO roles(role_name, deleted) VALUES ('BACK_OFFICE', 0);
IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'NASABAH') INSERT INTO roles(role_name, deleted) VALUES ('NASABAH', 0);
GO

IF NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'LOAN_SUBMIT') INSERT INTO permissions(code, name) VALUES ('LOAN_SUBMIT', 'Submit Loan');
IF NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'LOAN_REVIEW') INSERT INTO permissions(code, name) VALUES ('LOAN_REVIEW', 'Review Loan');
IF NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'LOAN_APPROVE') INSERT INTO permissions(code, name) VALUES ('LOAN_APPROVE', 'Approve Loan');
IF NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'LOAN_DISBURSE') INSERT INTO permissions(code, name) VALUES ('LOAN_DISBURSE', 'Disburse Loan');
IF NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'USER_MANAGE') INSERT INTO permissions(code, name) VALUES ('USER_MANAGE', 'Manage Users');
IF NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'ROLE_MANAGE') INSERT INTO permissions(code, name) VALUES ('ROLE_MANAGE', 'Manage Roles');
IF NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'MENU_MANAGE') INSERT INTO permissions(code, name) VALUES ('MENU_MANAGE', 'Manage Menus');
IF NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'PERMISSION_MANAGE') INSERT INTO permissions(code, name) VALUES ('PERMISSION_MANAGE', 'Manage Permissions');
IF NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'LOAN_VIEW') INSERT INTO permissions(code, name) VALUES ('LOAN_VIEW', 'View Loans');
IF NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'DASHBOARD_VIEW') INSERT INTO permissions(code, name) VALUES ('DASHBOARD_VIEW', 'View Dashboard');
GO

IF NOT EXISTS (SELECT 1 FROM menus WHERE code = 'MENU_LOANS') INSERT INTO menus(code, name, path) VALUES ('MENU_LOANS', 'Loans', '/api/loans');
IF NOT EXISTS (SELECT 1 FROM menus WHERE code = 'MENU_PLAFONDS') INSERT INTO menus(code, name, path) VALUES ('MENU_PLAFONDS', 'Plafonds', '/api/plafonds');
IF NOT EXISTS (SELECT 1 FROM menus WHERE code = 'MENU_USERS') INSERT INTO menus(code, name, path) VALUES ('MENU_USERS', 'Users', '/api/users');
IF NOT EXISTS (SELECT 1 FROM menus WHERE code = 'MENU_ROLES') INSERT INTO menus(code, name, path) VALUES ('MENU_ROLES', 'Roles', '/api/roles');
IF NOT EXISTS (SELECT 1 FROM menus WHERE code = 'MENU_PERMS') INSERT INTO menus(code, name, path) VALUES ('MENU_PERMS', 'Permissions', '/api/permissions');
GO

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'superadmin') INSERT INTO users(username, email, password, is_active) VALUES ('superadmin', 'superadmin@finprov.com', 'admin123', 1);
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'marketing1') INSERT INTO users(username, email, password, is_active) VALUES ('marketing1', 'marketing1@finprov.com', 'secret123', 1);
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'marketing2') INSERT INTO users(username, email, password, is_active) VALUES ('marketing2', 'marketing2@finprov.com', 'secret123', 1);
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'marketing3') INSERT INTO users(username, email, password, is_active) VALUES ('marketing3', 'marketing3@finprov.com', 'secret123', 1);
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'branchmanager1') INSERT INTO users(username, email, password, is_active) VALUES ('branchmanager1', 'bm1@finprov.com', 'secret123', 1);
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'branchmanager2') INSERT INTO users(username, email, password, is_active) VALUES ('branchmanager2', 'bm2@finprov.com', 'secret123', 1);
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'branchmanager3') INSERT INTO users(username, email, password, is_active) VALUES ('branchmanager3', 'bm3@finprov.com', 'secret123', 1);
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'backoffice1') INSERT INTO users(username, email, password, is_active) VALUES ('backoffice1', 'bo1@finprov.com', 'secret123', 1);
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'backoffice2') INSERT INTO users(username, email, password, is_active) VALUES ('backoffice2', 'bo2@finprov.com', 'secret123', 1);
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'backoffice3') INSERT INTO users(username, email, password, is_active) VALUES ('backoffice3', 'bo3@finprov.com', 'secret123', 1);
GO

DECLARE @i INT = 1;
WHILE @i <= 20
BEGIN
    DECLARE @u NVARCHAR(50) = CONCAT('nasabah', RIGHT('00' + CAST(@i AS VARCHAR(2)), 2));
    IF NOT EXISTS (SELECT 1 FROM users WHERE username = @u)
        INSERT INTO users(username, email, password, is_active) VALUES (@u, CONCAT(@u, '@mail.com'), 'secret123', 1);
    SET @i = @i + 1;
END
GO

INSERT INTO user_roles(user_id, role_id)
SELECT u.id, r.id
FROM (VALUES
    ('superadmin','SUPER_ADMIN'),
    ('marketing1','MARKETING'),
    ('marketing2','MARKETING'),
    ('marketing3','MARKETING'),
    ('branchmanager1','BRANCH_MANAGER'),
    ('branchmanager2','BRANCH_MANAGER'),
    ('branchmanager3','BRANCH_MANAGER'),
    ('backoffice1','BACK_OFFICE'),
    ('backoffice2','BACK_OFFICE'),
    ('backoffice3','BACK_OFFICE')
) x(username, role_name)
JOIN users u ON u.username = x.username
JOIN roles r ON r.role_name = x.role_name
WHERE NOT EXISTS (
    SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id
);
GO

INSERT INTO user_roles(user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.role_name = 'NASABAH'
WHERE u.username LIKE 'nasabah%' AND NOT EXISTS (
    SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id
);
GO

IF NOT EXISTS (SELECT 1 FROM plafonds WHERE code = 'PLF_SML') INSERT INTO plafonds(code, name, max_amount) VALUES ('PLF_SML', 'Small', 5000000.00);
IF NOT EXISTS (SELECT 1 FROM plafonds WHERE code = 'PLF_MED') INSERT INTO plafonds(code, name, max_amount) VALUES ('PLF_MED', 'Medium', 15000000.00);
IF NOT EXISTS (SELECT 1 FROM plafonds WHERE code = 'PLF_LRG') INSERT INTO plafonds(code, name, max_amount) VALUES ('PLF_LRG', 'Large', 30000000.00);
IF NOT EXISTS (SELECT 1 FROM plafonds WHERE code = 'PLF_XL') INSERT INTO plafonds(code, name, max_amount) VALUES ('PLF_XL', 'XL', 50000000.00);
IF NOT EXISTS (SELECT 1 FROM plafonds WHERE code = 'PLF_XXL') INSERT INTO plafonds(code, name, max_amount) VALUES ('PLF_XXL', 'XXL', 100000000.00);
IF NOT EXISTS (SELECT 1 FROM plafonds WHERE code = 'PLF_MSME') INSERT INTO plafonds(code, name, max_amount) VALUES ('PLF_MSME', 'MSME', 75000000.00);
GO

DECLARE @actorMarketing BIGINT = (SELECT id FROM users WHERE username = 'marketing1');
DECLARE @actorBM BIGINT = (SELECT id FROM users WHERE username = 'branchmanager1');
DECLARE @actorBO BIGINT = (SELECT id FROM users WHERE username = 'backoffice1');
DECLARE @plfSML BIGINT = (SELECT id FROM plafonds WHERE code = 'PLF_SML');
DECLARE @plfMED BIGINT = (SELECT id FROM plafonds WHERE code = 'PLF_MED');
DECLARE @plfLRG BIGINT = (SELECT id FROM plafonds WHERE code = 'PLF_LRG');
DECLARE @plfXL BIGINT = (SELECT id FROM plafonds WHERE code = 'PLF_XL');
DECLARE @plfXXL BIGINT = (SELECT id FROM plafonds WHERE code = 'PLF_XXL');
DECLARE @plfMSME BIGINT = (SELECT id FROM plafonds WHERE code = 'PLF_MSME');
GO

DECLARE @j INT = 1;
WHILE @j <= 40
BEGIN
    DECLARE @appUsername NVARCHAR(50) = CONCAT('nasabah', RIGHT('00' + CAST((((@j - 1) % 20) + 1) AS VARCHAR(2)), 2));
    DECLARE @applicant BIGINT = (SELECT id FROM users WHERE username = @appUsername);
    DECLARE @status VARCHAR(20) = CASE WHEN @j % 4 = 1 THEN 'SUBMITTED' WHEN @j % 4 = 2 THEN 'REVIEWED' WHEN @j % 4 = 3 THEN 'APPROVED' ELSE 'DISBURSED' END;
    DECLARE @pf BIGINT = CASE WHEN @j % 6 = 0 THEN @plfSML WHEN @j % 6 = 1 THEN @plfMED WHEN @j % 6 = 2 THEN @plfLRG WHEN @j % 6 = 3 THEN @plfXL WHEN @j % 6 = 4 THEN @plfXXL ELSE @plfMSME END;
    DECLARE @amt DECIMAL(18,2) = CASE WHEN @pf = @plfSML THEN 3000000.00 WHEN @pf = @plfMED THEN 8000000.00 WHEN @pf = @plfLRG THEN 20000000.00 WHEN @pf = @plfXL THEN 40000000.00 WHEN @pf = @plfXXL THEN 60000000.00 ELSE 25000000.00 END;
    INSERT INTO loans(applicant_id, plafond_id, amount, current_status, created_at) VALUES (@applicant, @pf, @amt, @status, DATEADD(DAY, -@j, GETDATE()));
    DECLARE @loanId BIGINT = SCOPE_IDENTITY();
    INSERT INTO loan_approvals(loan_id, status_after, actor_id, role, remarks, timestamp) VALUES (@loanId, 'SUBMITTED', @applicant, 'NASABAH', 'Submitted', DATEADD(DAY, -@j, GETDATE()));
    IF @status IN ('REVIEWED','APPROVED','DISBURSED')
        INSERT INTO loan_approvals(loan_id, status_after, actor_id, role, remarks, timestamp) VALUES (@loanId, 'REVIEWED', @actorMarketing, 'MARKETING', 'Reviewed', DATEADD(DAY, -@j + 1, GETDATE()));
    IF @status IN ('APPROVED','DISBURSED')
        INSERT INTO loan_approvals(loan_id, status_after, actor_id, role, remarks, timestamp) VALUES (@loanId, 'APPROVED', @actorBM, 'BRANCH_MANAGER', 'Approved', DATEADD(DAY, -@j + 2, GETDATE()));
    IF @status = 'DISBURSED'
    BEGIN
        INSERT INTO loan_approvals(loan_id, status_after, actor_id, role, remarks, timestamp) VALUES (@loanId, 'DISBURSED', @actorBO, 'BACK_OFFICE', 'Disbursed', DATEADD(DAY, -@j + 3, GETDATE()));
        INSERT INTO loan_disbursements(loan_id, amount, actor_id, timestamp) VALUES (@loanId, @amt, @actorBO, DATEADD(DAY, -@j + 3, GETDATE()));
    END
    IF @status IN ('APPROVED','DISBURSED')
        INSERT INTO notifications(user_id, type, message, created_at, [read]) VALUES (@applicant, 'LOAN_APPROVED', CONCAT('Loan ', @loanId, ' approved'), GETDATE(), 0);
    IF @status = 'DISBURSED'
        INSERT INTO notifications(user_id, type, message, created_at, [read]) VALUES (@applicant, 'LOAN_DISBURSED', CONCAT('Loan ', @loanId, ' disbursed'), GETDATE(), 0);
    SET @j = @j + 1;
END
GO

IF OBJECT_ID('dbo.customers','U') IS NULL
BEGIN
    CREATE TABLE dbo.customers (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        first_name NVARCHAR(255) NOT NULL,
        last_name NVARCHAR(255) NOT NULL,
        nik NVARCHAR(50) UNIQUE NULL,
        phone_number NVARCHAR(50) NULL
    );
END
GO

DECLARE @k INT = 1;
WHILE @k <= 30
BEGIN
    DECLARE @nik NVARCHAR(50) = CONCAT('321', RIGHT('000000' + CAST(@k AS VARCHAR(6)), 6));
    IF NOT EXISTS (SELECT 1 FROM customers WHERE nik = @nik)
        INSERT INTO customers(first_name, last_name, nik, phone_number) VALUES (CONCAT('Cust', @k), 'Sample', @nik, CONCAT('08123', RIGHT('000000' + CAST(@k AS VARCHAR(6)), 6)));
    SET @k = @k + 1;
END
GO
