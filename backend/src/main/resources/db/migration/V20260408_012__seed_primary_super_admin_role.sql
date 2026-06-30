-- Stage 01 seed PRIMARY_SUPER_ADMIN role for hierarchical super-admin governance.
INSERT INTO admin_roles (id, role_code, display_name, description)
SELECT UUID(), 'PRIMARY_SUPER_ADMIN', '主超级管理员', '超管关系治理与全量管理权限'
WHERE NOT EXISTS (SELECT 1 FROM admin_roles WHERE role_code = 'PRIMARY_SUPER_ADMIN');

