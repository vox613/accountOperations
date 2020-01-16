INSERT INTO wallets_table (id, account, wallet_name, create_date_time)
VALUES (nextval('wallet_id_seq'), 1, 'acc1', CURRENT_TIMESTAMP),
       (nextval('wallet_id_seq'), 0, 'acc2', CURRENT_TIMESTAMP),
       (nextval('wallet_id_seq'), 0, 'acc3', CURRENT_TIMESTAMP);



-- INSERT INTO operations_history (id, account_id, operation, transaction_amount, account_before_operations, account_after_operations)
-- VALUES  (nextval('operations_id_seq'), 1, 'sum', 1, (SELECT account FROM accounts_table WHERE id = 1), (SELECT account FROM accounts_table WHERE id = 1) + 1),
--         (nextval('operations_id_seq'), 2, 'sub', 2, (SELECT account FROM accounts_table WHERE id = 2), (SELECT account FROM accounts_table WHERE id = 2) + 2),
--         (nextval('operations_id_seq'), 2, 'sum', 3, (SELECT account FROM accounts_table WHERE id = 3), (SELECT account FROM accounts_table WHERE id = 3) + 3);
--

INSERT INTO journal (id, wallet_id, wallet_name, transaction_type, transaction_amount, wallet_account_after_transaction, transactional_date)
VALUES  (nextval('transactional_id_seq'), 1, (SELECT wallet_name FROM wallets_table WHERE id = 1), 'sum', 1, (SELECT account FROM wallets_table WHERE id = 1), CURRENT_TIMESTAMP);




-- INSERT INTO condition (id, name)
-- VALUES (nextval('condition_id_seq'), 'идеальное'),
--        (nextval('condition_id_seq'), 'хорошее'),
--        (nextval('condition_id_seq'), 'среднее'),
--        (nextval('condition_id_seq'), 'плохое');
--
-- INSERT INTO category (id, name)
-- VALUES (nextval('category_id_seq'), 'Одежда'),
--        (nextval('category_id_seq'), 'Электроника'),
--        (nextval('category_id_seq'), 'Спорттовары'),
--        (nextval('category_id_seq'), 'Услуги');
--
-- INSERT INTO users (id, username, first_name, last_name, password,
--                    phone_number, email, city_id, role_id)
-- VALUES (nextval('users_id_seq'), 'username1', 'first name 1', 'last name 1',
--         'password1', '1234565', 'email1',
--         (SELECT id FROM city WHERE id = 1), 1),
--        (nextval('users_id_seq'), 'username2', 'first name 2', 'last name 2',
--         'password2', '1234564', 'email2',
--         (SELECT id FROM city WHERE id = 2), 2),
--        (nextval('users_id_seq'), 'username3', 'first name 3', 'last name 3',
--         'password3', '1234563', 'email3',
--         (SELECT id FROM city WHERE id = 3), 2),
--        (nextval('users_id_seq'), 'username4', 'first name 4', 'last name 4',
--         'password4', '1234562', 'email4',
--         (SELECT id FROM city WHERE id = 4), 2),
--        (nextval('users_id_seq'), 'username5', 'first name 5', 'last name 5',
--         'password5', '1234561', 'email5',
--         (SELECT id FROM city WHERE id = 1), 2);
--
--
-- INSERT INTO message (id, body, sender_id, receiver_id)
-- VALUES (nextval('message_id_seq'), 'Hello. I want to get my purchase.',
--         (SELECT id FROM users WHERE username = 'username3'), (SELECT id FROM users WHERE username = 'username2')),
--        (nextval('message_id_seq'), 'Ok, call me',
--         (SELECT id FROM users WHERE username = 'username3'), (SELECT id FROM users WHERE username = 'username2')),
--        (nextval('message_id_seq'), 'I can''t, call me back pls',
--         (SELECT id FROM users WHERE username = 'username2'), (SELECT id FROM users WHERE username = 'username3')),
--        (nextval('message_id_seq'), 'Ok, I will call you tomorrow',
--         (SELECT id FROM users WHERE username = 'username2'), (SELECT id FROM users WHERE username = 'username3'));
--
-- INSERT INTO lot (id, category_id, condition_id, creation_time, last_mod_time, description,
--                   user_id, current_price, min_price, max_price, step_price, city_id, name)
-- VALUES (nextval('lots_id_seq'), 1, 1, now(), now(), 'Simple car', (SELECT id FROM users WHERE username = 'username3'),
--         1000, 500, 1500, 75, 1, 'Car 1'),
--        (nextval('lots_id_seq'), 1, 3, now(), now(), 'Simple car', (SELECT id FROM users WHERE username = 'username3'),
--         1000, 500, 1500, 75, 2, 'Car 2'),
--        (nextval('lots_id_seq'), 1, 3, now(), now(), 'Simple phone', (SELECT id FROM users WHERE username = 'username2'),
--         1000, 500, 1500, 75, 3, 'Phone 1'),
--        (nextval('lots_id_seq'), 1, 3, now(), now(), 'Simple phone', (SELECT id FROM users WHERE username = 'username2'),
--         1000, 500, 1500, 75, 4, 'Phone 2'),
--        (nextval('lots_id_seq'), 1, 3, now(), now(), 'Simple car', (SELECT id FROM users WHERE username = 'username4'),
--         1000, 500, 1500, 75, 3, 'Car 3'),
--        (nextval('lots_id_seq'), 2, 2, now(), now(), 'Simple car', (SELECT id FROM users WHERE username = 'username4'),
--         1000, 500, 1500, 75, 2, 'Car 4'),
--        (nextval('lots_id_seq'), 2, 2, now(), now(), 'Simple car', (SELECT id FROM users WHERE username = 'username4'),
--         1000, 500, 1500, 75, 1, 'Car 5');