-- MySQL dump 10.13  Distrib 9.2.0, for macos14.7 (arm64)
--
-- Host: localhost    Database: Motos
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `usuario_id` int NOT NULL,
  `comment_text` varchar(500) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (1,3,39,'Este es un comentario de ejemplo.','2024-11-04 19:02:00'),(2,3,39,'Que genial esta funcion,miremos si muestra lo que en verdad deberia mostrar','2024-11-04 23:38:57'),(3,9,41,'Que genial ver esta moto tan cuidada!','2024-11-05 08:21:09'),(4,9,41,'Comentario de prueba!','2024-11-05 08:24:37'),(5,3,44,'Hola Mundo!','2024-11-05 08:58:42'),(6,3,47,'Hola Planeta Tierra!!jajaja😁','2024-11-05 08:59:21'),(7,4,44,'Por qué nadie comenta nada hacerca de la ducatti Panigale?','2024-11-05 11:04:46'),(8,9,44,'Yamaha R6r el sueño de cualquiera','2024-11-05 11:05:33'),(9,3,44,'Nuevo comment','2024-11-06 00:27:03'),(10,3,44,'Mensajes prueba','2024-11-06 00:32:27'),(11,4,44,'Mejor Ducati Panigale V4','2024-11-06 00:33:22'),(12,18,48,'buena moto','2024-11-06 00:49:37');
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `etiquetas`
--

DROP TABLE IF EXISTS `etiquetas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `etiquetas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre_etiqueta` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `etiquetas`
--

LOCK TABLES `etiquetas` WRITE;
/*!40000 ALTER TABLE `etiquetas` DISABLE KEYS */;
INSERT INTO `etiquetas` VALUES (1,'Deportiva'),(2,'Clásica'),(3,'Touring'),(4,'Enduro'),(5,'Scooter'),(6,'Custom'),(7,'Eléctrica'),(8,'Aventura'),(9,'Naked'),(10,'Retro'),(11,'Urbana'),(12,'Clásica Deportiva');
/*!40000 ALTER TABLE `etiquetas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_etiquetas`
--

DROP TABLE IF EXISTS `post_etiquetas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_etiquetas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `etiqueta_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_post_etiqueta` (`post_id`,`etiqueta_id`),
  KEY `etiqueta_id` (`etiqueta_id`),
  CONSTRAINT `post_etiquetas_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `post_etiquetas_ibfk_2` FOREIGN KEY (`etiqueta_id`) REFERENCES `etiquetas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=306 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_etiquetas`
--

LOCK TABLES `post_etiquetas` WRITE;
/*!40000 ALTER TABLE `post_etiquetas` DISABLE KEYS */;
INSERT INTO `post_etiquetas` VALUES (305,3,1),(286,11,1),(287,11,2),(303,15,1),(304,15,10);
/*!40000 ALTER TABLE `post_etiquetas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `image` varchar(255) NOT NULL,
  `user_id` int NOT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `likes` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (3,'BMWS1000RR','Moto de 1000 cc','/uploads/motobmw.jpg',44,'2024-11-02 15:23:22',0),(4,'DUCATI-PANIGALE','Moto de 899 cc','/uploads/DUCATI.jpg',44,'2024-11-02 16:37:39',0),(8,'Kawasaki','la mas rapida','/uploads/1730574631650-748093255.jpg',44,'2024-11-02 19:10:31',0),(9,'Yamaha r6r','Viejita pero es mi sueno','/uploads/1730590495533-659912441.jpg',44,'2024-11-02 23:34:55',0),(10,'Ducati StrigthFither','la cara de demonia','/uploads/1730594502216-231663903.jpg',44,'2024-11-03 00:41:42',0),(11,'XT660','La moto de las raticas','/uploads/1730741469511-669108471.jpg',44,'2024-11-04 17:31:09',0),(15,'Kawasaki Ninja','La mejor marca de Colombia en motos','/uploads/1730785176660-434259275.jpg',44,'2024-11-05 05:39:36',0),(16,'Pulsar NS200','La más aletosa','/uploads/1730786889596-590967785.jpg',44,'2024-11-05 06:08:09',0),(17,'Apache 200','Mi guerrera','/uploads/1730789324964-282139622.jpg',44,'2024-11-05 06:48:45',0),(18,'KTM Duke 200','La moto de mis suenos','/uploads/1730791973964-287129396.jpg',44,'2024-11-05 07:32:53',0);
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre_usuario` varchar(255) NOT NULL,
  `correo` varchar(255) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `edad` int NOT NULL,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (39,'Juan Perez','juan.perez@example.com','$2b$10$oKUb5ETZXkMGAZ0kmsyZvugAtx6J0KCx.tpbyvrLWPdq6v3.n9K9S',25,'2024-09-28 10:00:19'),(40,'Maria Gomez','maria.gomez@example.com','$2b$10$PYEOlcIUBGJhUBnJqbU59.hcFKQ/mnsY31TzfOgiB1Fq2EQuhpe86',30,'2024-09-28 10:00:36'),(41,'Carlos Martinez','carlos.martinez@example.com','$2b$10$jXps6oKJXzhuJ1UWtllPbOzZYZFLWT/H70MPmTYRoP7Kldp4rcVIi',28,'2024-09-28 10:00:49'),(42,'Lucia Fernandez','lucia.fernandez@example.com','$2b$10$RVx1KrJ7Ypwv5BfkZruIre95Sc29acISAj2B/WBUl8B5TPjscQ87e',22,'2024-09-28 10:00:56'),(43,'Javier Lopez','javier.lopez@example.com','$2b$10$f9ww001vGRbYmRHY6MOrjOoqyXi25Cq0jTVWQ.gKe4jFMX5q9YcFS',27,'2024-09-28 10:01:04'),(44,'Juan Pineda','juanpp282@gmail.com','$2b$10$Ej2SDtGeidbXOV3r7K1iLu5hkC900vBnxQ6trUoTVU4oreSj1k0hO',25,'2024-09-28 10:06:03'),(46,'Juan Pineda','juanpa.pinedama@ecci.edu.co','$2b$10$gEAI77JXRUYzM.38MmpfE.XmEuJpBUBa3YhZzJ8uCspxDjLqKOhdS',25,'2024-10-23 19:07:09'),(47,'Daniel Felipe Talero Pineda','dany2@gmail.com','$2b$10$arBw1t3sqx2LOld1Z9an7uB.Umrh8qGxYrL8ig.CurtrGyvELA7nG',19,'2024-11-04 17:59:32'),(48,'Jaime avila','jaimejaas@gmail.com','$2b$10$5JQcAobQFo4Pd5EumzR54uUFkxd7mOFTuS4WQ.rnbev6OLnQ5lChK',45,'2024-11-06 00:47:12'),(49,'Andres Hernandez','andrured32@gmail.com','$2b$10$nynTK7jctBkHUzXJvrsRw.4uDvrK7uMoy2ohYQaJZuAy7b44cPLX6',20,'2025-03-08 18:03:51');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-10  0:35:10
