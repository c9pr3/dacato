CREATE DATABASE /*!32312 IF NOT EXISTS*/ `server_v5` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `server_v5`;

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `customer_first_name` varchar(255) NOT NULL,
  `customer_number` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE `PRODUCTS` (
  `products_id` int(11) NOT NULL AUTO_INCREMENT,
  `products_ean` varchar(128) DEFAULT NULL,
  `products_quantity` int(4) NOT NULL,
  `products_shippingtime` int(4) NOT NULL,
  `products_model` varchar(64) DEFAULT NULL,
  `group_permission_0` tinyint(1) NOT NULL,
  `group_permission_1` tinyint(1) NOT NULL,
  `group_permission_2` tinyint(1) NOT NULL,
  `group_permission_3` tinyint(1) NOT NULL,
  `group_permission_4` tinyint(1) NOT NULL,
  `products_sort` int(4) NOT NULL DEFAULT '0',
  `products_image` varchar(254) NOT NULL,
  `products_price` decimal(15,4) NOT NULL,
  `products_discount_allowed` decimal(4,2) NOT NULL,
  `products_date_added` datetime NOT NULL,
  `products_last_modified` datetime DEFAULT NULL,
  `products_date_available` datetime DEFAULT NULL,
  `products_weight` decimal(6,3) NOT NULL,
  `products_status` tinyint(1) NOT NULL,
  `products_tax_class_id` int(11) NOT NULL,
  `product_template` varchar(64) DEFAULT NULL,
  `options_template` varchar(64) DEFAULT NULL,
  `manufacturers_id` int(11) DEFAULT NULL,
  `products_manufacturers_model` varchar(64) DEFAULT NULL,
  `products_ordered` int(11) NOT NULL DEFAULT '0',
  `products_fsk18` int(1) NOT NULL DEFAULT '0',
  `products_vpe` int(11) NOT NULL,
  `products_vpe_status` int(1) NOT NULL DEFAULT '0',
  `products_vpe_value` decimal(15,4) NOT NULL,
  `products_startpage` int(1) NOT NULL DEFAULT '0',
  `products_startpage_sort` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`products_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
-- Dump completed on 2016-04-25 10:35:35

/*!40101 SET character_set_client = @saved_cs_client */;