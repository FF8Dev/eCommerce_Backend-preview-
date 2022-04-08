-- MySQL dump 10.13  Distrib 8.0.22, for macos10.15 (x86_64)
--
-- Host: localhost    Database: rentyourstuff
-- ------------------------------------------------------
-- Server version	8.0.22

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id_order` int NOT NULL AUTO_INCREMENT,
  `order_date` datetime NOT NULL,
  `total_days` int NOT NULL,
  `start_rent_date` date DEFAULT NULL,
  `end_rent_date` date DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `shipment_status` varchar(45) NOT NULL DEFAULT 'Pending',
  `last_update` datetime NOT NULL,
  `id_byuser` int NOT NULL,
  `id_tocustomer` int NOT NULL,
  PRIMARY KEY (`id_order`),
  KEY `fk_orders_users1_idx` (`id_byuser`),
  KEY `fk_orders_customers1_idx` (`id_tocustomer`),
  CONSTRAINT `fk_orders_customers1` FOREIGN KEY (`id_tocustomer`) REFERENCES `customers` (`id_user`),
  CONSTRAINT `fk_orders_users1` FOREIGN KEY (`id_byuser`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2021-11-24 17:08:16',2,'2021-11-24','2021-11-26',33,'Cancelled','2021-12-21 17:17:30',1,2),(2,'2021-12-21 12:21:11',1,'2021-12-21','2021-12-21',13,'Ended','2021-12-21 19:53:01',1,4),(3,'2021-12-21 17:48:34',2,'2021-12-22','2021-12-23',31.2,'Cancelled','2021-12-22 06:55:43',1,3),(4,'2021-12-21 18:31:32',4,'2021-12-21','2021-12-24',54.6,'In Rent','2021-12-22 06:58:24',1,4),(5,'2021-12-21 19:52:21',7,'2021-12-21','2021-12-27',74.5,'In Rent','2021-12-22 07:07:54',1,5),(6,'2021-12-22 06:17:14',5,'2021-12-27','2021-12-31',123.12,'Cancelled','2021-12-22 12:03:33',1,3),(7,'2021-12-22 06:31:22',1,'2021-12-22','2021-12-22',28.6,'Cancelled','2021-12-22 12:03:37',1,4),(8,'2021-12-22 12:05:31',3,'2021-12-22','2021-12-24',34.45,'Waiting','2021-12-22 12:05:54',1,4),(9,'2021-12-22 12:08:30',3,'2021-12-22','2021-12-24',11.38,'In Rent','2021-12-22 12:11:51',4,4),(10,'2021-12-22 14:49:05',2,'2021-12-23','2021-12-24',31.2,'Not Finished','2021-12-22 14:49:19',1,4);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-12-22 15:04:03
