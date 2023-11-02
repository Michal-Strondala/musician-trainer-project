DROP SCHEMA IF EXISTS `musician-trainer-directory`;

CREATE SCHEMA `musician-trainer-directory`;

use `musician-trainer-directory`;

SET FOREIGN_KEY_CHECKS = 0;


CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `created` DATETIME,
  `logged` DATETIME,
  `password` char(68) DEFAULT NULL,
  `hash` char(68) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--
-- NOTE: The passwords are encrypted using BCrypt
--
-- A generation tool is avail at: http://www.luv2code.com/generate-bcrypt-password
-- https://www.bcryptcalculator.com/
--
-- Default passwords here are: fun123
--

INSERT INTO `user` (`email`,`password`)
VALUES 
('john@john.com','$2a$10$t90CmbfCiJ.Fxm5ckIFiDepXrVp/Q9Onvp4sU9Aro6xnEP1WAD7fi'),
('mary@mary.com','$2a$10$t90CmbfCiJ.Fxm5ckIFiDepXrVp/Q9Onvp4sU9Aro6xnEP1WAD7fi');

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `role`
--

INSERT INTO `role` (role_name)
VALUES 
('ROLE_USER'),('ROLE_ADMIN');

SET FOREIGN_KEY_CHECKS = 0;

--
-- Table structure for table `users_roles`
--

DROP TABLE IF EXISTS `users_roles`;

CREATE TABLE `users_roles` (
  `user_id` int NOT NULL,
  `role_id` bigint NOT NULL,
  
  PRIMARY KEY (`user_id`,`role_id`),
  
  KEY `FK_ROLE_idx` (`role_id`),
  
  CONSTRAINT `FK_USER_05` FOREIGN KEY (`user_id`) 
  REFERENCES `user` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION,
  
  CONSTRAINT `FK_ROLE` FOREIGN KEY (`role_id`) 
  REFERENCES `role` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

SET FOREIGN_KEY_CHECKS = 1;

--
-- Dumping data for table `users_roles`
--

INSERT INTO `users_roles` (user_id,role_id)
VALUES 
(1, 1),
(2, 1),
(2, 2)

