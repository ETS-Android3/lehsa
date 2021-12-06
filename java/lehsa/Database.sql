-- MySQL dump 10.13  Distrib 5.5.42, for osx10.6 (i386)
--
-- Host: localhost    Database: 
-- ------------------------------------------------------
-- Server version	5.5.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP DATABASE IF EXISTS hw1_db;
CREATE DATABASE hw1_db;
USE hw1_db;

--
-- Table structure for table `tb_Location`
--

DROP TABLE IF EXISTS `tb_Location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_Location` (
  `mac` varchar(30) DEFAULT NULL,
  `building` varchar(30) DEFAULT NULL,
  `floor` varchar(30) DEFAULT NULL,
  UNIQUE KEY `mac` (`mac`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_Location`
--

LOCK TABLES `tb_Location` WRITE;
/*!40000 ALTER TABLE `tb_Location` DISABLE KEYS */;
INSERT INTO `tb_Location` VALUES ('c0:c1:c0:65:e3:b2','Packard Lab','1st Floor'),('00:24:6c:12:6e:20','Packard Lab','1st Floor'),('00:24:6c:12:6e:22','Packard Lab','1st Floor'),('00:24:6c:13:6f:40','Packard Lab','1st Floor'),('04:bd:88:64:13:40','Packard Lab','1st Floor'),('04:bd:88:64:13:41','Packard Lab','1st Floor'),('d8:c7:c8:9a:33:30','Packard Lab','1st Floor'),('d8:c7:c8:9a:33:31','Packard Lab','1st Floor'),('04:bd:88:64:17:81','Packard Lab','1th Floor'),('00:24:6c:13:6b:40','Packard Lab','2nd Floor'),('00:24:6c:13:6b:41','Packard Lab','2nd Floor'),('00:24:6c:13:6d:a0','Packard Lab','2nd Floor'),('00:24:6c:13:6d:a1','Packard Lab','2nd Floor'),('00:24:6c:13:85:d1','Packard Lab','2nd Floor'),('04:bd:88:64:0c:40','Packard Lab','2nd Floor'),('04:bd:88:64:0c:41','Packard Lab','2nd Floor'),('04:bd:88:64:17:a1','Packard Lab','2nd Floor'),('ac:a3:1e:e2:ca:41','Packard Lab','2nd Floor'),('c0:38:96:18:32:db','Packard Lab','3rd Floor'),('04:bd:88:64:19:80','Packard Lab','3rd Floor'),('04:bd:88:64:19:a0','Packard Lab','3rd Floor'),('04:bd:88:64:19:a1','Packard Lab','3rd Floor'),('20:aa:4b:15:58:6c','Packard Lab','3th Floor'),('04:bd:88:64:16:20','Packard Lab','3th Floor'),('04:bd:88:64:16:21','Packard Lab','3th Floor'),('c0:c1:c0:a5:4b:20','Packard Lab','4th Floor'),('00:1d:7e:e7:7f:d3','Packard Lab','4th Floor'),('00:0b:86:78:18:f1','Packard Lab','4th Floor'),('04:bd:88:64:17:60','Packard Lab','4th Floor'),('04:bd:88:64:17:61','Packard Lab','4th Floor'),('9c:1c:12:c2:f1:e0','Packard Lab','4th Floor'),('9c:1c:12:c2:f1:e1','Packard Lab','4th Floor'),('00:23:69:bd:03:75','Packard Lab','5th Floor'),('58:6d:8f:b1:19:be','Packard Lab','5th Floor'),('00:0b:86:7d:f0:80','Packard Lab','5th Floor'),('00:0b:86:7d:f0:81','Packard Lab','5th Floor'),('04:bd:88:64:16:01','Packard Lab','5th Floor'),('04:bd:88:64:16:40','Packard Lab','5th Floor'),('04:bd:88:64:16:41','Packard Lab','5th Floor'),('d8:c7:c8:9b:07:21','Packard Lab','5th Floor'),('d8:c7:c8:9b:07:20','Packard Lab','5th Floor'),('04:bd:88:64:16:00','Packard Lab','5th Floor'),('c8:b3:73:29:73:7e','Packard Lab','1st Floor');
/*!40000 ALTER TABLE `tb_Location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_News`
--

DROP TABLE IF EXISTS `tb_News`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_News` (
  `URL` varchar(100) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  UNIQUE KEY `URL` (`URL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_News`
--

LOCK TABLES `tb_News` WRITE;
/*!40000 ALTER TABLE `tb_News` DISABLE KEYS */;
INSERT INTO `tb_News` VALUES ('https://cas.cas2.lehigh.edu/content/future-students','Sciences'),('http://www1.lehigh.edu/news/racism-and-%E2%80%9Cutility-belief%E2%80%9D','Sciences'),('http://www1.lehigh.edu/news/scholars-join-first-uk-black-lives-matter-conference','Sciences'),('http://cbe.lehigh.edu/economics/undergraduate-programs/economics-major','Business'),('http://cbe.lehigh.edu/marketing/undergraduate-major','Business'),('http://www.lehigh.edu/engineering/academics/undergraduate/majors/index.html','Computers'),('http://www.lehigh.edu/engineering/academics/undergraduate/majors/bioeng.html','Computers'),('http://www.lehigh.edu/engineering/academics/undergraduate/majors/mechanicaleng.html','Education'),('http://coe.lehigh.edu/academics/degrees','Education'),('http://coe.lehigh.edu/academics/degrees/2','Education');
/*!40000 ALTER TABLE `tb_News` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_Users`
--

DROP TABLE IF EXISTS `tb_Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_Users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(30) DEFAULT NULL,
  `password` varchar(30) DEFAULT NULL,
  `device_mac` varchar(30) DEFAULT NULL,
  `connected_mac` varchar(30) DEFAULT NULL,
  `around_wifi_list` longtext,
  `full_name` varchar(30) DEFAULT 'Unknown',
  `interest` varchar(100) DEFAULT 'Sciences(Default)',
  `update_frequency` varchar(50) DEFAULT '10',
  `online` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name` (`user_name`),
  KEY `id` (`id`,`user_name`,`password`,`device_mac`,`connected_mac`,`full_name`,`interest`,`update_frequency`,`online`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_Users`
--

LOCK TABLES `tb_Users` WRITE;
/*!40000 ALTER TABLE `tb_Users` DISABLE KEYS */;
INSERT INTO `tb_Users` VALUES (1,'xincoder','qwe','50:46:5d:7b:10:e9','c8:b3:73:29:73:7e','c8:b3:73:29:73:7e_b4:75:0e:22:ef:62_9c:a9:e4:9e:98:c3_a0:63:91:9a:3a:99_08:86:3b:29:b3:06_','Xin Li','Business_Computers','10','2016-01-14 01:47:41'),(3,'xyz','qwe','50:46:5d:7b:10:e9','04:bd:88:64:16:00','00:23:69:bd:03:75_58:6d:8f:b1:19:be_04:bd:88:64:16:01_04:bd:88:64:16:00_00:1d:7e:e7:7f:d3_','Unknown','Sciences(Default)','10','2016-01-13 22:15:57'),(6,'qqq','qwe','50:46:5d:7b:10:e9','d8:c7:c8:9b:07:20','00:23:69:bd:03:75_58:6d:8f:b1:19:be_d8:c7:c8:9b:07:20_d8:c7:c8:9b:07:21_04:bd:88:64:16:01_','Unknown','Sciences(Default)','10','2016-01-13 23:16:04'),(7,'abc','qwe','50:46:5d:7b:10:e9','d8:c7:c8:9b:07:20','58:6d:8f:b1:19:be_00:23:69:bd:03:75_00:1d:7e:e7:7f:d3_d8:c7:c8:9b:07:20_d8:c7:c8:9b:07:21_','Li li','Sciences(Default)','10','2016-01-13 23:37:34');
/*!40000 ALTER TABLE `tb_Users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-01-26 10:14:58
