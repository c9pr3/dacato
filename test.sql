USE `server_v5`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `customer`;
CREATE TABLE customer (`id` bigint(20) NOT NULL AUTO_INCREMENT,`customer_first_name` varchar(255) NOT NULL,`customer_number` bigint(20) NOT NULL,PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
CREATE TABLE products (`products_id` int(11) NOT NULL AUTO_INCREMENT,`products_ean` varchar(128) DEFAULT NULL, PRIMARY KEY (`products_id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8;
