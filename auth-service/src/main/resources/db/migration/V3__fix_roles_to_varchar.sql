-- 1) Change column type from ENUM to VARCHAR
ALTER TABLE user_roles MODIFY role VARCHAR(50) NOT NULL;

-- 2) Normalize existing values
UPDATE user_roles SET role = 'ROLE_USER'  WHERE role = 'role_user';
UPDATE user_roles SET role = 'ROLE_ADMIN' WHERE role = 'role_admin';
