-- One-time cleanup: super-admin roles and business roles cannot coexist.
-- Keep super relation roles, remove OPERATOR/SALES/AUDITOR for any user that has
-- PRIMARY_SUPER_ADMIN or SUPER_ADMIN.
DELETE ur
FROM admin_user_roles ur
INNER JOIN admin_roles r ON r.id = ur.role_id
INNER JOIN admin_user_roles ur_super ON ur_super.user_id = ur.user_id
INNER JOIN admin_roles r_super ON r_super.id = ur_super.role_id
WHERE r.role_code IN ('OPERATOR', 'SALES', 'AUDITOR')
  AND r_super.role_code IN ('PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN');
