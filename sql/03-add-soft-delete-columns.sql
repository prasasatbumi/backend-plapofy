USE Plapofy;
GO

-- Add deleted column to users table if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE Name = N'deleted' AND Object_ID = Object_ID(N'users'))
BEGIN
    ALTER TABLE users ADD deleted bit DEFAULT 0;
END
GO

-- Add deleted column to customers table if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE Name = N'deleted' AND Object_ID = Object_ID(N'customers'))
BEGIN
    ALTER TABLE customers ADD deleted bit DEFAULT 0;
END
GO

-- Update existing records to have deleted = 0
UPDATE users SET deleted = 0 WHERE deleted IS NULL;
UPDATE customers SET deleted = 0 WHERE deleted IS NULL;
GO
