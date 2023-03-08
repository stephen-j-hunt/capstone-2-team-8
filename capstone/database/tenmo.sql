BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_transaction;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS tenmo_user;
DROP SEQUENCE IF EXISTS seq_user_id;

CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) UNIQUE NOT NULL,
	password_hash varchar(200) NOT NULL,
	role varchar(20),
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);
CREATE TABLE account (
	account_id SERIAL NOT NULL PRIMARY KEY,
	user_id int NOT NULL,
	balance real NOT NULL DEFAULT 1000.00,
	CONSTRAINT FK_account_user_id FOREIGN KEY (user_id) REFERENCES user(user_id)
);
CREATE TABLE tenmo_transaction (
	transfer_id SERIAL NOT NULL PRIMARY KEY,
	sender_id int NOT NULL,
	receiver_id int NOT NULL,
	is_request boolean NOT NULL,
	status boolean NOT NULL,
	amount real NOT NULL,
	time datetime NOT NULL
	CONSTRAINT FK_tenmo_transaction_sender_id FOREIGN KEY (sender_id) REFERENCES account(account_id),
	CONSTRAINT FK_tenmo_transaction_receiver_id FOREIGN KEY (receiver_id) REFERENCES account(account_id)
);
COMMIT;
