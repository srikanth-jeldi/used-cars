ALTER TABLE user_roles
    MODIFY COLUMN role ENUM('role_user','role_admin') NOT NULL;