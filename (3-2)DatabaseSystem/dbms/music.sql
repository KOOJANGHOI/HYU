-- MySQL dump 10.16  Distrib 10.2.9-MariaDB, for osx10.12 (x86_64)
--
-- Host: localhost    Database: MUSIC
-- ------------------------------------------------------
-- Server version	10.2.9-MariaDB

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

--
-- Table structure for table `ADDSONG`
--

DROP TABLE IF EXISTS `ADDSONG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ADDSONG` (
  `USID` int(11) NOT NULL,
  `PLNAME` varchar(45) NOT NULL,
  `SONID` int(11) NOT NULL,
  PRIMARY KEY (`USID`,`PLNAME`,`SONID`),
  KEY `ADDSOFK_idx` (`SONID`),
  CONSTRAINT `ADD_PLA_FK` FOREIGN KEY (`USID`, `PLNAME`) REFERENCES `PLAYLIST` (`USRID`, `PNAME`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `ADD_SON_FK` FOREIGN KEY (`SONID`) REFERENCES `SONG` (`SID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ADDSONG`
--

LOCK TABLES `ADDSONG` WRITE;
/*!40000 ALTER TABLE `ADDSONG` DISABLE KEYS */;
INSERT INTO `ADDSONG` VALUES (19900000,'브론즈1',20000001),(19900000,'브론즈1',20000010),(19900000,'브론즈1',20000011),(19900002,'브론즈2',20000002),(19900003,'브론즈1',20000001),(19900004,'마스터',20000010),(19900006,'플레1',20000014),(19900008,'다이아1',20000006),(19900008,'챌린저',20000008);
/*!40000 ALTER TABLE `ADDSONG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ALBUM`
--

DROP TABLE IF EXISTS `ALBUM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ALBUM` (
  `ALID` int(11) NOT NULL,
  `ALCOUNT` int(11) NOT NULL,
  `ALNAME` varchar(100) NOT NULL,
  `ALPRICE` int(11) NOT NULL,
  `ALDATE` datetime NOT NULL,
  `GENRE` varchar(60) NOT NULL,
  `MGRID` int(11) NOT NULL,
  `ARTID` int(11) NOT NULL,
  PRIMARY KEY (`ALID`),
  KEY `ALMGRFK_idx` (`MGRID`),
  KEY `ALARTFK_idx` (`ARTID`),
  CONSTRAINT `ALB_ART_FK` FOREIGN KEY (`ARTID`) REFERENCES `ARTIST` (`ARID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `ALB_MGR_FK` FOREIGN KEY (`MGRID`) REFERENCES `MANAGER` (`MID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ALBUM`
--

LOCK TABLES `ALBUM` WRITE;
/*!40000 ALTER TABLE `ALBUM` DISABLE KEYS */;
INSERT INTO `ALBUM` VALUES (19700000,5,'Moonlight',20000,'2017-07-10 00:00:00','Ballard',19600000,19800000),(19700001,12,'Brother.Act',40000,'2017-10-16 00:00:00','Ballard',19600001,19800001),(19700002,13,'twicetagram',15000,'2017-10-30 00:00:00','Dance',19600002,19800002),(19700003,1,'SUNMU SPECIAL',20000,'2017-08-22 00:00:00','Dance',19600003,19800003),(19700004,11,'WE VE DONE',70000,'2017-10-23 00:00:00','Rap&Hiphop',19600004,19800004),(19700005,11,'WE Are',10000,'2017-09-04 00:00:00','Rap&Hiphop',19600005,19800005),(19700006,16,'Deluxe',30000,'2017-03-04 00:00:00','Pop',19600006,19800006);
/*!40000 ALTER TABLE `ALBUM` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ARTIST`
--

DROP TABLE IF EXISTS `ARTIST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ARTIST` (
  `ARID` int(11) NOT NULL,
  `FNAME` varchar(15) NOT NULL,
  `MINIT` varchar(5) DEFAULT NULL,
  `LNAME` varchar(15) DEFAULT NULL,
  `ARDATE` datetime NOT NULL,
  PRIMARY KEY (`ARID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ARTIST`
--

LOCK TABLES `ARTIST` WRITE;
/*!40000 ALTER TABLE `ARTIST` DISABLE KEYS */;
INSERT INTO `ARTIST` VALUES (19800000,'멜로망스','NULL','NULL','2016-02-15 00:00:00'),(19800001,'비투비',NULL,NULL,'2014-11-11 00:00:00'),(19800002,'twice',NULL,NULL,'2015-12-20 00:00:00'),(19800003,'선미',NULL,NULL,'2011-02-28 00:00:00'),(19800004,'에픽하이',NULL,NULL,'2008-07-01 00:00:00'),(19800005,'우원재',NULL,NULL,'2017-03-19 00:00:00'),(19800006,'Ed',NULL,'sheeran','2010-11-18 00:00:00');
/*!40000 ALTER TABLE `ARTIST` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MANAGER`
--

DROP TABLE IF EXISTS `MANAGER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MANAGER` (
  `MID` int(11) NOT NULL,
  `FNAME` varchar(15) NOT NULL,
  `MINIT` varchar(5) DEFAULT NULL,
  `LNAME` varchar(15) NOT NULL,
  `MDATE` datetime NOT NULL,
  PRIMARY KEY (`MID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MANAGER`
--

LOCK TABLES `MANAGER` WRITE;
/*!40000 ALTER TABLE `MANAGER` DISABLE KEYS */;
INSERT INTO `MANAGER` VALUES (19600000,'jacob','a ','martin','2007-03-11 00:00:00'),(19600001,'Ethan','b','john','2010-01-19 00:00:00'),(19600002,'Michael','c','tyler','2006-12-12 00:00:00'),(19600003,'Alexander','d ','Dylan','2007-04-15 00:00:00'),(19600004,'William','e','Jonathan','2008-06-28 00:00:00'),(19600005,'Joshua','f','Caleb','2012-04-01 00:00:00'),(19600006,'Daniel','g','Nicholas','2016-10-30 00:00:00');
/*!40000 ALTER TABLE `MANAGER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PLAYLIST`
--

DROP TABLE IF EXISTS `PLAYLIST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PLAYLIST` (
  `USRID` int(11) NOT NULL,
  `PNAME` varchar(45) NOT NULL,
  `PCOUNT` int(11) DEFAULT NULL,
  PRIMARY KEY (`USRID`,`PNAME`),
  CONSTRAINT `PLA_USR_FK` FOREIGN KEY (`USRID`) REFERENCES `USER` (`UID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PLAYLIST`
--

LOCK TABLES `PLAYLIST` WRITE;
/*!40000 ALTER TABLE `PLAYLIST` DISABLE KEYS */;
INSERT INTO `PLAYLIST` VALUES (19900000,'골드1',0),(19900000,'브론즈1',3),(19900000,'브론즈2',0),(19900002,'브론즈2',1),(19900003,'브론즈1',1),(19900004,'마스터',1),(19900006,'플레1',2),(19900008,'다이아1',1),(19900008,'챌린저',1);
/*!40000 ALTER TABLE `PLAYLIST` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RECOMMANDS`
--

DROP TABLE IF EXISTS `RECOMMANDS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RECOMMANDS` (
  `USERID` int(11) NOT NULL,
  `SOID` int(11) NOT NULL,
  PRIMARY KEY (`USERID`,`SOID`),
  KEY `REC_SON_FK_idx` (`SOID`),
  CONSTRAINT `REC_SON_FK` FOREIGN KEY (`SOID`) REFERENCES `SONG` (`SID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `REC_USR_FK` FOREIGN KEY (`USERID`) REFERENCES `USER` (`UID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RECOMMANDS`
--

LOCK TABLES `RECOMMANDS` WRITE;
/*!40000 ALTER TABLE `RECOMMANDS` DISABLE KEYS */;
INSERT INTO `RECOMMANDS` VALUES (19900000,20000010),(19900003,20000013),(19900007,20000005);
/*!40000 ALTER TABLE `RECOMMANDS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SONG`
--

DROP TABLE IF EXISTS `SONG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SONG` (
  `SID` int(11) NOT NULL,
  `SNAME` varchar(45) NOT NULL,
  `SCOUNT` int(11) DEFAULT NULL,
  `SRANK` int(11) DEFAULT NULL,
  `SURL` varchar(45) NOT NULL,
  `ALBID` int(11) NOT NULL,
  PRIMARY KEY (`SID`),
  KEY `SONALBFK_idx` (`ALBID`),
  CONSTRAINT `SON_ALB_FK` FOREIGN KEY (`ALBID`) REFERENCES `ALBUM` (`ALID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SONG`
--

LOCK TABLES `SONG` WRITE;
/*!40000 ALTER TABLE `SONG` DISABLE KEYS */;
INSERT INTO `SONG` VALUES (20000001,'SLEEPY',2109,19,'www.melon.com',19700000),(20000002,'Moonlight',281,18,'www.melon.com',19700000),(20000003,'먼지',12,17,'www.melon.com',19700000),(20000004,'자장가',551,16,'www.melon.com',19700000),(20000005,'My Lady',999,15,'www.melon.com',19700001),(20000006,'그리워하다',18592,14,'www.bugs.com',19700001),(20000007,'새빨간 거짓말',391,13,'www.gini.com',19700001),(20000008,'LIKEY',25620,12,'www.bugs.com',19700002),(20000009,'거북이',1904,11,'www.bugs.com',19700002),(20000010,'MISSING U',5812,10,'www.bugs.com',19700002),(20000011,'가시나',14464,9,'www.gini.com',19700003),(20000012,'노땡큐',6812,8,'www.bugs.com',19700004),(20000013,'빈차',841,7,'www.bugs.com',19700004),(20000014,'연애소설',58819,6,'www.gini.com',19700004),(20000016,'시차',20137,4,'www.bugs.com',19700005),(20000017,'Dive',4567,3,'www.bugs.com',19700006),(20000018,'Shape of you',8201,2,'www.gini.com',19700006),(20000019,'Eraser',3000,1,'www.gini.com',19700006);
/*!40000 ALTER TABLE `SONG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TEST2`
--

DROP TABLE IF EXISTS `TEST2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TEST2` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(15) NOT NULL,
  `AGE` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `TEST2FK` (`AGE`),
  CONSTRAINT `TEST2FK` FOREIGN KEY (`AGE`) REFERENCES `SONG` (`SID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TEST2`
--

LOCK TABLES `TEST2` WRITE;
/*!40000 ALTER TABLE `TEST2` DISABLE KEYS */;
/*!40000 ALTER TABLE `TEST2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `USER`
--

DROP TABLE IF EXISTS `USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USER` (
  `UID` int(11) NOT NULL,
  `FNAME` varchar(15) NOT NULL,
  `MINIT` varchar(5) DEFAULT NULL,
  `LNAME` varchar(15) DEFAULT NULL,
  `SSN` int(20) NOT NULL,
  `EMAIL` varchar(45) NOT NULL,
  `SEX` varchar(15) NOT NULL,
  PRIMARY KEY (`UID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `USER`
--

LOCK TABLES `USER` WRITE;
/*!40000 ALTER TABLE `USER` DISABLE KEYS */;
INSERT INTO `USER` VALUES (19900000,'장회',NULL,'구',940228,'kjanghoi@gmail.com','male'),(19900001,'태호',NULL,'김',920111,'taehoo@gmail.com','male'),(19900002,'광남',NULL,'장',930222,'gangnam@naver.com','male'),(19900003,'득춘',NULL,'장',910311,'dukchoon@hanmail.net','male'),(19900004,'지금',NULL,'이',900422,'rightnow@gmail.com','female'),(19900005,'민정',NULL,'반',890511,'minjung@gmail.com','female'),(19900006,'수아',NULL,'홍',880622,'sua@naver.com','female'),(19900007,'성언',NULL,'임',870711,'seongun@paran.com','female'),(19900008,'태경',NULL,'김',860822,'izoi300@naver.com','female');
/*!40000 ALTER TABLE `USER` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-12-04 21:20:54
