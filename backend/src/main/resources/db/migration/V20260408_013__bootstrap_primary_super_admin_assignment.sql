-- If there is no PRIMARY_SUPER_ADMIN yet, promote the earliest SUPER_ADMIN as PRIMARY_SUPER_ADMIN.
INSERT INTO admin_user_roles (user_id, role_id, granted_by, granted_at)
SELECT picked.user_id, picked.primary_role_id, NULL, NOW(3)
FROM (
    SELECT ur.user_id, pr.id AS primary_role_id
    FROM admin_user_roles ur
    INNER JOIN admin_roles sr ON sr.id = ur.role_id AND sr.role_code = 'SUPER_ADMIN'
    INNER JOIN admin_roles pr ON pr.role_code = 'PRIMARY_SUPER_ADMIN'
    LEFT JOIN admin_user_roles existing_primary_ur
        ON existing_primary_ur.user_id = ur.user_id AND existing_primary_ur.role_id = pr.id
    WHERE existing_primary_ur.user_id IS NULL
    ORDER BY ur.granted_at ASC
    LIMIT 1
) picked
WHERE NOT EXISTS (
    SELECT 1
    FROM admin_user_roles ur2
    INNER JOIN admin_roles r2 ON r2.id = ur2.role_id
    WHERE r2.role_code = 'PRIMARY_SUPER_ADMIN'
);

