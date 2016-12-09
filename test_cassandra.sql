USE `server_v5`;
DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (`id` uuid(20) NOT NULL AUTO_INCREMENT,`customer_first_name` varchar(255) NOT NULL,`customer_number` bigint(20) NOT NULL,PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
CREATE TABLE `products` (`products_id` uuid(11) NOT NULL AUTO_INCREMENT,`products_ean` varchar(128) DEFAULT NULL, PRIMARY KEY (`products_id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8;