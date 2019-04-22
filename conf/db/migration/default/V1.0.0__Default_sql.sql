DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
	`id`	INT UNSIGNED	NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`name`	VARCHAR(32)	NOT NULL	COMMENT 'ユーザー名',
	`screen_name`	VARCHAR(16)	NOT NULL	COMMENT 'ユーザーID',
	`email`	VARCHAR(255)	NOT NULL	COMMENT 'メールアドレス',
	`password`	VARCHAR(255)	NOT NULL	COMMENT 'パスワード',
	`thumbnail_id`	INT UNSIGNED	NULL	COMMENT 'アイコンのID'
);

DROP TABLE IF EXISTS `assets`;

CREATE TABLE `assets` (
	`id`	INT UNSIGNED	NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`path`	MEDIUMTEXT	NOT NULL	COMMENT 'アセットまでのパス',
	`filename`	VARCHAR(255)	NULL	COMMENT 'ファイル名',
	`type`	VARCHAR(255)	NULL	COMMENT 'どこで使われるか'
);

DROP TABLE IF EXISTS `login_tokens`;

CREATE TABLE `login_tokens` (
	`id`	INT UNSIGNED	NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`token`	VARCHAR(255)	NOT NULL,
	`user_id`	INT UNSIGNED	NOT NULL
);

DROP TABLE IF EXISTS `products`;

CREATE TABLE `products` (
	`id`	INT UNSIGNED	NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`title`	VARCHAR(255)	NOT NULL	DEFAULT 'no title'	COMMENT 'product_title',
	`description`	LONGTEXT	NULL,
	`stock`	INT UNSIGNED	NOT NULL,
	`price`	INT	NULL,
	`sale_price`	INT	NULL
);

DROP TABLE IF EXISTS `product_thumbnails`;

CREATE TABLE `product_thumbnails` (
	`id`	INT UNSIGNED	NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`product_id`	INT UNSIGNED	NOT NULL,
	`asset_id`	INT UNSIGNED	NOT NULL
);

DROP TABLE IF EXISTS `carts`;

CREATE TABLE `carts` (
	`id`	INT UNSIGNED	NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`user_id`	INT UNSIGNED	NOT NULL,
	`product_id`	INT UNSIGNED	NOT NULL,
	`quantity`	INT	NOT NULL
);

DROP TABLE IF EXISTS `orders`;

CREATE TABLE `orders` (
	`id`	INT UNSIGNED	NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`user_id`	INT UNSIGNED	NOT NULL,
	`status`	VARCHAR(255)	NOT NULL,
	`amount`	INT	NOT NULL	COMMENT 'amount of money ( yen )'
);

DROP TABLE IF EXISTS `order_cart_records`;

CREATE TABLE `order_cart_records` (
	`id`	INT UNSIGNED	NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`user_id`	INT UNSIGNED	NOT NULL,
	`order_id`	INT UNSIGNED	NOT NULL,
	`product_id`	INT UNSIGNED	NOT NULL,
	`quantity`	INT	NOT NULL,
	`sell_price`	INT	NULL
);

DROP TABLE IF EXISTS `payment`;

CREATE TABLE `payment` (
	`id`	INT UNSIGNED	NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`type`	VARCHAR(255)	NOT NULL,
	`expired_at`	DATETIME	NULL,
	`token`	VARCHAR(255)	NULL,
	`user_id`	INT UNSIGNED	NOT NULL,
	`amount`	INT	NOT NULL,
	`order_id`	INT UNSIGNED	NOT NULL
);

DROP TABLE IF EXISTS `settings`;

CREATE TABLE `settings` (
	`id`	INT UNSIGNED	NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`key`	VARCHAR(255)	NOT NULL,
	`value`	MEDIUMTEXT	NOT NULL
);


ALTER TABLE `users` ADD CONSTRAINT `FK_assets_TO_users_1` FOREIGN KEY (
	`thumbnail_id`
)
REFERENCES `assets` (
	`id`
);

ALTER TABLE `login_tokens` ADD CONSTRAINT `FK_users_TO_login_tokens_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `users` (
	`id`
);

ALTER TABLE `product_thumbnails` ADD CONSTRAINT `FK_products_TO_product_thumbnails_1` FOREIGN KEY (
	`product_id`
)
REFERENCES `products` (
	`id`
);

ALTER TABLE `product_thumbnails` ADD CONSTRAINT `FK_assets_TO_product_thumbnails_1` FOREIGN KEY (
	`asset_id`
)
REFERENCES `assets` (
	`id`
);

ALTER TABLE `carts` ADD CONSTRAINT `FK_users_TO_carts_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `users` (
	`id`
);

ALTER TABLE `carts` ADD CONSTRAINT `FK_products_TO_carts_1` FOREIGN KEY (
	`product_id`
)
REFERENCES `products` (
	`id`
);

ALTER TABLE `orders` ADD CONSTRAINT `FK_users_TO_orders_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `users` (
	`id`
);

ALTER TABLE `order_cart_records` ADD CONSTRAINT `FK_users_TO_order_cart_records_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `users` (
	`id`
);

ALTER TABLE `order_cart_records` ADD CONSTRAINT `FK_orders_TO_order_cart_records_1` FOREIGN KEY (
	`order_id`
)
REFERENCES `orders` (
	`id`
);

ALTER TABLE `order_cart_records` ADD CONSTRAINT `FK_products_TO_order_cart_records_1` FOREIGN KEY (
	`product_id`
)
REFERENCES `products` (
	`id`
);

ALTER TABLE `payment` ADD CONSTRAINT `FK_users_TO_payment_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `users` (
	`id`
);

ALTER TABLE `payment` ADD CONSTRAINT `FK_orders_TO_payment_1` FOREIGN KEY (
	`order_id`
)
REFERENCES `orders` (
	`id`
);

