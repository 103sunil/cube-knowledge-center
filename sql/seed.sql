-- No hardcoded IDs anywhere in this file. Every insert below looks up the
-- actual generated id by its stable, unique code (group_code, module_code,
-- access_code, username) - this file is correct no matter what the IDENTITY
-- sequences have generated, including after partial/failed prior runs.
-- Run reset.sql first if you need a clean slate.

-- ===== Groups =====
INSERT INTO CU_GROUP_MASTER (group_code, group_name, description) VALUES ('ADMIN', 'Administrator', 'Manages users, groups, and permissions');
INSERT INTO CU_GROUP_MASTER (group_code, group_name, description) VALUES ('EMPLOYEE', 'Employee', 'Can search/view/download and submit for review');
INSERT INTO CU_GROUP_MASTER (group_code, group_name, description) VALUES ('MANAGER', 'Manager', 'Reviews and approves/rejects submissions');

-- ===== Modules =====
INSERT INTO CU_MODULE_MASTER (module_code, module_name, description) VALUES ('AUTH', 'Auth & Access Administration', 'Manage users, groups, modules, access codes');
INSERT INTO CU_MODULE_MASTER (module_code, module_name, description) VALUES ('KNOWLEDGE', 'Knowledge Management', 'Search/view/download/create/approve knowledge');

-- ===== AUTH module access codes =====
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'MANAGE_USERS', 'Create/list/update users, assign groups' FROM CU_MODULE_MASTER WHERE module_code = 'AUTH';
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'MANAGE_GROUPS', 'CRUD on groups' FROM CU_MODULE_MASTER WHERE module_code = 'AUTH';
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'MANAGE_MODULES', 'CRUD on modules' FROM CU_MODULE_MASTER WHERE module_code = 'AUTH';
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'MANAGE_ACCESS', 'CRUD on access codes within a module' FROM CU_MODULE_MASTER WHERE module_code = 'AUTH';
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'MANAGE_GROUP_ACCESS', 'Set group -> access-code allow/deny' FROM CU_MODULE_MASTER WHERE module_code = 'AUTH';
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'MANAGE_USER_ACCESS', 'Set per-user access overrides' FROM CU_MODULE_MASTER WHERE module_code = 'AUTH';

-- ===== KNOWLEDGE module access codes =====
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'CREATE', 'Submit new knowledge for review' FROM CU_MODULE_MASTER WHERE module_code = 'KNOWLEDGE';
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'APPROVE', 'Approve or reject pending knowledge' FROM CU_MODULE_MASTER WHERE module_code = 'KNOWLEDGE';
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'VIEW', 'View a knowledge article' FROM CU_MODULE_MASTER WHERE module_code = 'KNOWLEDGE';
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'SEARCH', 'Search published knowledge' FROM CU_MODULE_MASTER WHERE module_code = 'KNOWLEDGE';
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'DOWNLOAD', 'Download an attachment' FROM CU_MODULE_MASTER WHERE module_code = 'KNOWLEDGE';
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'UPDATE', 'Edit an existing knowledge item' FROM CU_MODULE_MASTER WHERE module_code = 'KNOWLEDGE';
INSERT INTO CU_ACCESS_MASTER (module_id, access_code, description)
  SELECT module_id, 'DELETE', 'Delete a knowledge item' FROM CU_MODULE_MASTER WHERE module_code = 'KNOWLEDGE';

-- ===== Group -> access templates =====

-- ADMIN: full AUTH access
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'ADMIN' AND a.module_id = m.module_id AND m.module_code = 'AUTH'
    AND a.access_code = 'MANAGE_USERS';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'ADMIN' AND a.module_id = m.module_id AND m.module_code = 'AUTH'
    AND a.access_code = 'MANAGE_GROUPS';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'ADMIN' AND a.module_id = m.module_id AND m.module_code = 'AUTH'
    AND a.access_code = 'MANAGE_MODULES';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'ADMIN' AND a.module_id = m.module_id AND m.module_code = 'AUTH'
    AND a.access_code = 'MANAGE_ACCESS';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'ADMIN' AND a.module_id = m.module_id AND m.module_code = 'AUTH'
    AND a.access_code = 'MANAGE_GROUP_ACCESS';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'ADMIN' AND a.module_id = m.module_id AND m.module_code = 'AUTH'
    AND a.access_code = 'MANAGE_USER_ACCESS';

-- EMPLOYEE: search/view/download + submit, NOT approve
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'EMPLOYEE' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'CREATE';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'EMPLOYEE' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'VIEW';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'EMPLOYEE' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'SEARCH';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'EMPLOYEE' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'DOWNLOAD';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'N'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'EMPLOYEE' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'APPROVE';

-- MANAGER: everything an employee has, plus APPROVE/UPDATE/DELETE
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'MANAGER' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'CREATE';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'MANAGER' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'APPROVE';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'MANAGER' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'VIEW';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'MANAGER' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'SEARCH';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'MANAGER' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'DOWNLOAD';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'MANAGER' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'UPDATE';
INSERT INTO CU_GROUP_ACCESS_TEMPLATE (group_id, access_id, allowed)
  SELECT g.group_id, a.access_id, 'Y'
  FROM CU_GROUP_MASTER g, CU_ACCESS_MASTER a, CU_MODULE_MASTER m
  WHERE g.group_code = 'MANAGER' AND a.module_id = m.module_id AND m.module_code = 'KNOWLEDGE'
    AND a.access_code = 'DELETE';

-- ===== Bootstrap users =====
-- status is left unset - defaults to 'A' per schema.sql's DEFAULT 'A' clause.
-- If a login ever comes back "Account is inactive", check this directly:
--   SELECT username, status FROM CU_USER_MASTER;

-- admin / Admin@123
INSERT INTO CU_USER_MASTER (username, email, password_hash, first_name, last_name) VALUES
  ('admin', 'admin@example.com', '$2b$10$fZg8Srwb8.Q6hqKRClV6V.iNww1KcSSvZcQdmSZJSzXWDT1txyQba', 'System', 'Admin');
INSERT INTO CU_USER_GROUP (user_id, group_id)
  SELECT u.user_id, g.group_id FROM CU_USER_MASTER u, CU_GROUP_MASTER g
  WHERE u.username = 'admin' AND g.group_code = 'ADMIN';

-- rahul.manager / Manager@123
INSERT INTO CU_USER_MASTER (username, email, password_hash, first_name, last_name) VALUES
  ('rahul.manager', 'rahul.manager@example.com', '$2b$10$cc5o4bt7O5uss/sXbiaKTe8tucKBARwEULEl7rSlBup4egpkq/ObO', 'Rahul', 'Sharma');
INSERT INTO CU_USER_GROUP (user_id, group_id)
  SELECT u.user_id, g.group_id FROM CU_USER_MASTER u, CU_GROUP_MASTER g
  WHERE u.username = 'rahul.manager' AND g.group_code = 'MANAGER';

-- priya.employee / Employee@123
INSERT INTO CU_USER_MASTER (username, email, password_hash, first_name, last_name) VALUES
  ('priya.employee', 'priya.employee@example.com', '$2b$10$Q4lHrtZbAHvPL2lpdsr5Y.krhNE6uoH0rJfrvZgVgC0wMmMJHKPja', 'Priya', 'Verma');
INSERT INTO CU_USER_GROUP (user_id, group_id)
  SELECT u.user_id, g.group_id FROM CU_USER_MASTER u, CU_GROUP_MASTER g
  WHERE u.username = 'priya.employee' AND g.group_code = 'EMPLOYEE';

COMMIT;