-- ===== Groups =====
INSERT INTO GROUP_MASTER (group_code, group_name, description) VALUES ('ADMIN', 'Administrator', 'Manages users, groups, and permissions');
INSERT INTO GROUP_MASTER (group_code, group_name, description) VALUES ('EMPLOYEE', 'Employee', 'Standard employee');
INSERT INTO GROUP_MASTER (group_code, group_name, description) VALUES ('MANAGER', 'Manager', 'Reviews and approves knowledge');
-- group_id: ADMIN=1, EMPLOYEE=2, MANAGER=3 (adjust below if your sequence differs)

-- ===== Modules =====
INSERT INTO MODULE_MASTER (module_code, module_name, description) VALUES ('AUTH', 'Auth & Access Administration', 'Manage users, groups, modules, access codes');
INSERT INTO MODULE_MASTER (module_code, module_name, description) VALUES ('KNOWLEDGE', 'Knowledge Management', 'Create/approve/reject knowledge');
-- module_id: AUTH=1, KNOWLEDGE=2

-- ===== AUTH module access codes =====
INSERT INTO ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_USERS', 'Create/list/update users, assign groups');
INSERT INTO ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_GROUPS', 'CRUD on groups');
INSERT INTO ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_MODULES', 'CRUD on modules');
INSERT INTO ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_ACCESS', 'CRUD on access codes within a module');
INSERT INTO ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_GROUP_ACCESS', 'Set group -> access-code allow/deny');
INSERT INTO ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_USER_ACCESS', 'Set per-user access overrides');
-- access_id: MANAGE_USERS=1, MANAGE_GROUPS=2, MANAGE_MODULES=3, MANAGE_ACCESS=4, MANAGE_GROUP_ACCESS=5, MANAGE_USER_ACCESS=6

-- ===== KNOWLEDGE module access codes =====
INSERT INTO ACCESS_MASTER (module_id, access_code, description) VALUES (2, 'CREATE', 'Submit new knowledge');
INSERT INTO ACCESS_MASTER (module_id, access_code, description) VALUES (2, 'APPROVE', 'Approve or reject pending knowledge');
-- access_id: CREATE=7, APPROVE=8

-- ===== Group -> access templates =====
-- ADMIN (group_id=1): full AUTH access
INSERT INTO GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 1, 'Y');
INSERT INTO GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 2, 'Y');
INSERT INTO GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 3, 'Y');
INSERT INTO GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 4, 'Y');
INSERT INTO GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 5, 'Y');
INSERT INTO GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 6, 'Y');

-- EMPLOYEE (group_id=2): can CREATE knowledge
INSERT INTO GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (2, 7, 'Y');

-- MANAGER (group_id=3): can CREATE + APPROVE knowledge
INSERT INTO GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (3, 7, 'Y');
INSERT INTO GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (3, 8, 'Y');

-- ===== Bootstrap admin user =====
-- password_hash must be a BCrypt hash - generate via `new BCryptPasswordEncoder().encode("yourpassword")`
-- INSERT INTO USER_MASTER (username, email, password_hash, first_name, last_name) VALUES ('admin', 'admin@example.com', '<bcrypt-hash>', 'System', 'Admin');
-- INSERT INTO USER_GROUP (user_id, group_id) VALUES (<admin_user_id>, 1);
