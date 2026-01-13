IF NOT EXISTS (
    SELECT name FROM sys.databases WHERE name = 'Plapofy'
)
BEGIN
    CREATE DATABASE Plapofy;
END
GO

USE Plapofy;
GO

IF OBJECT_ID('dbo.users','U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(255) NOT NULL UNIQUE,
        email NVARCHAR(255) NULL,
        password NVARCHAR(255) NULL,
        is_active BIT NULL
    );
END
GO

IF OBJECT_ID('dbo.roles','U') IS NULL
BEGIN
    CREATE TABLE dbo.roles (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        role_name NVARCHAR(255) NOT NULL UNIQUE,
        deleted BIT NULL DEFAULT(0)
    );
END
GO

IF OBJECT_ID('dbo.user_roles','U') IS NULL
BEGIN
    CREATE TABLE dbo.user_roles (
        user_id BIGINT NOT NULL,
        role_id BIGINT NOT NULL,
        CONSTRAINT PK_user_roles PRIMARY KEY (user_id, role_id),
        CONSTRAINT FK_user_roles_user FOREIGN KEY (user_id) REFERENCES dbo.users(id),
        CONSTRAINT FK_user_roles_role FOREIGN KEY (role_id) REFERENCES dbo.roles(id)
    );
END
GO

IF OBJECT_ID('dbo.permissions','U') IS NULL
BEGIN
    CREATE TABLE dbo.permissions (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        code NVARCHAR(255) NOT NULL UNIQUE,
        name NVARCHAR(255) NOT NULL
    );
END
GO

IF OBJECT_ID('dbo.role_permissions','U') IS NULL
BEGIN
    CREATE TABLE dbo.role_permissions (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        role_id BIGINT NOT NULL,
        permission_id BIGINT NOT NULL,
        CONSTRAINT UQ_role_perm UNIQUE (role_id, permission_id),
        CONSTRAINT FK_role_permissions_role FOREIGN KEY (role_id) REFERENCES dbo.roles(id),
        CONSTRAINT FK_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES dbo.permissions(id)
    );
END
GO

IF OBJECT_ID('dbo.menus','U') IS NULL
BEGIN
    CREATE TABLE dbo.menus (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        code NVARCHAR(255) NOT NULL UNIQUE,
        name NVARCHAR(255) NOT NULL,
        path NVARCHAR(512) NOT NULL
    );
END
GO

IF OBJECT_ID('dbo.role_menus','U') IS NULL
BEGIN
    CREATE TABLE dbo.role_menus (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        role_id BIGINT NOT NULL,
        menu_id BIGINT NOT NULL,
        CONSTRAINT UQ_role_menu UNIQUE (role_id, menu_id),
        CONSTRAINT FK_role_menus_role FOREIGN KEY (role_id) REFERENCES dbo.roles(id),
        CONSTRAINT FK_role_menus_menu FOREIGN KEY (menu_id) REFERENCES dbo.menus(id)
    );
END
GO

IF OBJECT_ID('dbo.plafonds','U') IS NULL
BEGIN
    CREATE TABLE dbo.plafonds (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        code NVARCHAR(100) NOT NULL UNIQUE,
        name NVARCHAR(255) NOT NULL,
        max_amount DECIMAL(18,2) NOT NULL
    );
END
GO

IF OBJECT_ID('dbo.loans','U') IS NULL
BEGIN
    CREATE TABLE dbo.loans (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        applicant_id BIGINT NOT NULL,
        plafond_id BIGINT NOT NULL,
        amount DECIMAL(18,2) NOT NULL,
        current_status VARCHAR(20) NOT NULL,
        created_at DATETIME2 NOT NULL,
        CONSTRAINT FK_loans_applicant FOREIGN KEY (applicant_id) REFERENCES dbo.users(id),
        CONSTRAINT FK_loans_plafond FOREIGN KEY (plafond_id) REFERENCES dbo.plafonds(id),
        CONSTRAINT CK_loans_status CHECK (current_status IN ('DRAFT','SUBMITTED','REVIEWED','APPROVED','DISBURSED'))
    );
END
GO

IF OBJECT_ID('dbo.loan_approvals','U') IS NULL
BEGIN
    CREATE TABLE dbo.loan_approvals (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        loan_id BIGINT NOT NULL,
        status_after VARCHAR(20) NOT NULL,
        actor_id BIGINT NOT NULL,
        role NVARCHAR(50) NOT NULL,
        remarks NVARCHAR(MAX) NULL,
        timestamp DATETIME2 NOT NULL,
        CONSTRAINT FK_loan_approvals_loan FOREIGN KEY (loan_id) REFERENCES dbo.loans(id),
        CONSTRAINT FK_loan_approvals_actor FOREIGN KEY (actor_id) REFERENCES dbo.users(id),
        CONSTRAINT CK_loan_approvals_status CHECK (status_after IN ('DRAFT','SUBMITTED','REVIEWED','APPROVED','DISBURSED'))
    );
END
GO

IF OBJECT_ID('dbo.loan_disbursements','U') IS NULL
BEGIN
    CREATE TABLE dbo.loan_disbursements (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        loan_id BIGINT NOT NULL,
        amount DECIMAL(18,2) NOT NULL,
        actor_id BIGINT NOT NULL,
        timestamp DATETIME2 NOT NULL,
        CONSTRAINT UQ_loan_disb UNIQUE (loan_id),
        CONSTRAINT FK_loan_disb_loan FOREIGN KEY (loan_id) REFERENCES dbo.loans(id),
        CONSTRAINT FK_loan_disb_actor FOREIGN KEY (actor_id) REFERENCES dbo.users(id)
    );
END
GO

IF OBJECT_ID('dbo.notifications','U') IS NULL
BEGIN
    CREATE TABLE dbo.notifications (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id BIGINT NOT NULL,
        type NVARCHAR(100) NOT NULL,
        message NVARCHAR(MAX) NOT NULL,
        created_at DATETIME2 NOT NULL,
        [read] BIT NOT NULL,
        CONSTRAINT FK_notifications_user FOREIGN KEY (user_id) REFERENCES dbo.users(id)
    );
END
GO
