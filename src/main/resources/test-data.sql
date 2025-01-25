--- e2e test data
INSERT INTO PRODUCTS (PRODUCT_ID, NAME)
VALUES (35455, 'Product 35455 - Test');

INSERT INTO PRODUCTS (PRODUCT_ID, NAME)
VALUES (1, 'Product 1 - Test');

INSERT INTO BRANDS (BRAND_ID, NAME)
VALUES (1, 'ZARA');

INSERT INTO BRANDS (BRAND_ID, NAME)
VALUES (2, 'ZARA - COPY');

--- added 3 prices to test the priority for each partition
INSERT INTO prices (price_id, brand_id, start_date, end_date, price_list, product_id, priority, price, currency)
VALUES
    (RANDOM_UUID(), 1, '2020-06-14 00:00:00', '2020-12-31 23:59:59', 1, 35455, 0, 35.50, 'EUR'),
    (RANDOM_UUID(), 1, '2020-06-14 15:00:00', '2020-06-14 18:30:00', 2, 35455, 1, 25.45, 'EUR'),
    (RANDOM_UUID(), 1, '2020-06-15 00:00:00', '2020-06-15 11:00:00', 3, 35455, 1, 30.50, 'EUR'),
    (RANDOM_UUID(), 1, '2020-06-15 16:00:00', '2020-12-31 23:59:59', 4, 35455, 1, 38.95, 'EUR'),
    (RANDOM_UUID(), 1, '2020-06-14 15:00:00', '2020-06-14 18:30:00', 2, 35455, 0, 25.45, 'EUR'),
    (RANDOM_UUID(), 1, '2020-06-15 00:00:00', '2020-06-15 11:00:00', 3, 35455, 0, 30.50, 'EUR'),
    (RANDOM_UUID(), 1, '2020-06-15 16:00:00', '2020-12-31 23:59:59', 4, 35455, 0, 38.95, 'EUR');
