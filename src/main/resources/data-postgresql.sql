INSERT INTO wallets_table (id, account, wallet_name, create_date_time)
VALUES (nextval('wallet_id_seq'), 1, 'acc1', CURRENT_TIMESTAMP),
       (nextval('wallet_id_seq'), 10005.05, 'acc2', CURRENT_TIMESTAMP),
       (nextval('wallet_id_seq'), 0, 'acc3', CURRENT_TIMESTAMP);


INSERT INTO journal (id, wallet_id, wallet_name, transaction_type, transaction_amount, wallet_account_after_transaction, transactional_date)
VALUES  (nextval('transactional_id_seq'), 1, (SELECT wallet_name FROM wallets_table WHERE id = 1), 'sum', 1, (SELECT account FROM wallets_table WHERE id = 1), CURRENT_TIMESTAMP);
