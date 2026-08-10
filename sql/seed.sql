-- ===== Groups =====
INSERT INTO CU_GROUP_MASTER (group_code, group_name, description) VALUES ('ADMIN', 'Administrator', 'Manages users, groups, and permissions');
INSERT INTO CU_GROUP_MASTER (group_code, group_name, description) VALUES ('EMPLOYEE', 'Employee', 'Can search/view/download and submit for review');
INSERT INTO CU_GROUP_MASTER (group_code, group_name, description) VALUES ('MANAGER', 'Manager', 'Reviews and approves/rejects submissions');
-- group_id: ADMIN=1, EMPLOYEE=2, MANAGER=3

-- ===== Modules =====
INSERT INTO CU_MODULE_MASTER (module_code, module_name, description) VALUES ('AUTH', 'Auth & Access Administration', 'Manage users, groups, modules, access codes');
INSERT INTO CU_MODULE_MASTER (module_code, module_name, description) VALUES ('KNOWLEDGE', 'Knowledge Management', 'Search/view/download/create/approve knowledge');
-- module_id: AUTH=1, KNOWLEDGE=2

-- ===== AUTH module access codes =====
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_USERS', 'Create/list/update users, assign groups');
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_GROUPS', 'CRUD on groups');
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_MODULES', 'CRUD on modules');
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_ACCESS', 'CRUD on access codes within a module');
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_GROUP_ACCESS', 'Set group -> access-code allow/deny');
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (1, 'MANAGE_USER_ACCESS', 'Set per-user access overrides');
-- access_id: MANAGE_USERS=1, MANAGE_GROUPS=2, MANAGE_MODULES=3, MANAGE_ACCESS=4, MANAGE_GROUP_ACCESS=5, MANAGE_USER_ACCESS=6

-- ===== KNOWLEDGE module access codes =====
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (2, 'CREATE', 'Submit new knowledge for review');
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (2, 'APPROVE', 'Approve or reject pending knowledge');
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (2, 'VIEW', 'View a knowledge article');
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (2, 'SEARCH', 'Search published knowledge');
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (2, 'DOWNLOAD', 'Download an attachment');
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (2, 'UPDATE', 'Edit an existing knowledge item');
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description) VALUES (2, 'DELETE', 'Delete a knowledge item');
-- access_id: CREATE=7, APPROVE=8, VIEW=9, SEARCH=10, DOWNLOAD=11, UPDATE=12, DELETE=13

-- ===== Group -> access templates =====
-- ADMIN (group_id=1): full AUTH access
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 1, 'Y');
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 2, 'Y');
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 3, 'Y');
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 4, 'Y');
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 5, 'Y');
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (1, 6, 'Y');
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (3, 12, 'Y'); -- UPDATE
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (3, 13, 'Y'); -- DELETE

-- EMPLOYEE (group_id=2): search/view/download + submit, NOT approve
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (2, 7, 'Y');  -- CREATE
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (2, 9, 'Y');  -- VIEW
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (2, 10, 'Y'); -- SEARCH
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (2, 11, 'Y'); -- DOWNLOAD
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (2, 8, 'N');  -- APPROVE explicitly denied

-- MANAGER (group_id=3): everything an employee has, plus APPROVE
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (3, 7, 'Y');  -- CREATE
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (3, 8, 'Y');  -- APPROVE
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (3, 9, 'Y');  -- VIEW
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (3, 10, 'Y'); -- SEARCH
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed) VALUES (3, 11, 'Y'); -- DOWNLOAD


-- ===== Bootstrap users =====
-- admin / Admin@123
INSERT INTO CU_USER_MASTER (username, email, password_hash, first_name, last_name) VALUES
  ('admin', 'admin@example.com', '$2b$10$fZg8Srwb8.Q6hqKRClV6V.iNww1KcSSvZcQdmSZJSzXWDT1txyQba', 'System', 'Admin');
INSERT INTO CU_USER_GROUP (user_id, group_id) VALUES (1, 1);

-- rahul.manager / Manager@123
INSERT INTO CU_USER_MASTER (username, email, password_hash, first_name, last_name) VALUES
  ('rahul.manager', 'rahul.manager@example.com', '$2b$10$cc5o4bt7O5uss/sXbiaKTe8tucKBARwEULEl7rSlBup4egpkq/ObO', 'Rahul', 'Sharma');
INSERT INTO CU_USER_GROUP (user_id, group_id) VALUES (2, 3);

-- priya.employee / Employee@123
INSERT INTO CU_USER_MASTER (username, email, password_hash, first_name, last_name) VALUES
  ('priya.employee', 'priya.employee@example.com', '$2b$10$Q4lHrtZbAHvPL2lpdsr5Y.krhNE6uoH0rJfrvZgVgC0wMmMJHKPja', 'Priya', 'Verma');
INSERT INTO CU_USER_GROUP (user_id, group_id) VALUES (3, 2);
-- NOTE: user_id assumes IDENTITY sequence starts at 1 with nothing else inserted first.