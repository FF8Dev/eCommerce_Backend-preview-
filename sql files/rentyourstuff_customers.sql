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
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `id_user` int NOT NULL,
  `firstname` varchar(45) DEFAULT NULL,
  `lastname` varchar(45) DEFAULT NULL,
  `address_line` varchar(200) DEFAULT NULL,
  `postalcode` int(5) unsigned zerofill DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `email` varchar(45) NOT NULL,
  `telephone` int DEFAULT NULL,
  `discount` int DEFAULT NULL,
  `creation_date` datetime NOT NULL,
  `last_update` datetime NOT NULL,
  `id_byUser` int NOT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `id_user_UNIQUE` (`id_user`),
  KEY `fk_customer_user1_idx` (`id_byUser`),
  KEY `fk_customers_users1_idx` (`id_user`),
  CONSTRAINT `fk_customers_users1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
<<<<<<< HEAD
INSERT INTO `customers` VALUES (2,'','','',00000,'','default@email.com',0,0,'2021-11-23 15:54:08','2021-12-11 07:57:34',1),(3,'Ale','Gomez','C Ultimate Salà, 4 2,08172,'Sant Cugat del Vallès','agomezurrea@gmail.com',682381083,20,'2021-11-17 11:27:44','2021-11-17 11:27:44',1),(4,'Pippo','Listo','C La Santa 121',08188,'Sant Pedor','pippo@losabe.com',654555777,35,'2021-11-17 11:27:54','2021-12-22 12:05:50',1),(5,'Alocoifindo','Supremus','Carrer de la Juridisprudencia 88',08191,'Sant Cugat','alocoifindo@gmail.com',134567890,50,'2021-11-21 17:29:02','2021-12-20 18:49:13',5),(6,'Pippo','Sabe','C La Santa 121',08188,'Sant Pedor','pippoguapo@losabe.com',654783212,90,'2021-12-12 22:12:49','2021-12-19 13:31:56',1);
=======
INSERT INTO `customers` VALUES (2,'','','',00000,'','default@email.com',0,0,'2021-11-23 15:54:08','2021-12-11 07:57:34',1),(3,'Ale','Gomez','C Salvador Spriu 23, 2, 3',08172,'Sant Cugat del Vallès','agomezurrea@gmail.com',682381083,20,'2021-11-17 11:27:44','2021-11-17 11:27:44',1),(4,'Pippo','Listo','C La Santa 121',08188,'Sant Pedor','pippo@losabe.com',654555777,35,'2021-11-17 11:27:54','2021-12-22 12:05:50',1),(5,'Alocoifindo','Supremus','Carrer de la Juridisprudencia 88',08191,'Sant Cugat','alocoifindo@gmail.com',134567890,50,'2021-11-21 17:29:02','2021-12-20 18:49:13',5),(6,'Pippo','Sabe','C La Santa 121',08188,'Sant Pedor','pippoguapo@losabe.com',654783212,90,'2021-12-12 22:12:49','2021-12-19 13:31:56',1);
>>>>>>> eade6c9 (Add files via upload)
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
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
