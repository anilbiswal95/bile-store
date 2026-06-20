-- V7__assign_existing_products_categories.sql
UPDATE products SET category_id = 1 WHERE id IN (1,3,4,6,7,8,9,11,12);
UPDATE products SET category_id = 2 WHERE id IN (2,5,10);
