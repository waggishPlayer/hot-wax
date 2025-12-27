INSERT INTO customer (first_name, last_name) VALUES ('John', 'Doe');
INSERT INTO customer (first_name, last_name) VALUES ('Jane', 'Smith');
INSERT INTO customer (first_name, last_name) VALUES ('Alice', 'Wonderland');

INSERT INTO contact_mech (customer_id, street_address, city, state, postal_code, phone_number, email) VALUES (1, '123 Main St', 'Springfield', 'IL', '62701', '555-1234', 'john.doe@example.com');
INSERT INTO contact_mech (customer_id, street_address, city, state, postal_code, phone_number, email) VALUES (1, '456 Work Blvd', 'Springfield', 'IL', '62702', '555-9876', 'john.work@example.com');
INSERT INTO contact_mech (customer_id, street_address, city, state, postal_code, phone_number, email) VALUES (2, '789 Oak Ave', 'Metropolis', 'NY', '10001', '555-5678', 'jane.smith@example.com');
INSERT INTO contact_mech (customer_id, street_address, city, state, postal_code, phone_number, email) VALUES (3, '321 Pine Ln', 'Gotham', 'NJ', '07001', '555-9012', 'alice@example.com');

INSERT INTO product (product_name, color, size) VALUES ('T-Shirt', 'Red', 'M');
INSERT INTO product (product_name, color, size) VALUES ('Jeans', 'Blue', '32');
INSERT INTO product (product_name, color, size) VALUES ('Sneakers', 'White', '10');
INSERT INTO product (product_name, color, size) VALUES ('Jacket', 'Black', 'L');
INSERT INTO product (product_name, color, size) VALUES ('Hat', 'Green', 'OneSize');
