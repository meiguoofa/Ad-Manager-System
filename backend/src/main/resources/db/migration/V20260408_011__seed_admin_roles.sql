-- Stage 01 seed roles for admin RBAC.
INSERT INTO admin_roles (id, role_code, display_name, description)
SELECT UUID(), 'SUPER_ADMIN', '超级管理员', '全量管理权限'
WHERE NOT EXISTS (SELECT 1 FROM admin_roles WHERE role_code = 'SUPER_ADMIN');

INSERT INTO admin_roles (id, role_code, display_name, description)
SELECT UUID(), 'OPERATOR', '运营人员', '更新发布、资源审核、用户治理'
WHERE NOT EXISTS (SELECT 1 FROM admin_roles WHERE role_code = 'OPERATOR');

INSERT INTO admin_roles (id, role_code, display_name, description)
SELECT UUID(), 'SALES', '销售人员', '订单、许可证、客户信息只读'
WHERE NOT EXISTS (SELECT 1 FROM admin_roles WHERE role_code = 'SALES');

INSERT INTO admin_roles (id, role_code, display_name, description)
SELECT UUID(), 'AUDITOR', '审计人员', '全域只读和审计查看'
WHERE NOT EXISTS (SELECT 1 FROM admin_roles WHERE role_code = 'AUDITOR');
