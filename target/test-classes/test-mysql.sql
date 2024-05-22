
BEGIN;

DROP TABLE IF EXISTS transaction;

DROP TABLE IF EXISTS card;

CREATE TABLE card (
    card_id VARCHAR(16) PRIMARY KEY,
    product_id VARCHAR(6) NOT NULL,
    card_holder_name VARCHAR(255) NOT NULL,
    expiration_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL,
    is_blocked BOOLEAN NOT NULL,
    balance DECIMAL(10, 2) NOT NULL
);

-- Crea la tabla TRANSACTION
CREATE TABLE transaction (
    transaction_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    card_id VARCHAR(255) NOT NULL,
    transaction_date DATETIME NOT NULL,
    amount DOUBLE NOT NULL,
    status VARCHAR(255) COMMENT 'COMPLETED, ANULLED',
    is_anulled BOOLEAN NOT NULL,
    FOREIGN KEY (card_id) REFERENCES card(card_id)
);

-- Inserta datos de prueba
INSERT INTO card (card_id, product_id, card_holder_name, expiration_date, is_active, is_blocked, balance)
VALUES ('1234567890123456', '123456', 'John Doe', '2026-01-01', true, false, 500.00);


INSERT INTO transaction (transaction_id, card_id, transaction_date, amount, status, is_anulled) VALUES
(1, '1234567890123456', '2023-05-19 10:00:00', 100.0, 'COMPLETED', false);


COMMIT;