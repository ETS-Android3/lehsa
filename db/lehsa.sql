-- MySQL dump 10.13  Distrib 5.5.42 for osx10.6 (i386)
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
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES SQL_NOTES=0 */;

DROP DATABASE IF EXISTS lehsa;
CREATE DATABASE lehsa;
USE lehsa;

--
-- Table structure for table `faculty`
--

DROP TABLE IF EXISTS `faculty`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `faculty` (
  `id` int(11) NOT NULL AUTO_INCREMENT
  `fac_name` varchar(100) DEFAULT 'Unknown'
  `fac_email` varchar(50) DEFAULT NULL
  `fac_phone` varchar(20) DEFAULT NULL
  `fac_position` varchar(50) DEFAULT NULL
  `fac_research_link` varchar(150) DEFAULT NULL
  `fac_office` varchar(150) DEFAULT NULL
  `fac_feature_vectors` text NOT NULL
  UNIQUE KEY `fac_name` (`fac_name`)
  KEY `id` (`id``fac_name``fac_email``fac_phone``fac_position``fac_research_link``fac_office`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `faculty`
--

LOCK TABLES `faculty` WRITE;
/*!40000 ALTER TABLE `faculty` DISABLE KEYS */;
INSERT INTO `faculty` VALUES (1'Tom Cruise''''''Professor''''''-0.38941196 0.7066168 -0.45291072 0.12138486 0.5834402 0.24492161 0.2537372 0.30006874 -0.43040696 -0.28417575 -0.4233903 -0.14117144 -0.21048607 -0.14529659 0.033301104 0.19033657 0.41568428 0.20680495 -0.56911564 0.28442895 -0.38254738 0.2483248 -0.22653371 -0.5219019 -0.16898279 0.2464059 -0.36837795 -0.5780142 -0.15083155 0.3585689 1.1859144 0.36468711 -0.8098959 0.37931263 0.29535222 -0.51141113 0.048232432 0.43754596 -0.5457155 -0.18050873 -0.2957057 0.47010815 -0.409947 -0.2323969 -0.74883753 -1.0939765 0.2390801 -0.07428521 -0.0856794 -0.44911098 0.44546226 0.021831376 -0.2932341 0.40446502 0.27051392 -0.15877198 -0.34118947 -0.35549274 0.6210878 0.44638002 0.4712664 0.37714595 0.17085001 -0.15909547 -0.4021096 0.52624154 0.29386455 -0.026029577 -0.39946374 0.22988933 -0.2779236 0.56118673 -0.07780084 -0.054748107 0.4605646 -0.3584328 0.59370065 -0.39496422 0.2539135 -0.10467331 0.7536907 0.35583088 0.011999072 -0.075088546 0.69476587 -0.9794894 -0.6418271 0.081944555 0.22085243 -0.29811895 0.18842202 -0.046177734 -0.44272423 0.063696355 -0.15669227 0.6999441 0.116389886 0.3585264 0.59120035 -0.28450406 -0.40069267 -0.8853074 -0.4194697 4.818835E-4 -0.20091161 -0.05901664 1.0327684 -0.21814556 0.7015294 -0.31027788 -0.4462421 0.2820723 0.32791385 0.30871946 0.041641682 -0.5472234 -0.10580862 -0.3127195 0.63146406 0.42769554 -0.0698064 -0.39086083 -0.6553786 -0.095355235 0.049715724 -0.028204776 -0.69424784 0.022424094 0.11695603 0.52195066 -0.28021407 -0.23063287 0.40514997 -0.42581692 -0.8497832 0.12585223 0.4275563 0.47523224 0.12820445 -0.435099 -0.3249836 0.17696552 -0.4869405 0.4861731 0.44397372 0.57145804 -0.13511108 -0.22669227 0.5495469 0.40092564 -0.0040048603 -0.117102794 0.57465464 0.41309905 -0.74998724 0.37097228 -0.34638482 0.019067451 -0.8631903 0.02361779 0.03733697 -0.2407197 -0.05239184 -0.3552325 -0.019070929 0.152784 -0.18312857 -0.49411553 0.20715208 -0.024150984 -0.75491965 0.09406632 0.41595 0.3875638 0.14796372 -0.3695869 -0.3160899 -0.26664838 -0.05155976 -0.31282988 0.13213667 -0.18430765 -0.22968788 -0.0015801371 0.28353974 0.2977177 0.26318604 -0.21054277 1.1376723 -0.6124564 0.27922365 -0.66631216 -0.28711393 -0.35931534 0.045599546 -0.18506359 -0.28848085 0.29283917 0.3045701 0.69184524 -0.035836294 0.7368879 0.37248722 0.21755387 0.17219839 0.14575818 -0.13144764 0.1691225 0.9192646 -0.23183662 0.31102693 0.040657848 0.33206677 0.23961046 -0.49221382 -0.08243284 -0.47585526 0.5250328 -0.694471 -0.20994617 0.09789425 0.2316731 -1.2153887 -0.12637994 0.23345149 -0.20348224 -0.3134662 0.023333423 0.05313245 0.31916124 0.32517177 0.51078343 1.0945247 -0.43233028 -0.37469196 0.33891404 0.2623695 0.045524076 0.57794225 0.3820035 -0.25543284 -0.4782306 -0.46911922 0.21005158 -0.04598231 -0.12076813 -0.043328162 0.26812118 -0.3613268 0.31017536 -0.6435139 -0.05814855 -0.16622823 -0.06277444 0.7380023 0.1372454 0.077603996 0.2120928 -0.025016127 -0.925015 -0.112429686 -0.14697015 -0.0016649329 -0.593421 -0.17255454 0.23986086 -0.16565996 0.014203988 -0.26222268 0.21204987 0.4816766 -0.34261364 -0.36024344 0.34487402 0.73715585 -0.16106366 -0.82446826 -0.00861181 0.2546821 -0.5709412 0.60377884 0.33845654 -0.24041015 -0.2878073 0.049676992 0.40586647 -0.2077863 0.06575986 -0.3377677 -0.43549895 -0.1269046 0.42580047 -0.33359736 0.40445542 -0.39052868 0.31161612 0.73208666 0.062691584 -0.0031937743 -0.12865667 -0.061071146 -0.2320845 -0.033543676 -0.08122768 -0.28131565 -0.3806701 0.32549506 -0.3959312 -0.6210106 -0.28098977 -0.19893435 -0.22282152 -0.18907791 -0.29983556 0.048976842 0.25272885 -0.4639714 0.31133178 0.4421796 -0.42971295');
  /*!40000 ALTER TABLE `faculty` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_course`
--

DROP TABLE IF EXISTS `student_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `student_course` (
  `id` int(11) NOT NULL AUTO_INCREMENT
  `user_name` varchar(30) DEFAULT NULL
  `course_number` varchar(100) DEFAULT NULL
  KEY `id` (`id``user_name``course_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_course`
--

LOCK TABLES `student_course` WRITE;
/*!40000 ALTER TABLE `student_course` DISABLE KEYS */;
INSERT INTO `student_course` VALUES (1'user1''CSE471');
/*!40000 ALTER TABLE `student_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_list`
--

DROP TABLE IF EXISTS `course_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT
  `course_number` varchar(10) DEFAULT NULL
  `course_name` varchar(30) DEFAULT NULL
  `professor` varchar(50) DEFAULT NULL
  `days_offered` varchar(50) DEFAULT NULL
  `timing` varchar(20) DEFAULT NULL
  `ta_list` varchar(100) DEFAULT NULL
  KEY `id` (`id``course_number``course_name``professor``days_offered``timing``ta_list`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_list`
--

LOCK TABLES `course_list` WRITE;
/*!40000 ALTER TABLE `course_list` DISABLE KEYS */;
INSERT INTO `course_list` VALUES (1'CSE498''Big Data Analytics''Daniel Lopresti''MW''02.10 PM - 03.25 PM''');
INSERT INTO `course_list` VALUES (2'CSE471''Mobile Computing''Mooi Chu Chuah''MW''09.20 PM - 10.35 AM''Hannah Fabian Allen Wang');
INSERT INTO `course_list` VALUES (3'CSE398''Big Data Analytics''Daniel Lopresti''MW''02.10 PM - 03.25 PM''');
INSERT INTO `course_list` VALUES (4'CSE371''Mobile Computing''Mooi Chu Chuah''MW''09.20 AM - 10.35 AM''Hannah Fabian Allen Wang');
INSERT INTO `course_list` VALUES (5'CSE403''Advanced Operating System''Michael Spear''MW''12.20 PM - 01.35 PM''Jack Jack John John');
INSERT INTO `course_list` VALUES (6'CSE411''Advanced Programming Techniques''Ahmed Hassan''TR''08.00 AM - 09.20 AM''');
INSERT INTO `course_list` VALUES (7'CSE331''User Interface Design and Techniques''Eric Baumer''TR''03.00 PM - 04.20 PM''');
/*!40000 ALTER TABLE `course_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_assignment`
--

DROP TABLE IF EXISTS `assignment_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assignment_tracker` (
  `id` int(11) NOT NULL AUTO_INCREMENT
  `user_name` varchar(30) DEFAULT NULL
  `course_number` varchar(30) DEFAULT NULL
  `due_date` varchar(100) DEFAULT NULL
  `due_time` varchar(100) DEFAULT NULL
  `assignment` text DEFAULT NULL
  KEY `id` (`id``user_name` `course_number``due_date``due_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment_tracker`
--

LOCK TABLES `assignment_tracker` WRITE;
/*!40000 ALTER TABLE `assignment_tracker` DISABLE KEYS */;
INSERT INTO `assignment_tracker` VALUES (1'user1' 'CSE471' '' '' '');
/*!40000 ALTER TABLE `assignment_tracker` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Table structure for table `student_details`
--

DROP TABLE IF EXISTS `student_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `student_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT
  `first_name` varchar(50) DEFAULT 'Unknown'
  `last_name` varchar(50) DEFAULT 'Unknown'
  `user_name` varchar(30) DEFAULT NULL
  `password` varchar(30) DEFAULT NULL
  PRIMARY KEY (`id`)
  UNIQUE KEY `user_name` (`user_name`)
  KEY `id` (`id``first_name``last_name``user_name``password`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_details`
--

LOCK TABLES `student_details` WRITE;
/*!40000 ALTER TABLE `student_details` DISABLE KEYS */;
INSERT INTO `student_details` VALUES (1'Sam''Red''user1''pass');
  /*!40000 ALTER TABLE `student_details` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;


--
-- Table structure for table `chatbot`
--

CREATE TABLE `chatbot` (
  `id` int(100) NOT NULL
  `keyword` varchar(50) NOT NULL
  `answer` varchar(2000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Dumping data for table `chatbot`
--


INSERT INTO `chatbot` (`id` `keyword` `answer`) VALUES
(1 "exam" "Ah Exams!!. Even though exams seem so important your entire future doesn't depend on them. Don't give a test the power to define you!.\nIt might seem overwhelming now but you will come out with flying colors. For assistance you should join student study groups. Just hang in there!")
(3 "relationship" "You may want to talk with your counselor about your feelings.")
(6 "poor grade" "It is scary to see a poor grade. Talk to your professor about how you could make up for it. Meanwhile stay calm and prepare well for your next assessment.")
(8 "accommodation" "It can seem complicated to find a place you can call home. You should follow the college housing page on Facebook. They have pretty good listings there.")
(10 "sickness" "You have to contact the college health and wellness center if you don't feel good. They can help you feel better")
(11 "home sick" "Don't worry you are not alone. Many students will be feeling the same. Stay in constant touch with those you have left behind. Make new friends and hang out with them to keep yourself distracted. You'll be fine!!.")
(13 "expectations" "It is hard to fulfill every expectation. Just take one day at a time and hang in there. You'll be fine!.")
(15 "distraction" "It is natural to get distracted. But it is important to stay focused at times. Try some meditation to improve your concentration. But if you feel like you need medical assistance contact the health and wellness center.")
(16 "depression" "Reduce substances that hurt you. Eat a balanced meal. You can meet your therapists who can assist you in feeling better.")
(17 "study" "You are not alone. Many of us feel that way too as if we only learn the material for exams. However there are professors who teach well and use real life examples to help you remember what you learn.")
(18 "college hype" "You are not alone. Many of us feel that way too as if we only learn the material for exams. However there are professors who teach well and use real life examples to help you remember what you learn.")
(19 "major" "You have the right perspective. All majors are respectable and students should pick one where they enjoy them as long as they don't incur much student debts knowing for sure that the jobs they will get will not be paid well.")
(20 "semester" "You have a funny way of describing your semester. However many will say they experience similar things like what you describe here. Well done.")






-- Indexes for dumped tables
--

--
-- Indexes for table `chatbot`
--
ALTER TABLE `chatbot`
  ADD PRIMARY KEY (`id`);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-01-26 10:14:58
