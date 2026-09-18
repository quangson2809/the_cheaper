-- READ ONLY. Select the intended rehearsal database before running this file.
SELECT DATABASE() AS selected_database, VERSION() AS mysql_version;
SELECT table_name FROM information_schema.tables
WHERE table_schema = DATABASE() AND table_name IN
('accounts','roles','permissions','account_roles','role_permissions','orders','product_variants')
ORDER BY table_name;
SELECT table_name, column_name, column_type, is_nullable
FROM information_schema.columns WHERE table_schema = DATABASE()
AND table_name IN ('orders','product_variants') AND column_name = 'version';
SELECT id, name FROM roles WHERE UPPER(name) IN ('USER','ADMIN');
SELECT r.id AS role_id, r.name, p.code FROM roles r
JOIN role_permissions rp ON rp.role_id=r.id JOIN permissions p ON p.id=rp.permission_id
WHERE p.code LIKE 'ORDER\_%' OR p.code LIKE 'USER\_ORDER\_%'
ORDER BY r.id,p.code;
SELECT COUNT(*) AS orders_count FROM orders;
SELECT COUNT(*) AS variants_count, SUM(stock) AS stock_sum, SUM(sold) AS sold_sum FROM product_variants;
SELECT COUNT(*) AS payments_count, SUM(amount) AS payment_amount FROM payments;
