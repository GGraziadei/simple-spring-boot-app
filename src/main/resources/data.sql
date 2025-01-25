INSERT INTO PRODUCTS (PRODUCT_ID, NAME)
VALUES (35455, 'Product 35455 - Test');

INSERT INTO PRODUCTS (PRODUCT_ID, NAME)
VALUES (1, 'Product 1 - Test');

INSERT INTO BRANDS (BRAND_ID, NAME)
VALUES (1, 'ZARA');

INSERT INTO BRANDS (BRAND_ID, NAME)
VALUES (2, 'ZARA - COPY');

INSERT INTO PRICES (PRICE_ID, PRODUCT_ID, PRICE, CURRENCY, START_DATE, END_DATE, PRIORITY, PRICE_LIST, BRAND_ID)
VALUES (RANDOM_UUID(), 1, 35.50, 'EUR', '2020-06-14 00:00:00', '2020-12-31 23:59:59', 0, 1, 1);

INSERT INTO PRICES (PRICE_ID, PRODUCT_ID, PRICE, CURRENCY, START_DATE, END_DATE, PRIORITY, PRICE_LIST, BRAND_ID)
VALUES (RANDOM_UUID(), 1, 25.50, 'EUR', '2020-06-14 00:00:00', '2020-12-31 23:59:59', 1, 1, 1);

INSERT INTO PRICES (PRICE_ID, PRODUCT_ID, PRICE, CURRENCY, START_DATE, END_DATE, PRIORITY, PRICE_LIST, BRAND_ID)
VALUES (RANDOM_UUID(), 1, 25.50, 'EUR', '2020-06-14 00:00:00', '2020-12-31 23:59:59', 1, 2, 1);

INSERT INTO PRICES (PRICE_ID, PRODUCT_ID, PRICE, CURRENCY, START_DATE, END_DATE, PRIORITY, PRICE_LIST, BRAND_ID)
VALUES (RANDOM_UUID(), 1, 25.50, 'EUR', '2020-06-14 00:00:00', '2020-12-31 23:59:59', 1, 2, 2);

