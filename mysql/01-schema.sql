CREATE DATABASE IF NOT EXISTS `petshop`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'petshop_user'@'%'
  IDENTIFIED BY 't3stMet4waY';

GRANT ALL PRIVILEGES ON `petshop`.* TO 'petshop_user'@'%';
FLUSH PRIVILEGES;

USE `petshop`;