-- READ ONLY. Run only AFTER both version columns exist and startup completes.
SELECT COUNT(*) AS null_order_versions FROM orders WHERE version IS NULL;
SELECT COUNT(*) AS null_variant_versions FROM product_variants WHERE version IS NULL;
-- Expected: no rows.
SELECT r.id, r.name, p.code FROM roles r
JOIN role_permissions rp ON rp.role_id=r.id JOIN permissions p ON p.id=rp.permission_id
WHERE r.name='USER' AND p.code IN
('ORDER_READ','ORDER_UPDATE','ORDER_CONFIRM','ORDER_CANCEL','ORDER_DELIVERY_UPDATE','ORDER_PAYMENT_COLLECT');
-- Expected: one USER row with 3, assuming the canonical USER role exists.
SELECT r.id,r.name,COUNT(p.id) AS baseline_order_permissions FROM roles r
LEFT JOIN role_permissions rp ON rp.role_id=r.id
LEFT JOIN permissions p ON p.id=rp.permission_id
AND p.code IN ('USER_ORDER_CREATE','USER_ORDER_READ','USER_ORDER_CANCEL')
WHERE r.name='USER' GROUP BY r.id,r.name;
-- Compare with the explicitly approved role-to-permission plan; do not auto-grant.
SELECT r.id AS role_id,r.name,p.code FROM roles r
JOIN role_permissions rp ON rp.role_id=r.id JOIN permissions p ON p.id=rp.permission_id
WHERE r.name NOT IN ('USER','ADMIN') AND p.code LIKE 'ORDER\_%' ORDER BY r.id,p.code;
SELECT COUNT(*) AS orders_count FROM orders;
SELECT COUNT(*) AS variants_count, SUM(stock) AS stock_sum, SUM(sold) AS sold_sum FROM product_variants;
SELECT COUNT(*) AS payments_count, SUM(amount) AS payment_amount FROM payments;
