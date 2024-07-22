-- drop table wallet;
CREATE TABLE IF NOT EXISTS wallet
(
    wallet_id VARCHAR(128),
    operation_type VARCHAR(64),
    amount REAL
    );