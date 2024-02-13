/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE `accounts` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `bban` int DEFAULT NULL,
  `balance` double DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `created` date DEFAULT (curdate()),
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `senderAccount_id` int DEFAULT NULL,
  `receiverAccount_id` int DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `date` timestamp NULL DEFAULT (now()),
  PRIMARY KEY (`transaction_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `personal_number` int DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  `created` date DEFAULT (curdate()),
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `accounts` (`account_id`, `bban`, `balance`, `user_id`, `created`) VALUES
(1, 5555, 4100, 1, '2023-06-01');
INSERT INTO `accounts` (`account_id`, `bban`, `balance`, `user_id`, `created`) VALUES
(2, 666, 3800, 1, '2023-06-01');
INSERT INTO `accounts` (`account_id`, `bban`, `balance`, `user_id`, `created`) VALUES
(3, 8545, 26500, 2, '2023-06-01');
INSERT INTO `accounts` (`account_id`, `bban`, `balance`, `user_id`, `created`) VALUES
(4, 9423, 5000, 2, '2023-06-01'),
(5, 9999, 9000, 3, '2023-06-01'),
(6, 8999, 8000, 3, '2023-06-01'),
(7, 3444, 3200, 4, '2023-06-01'),
(8, 4333, 4200, 4, '2023-06-01'),
(9, 8787, 8300, 5, '2023-06-01'),
(14, 343, 343, 1, '2023-06-02'),
(15, 343, 343, 7, '2023-06-02'),
(22, 10, 9800, 1, '2023-06-02');

INSERT INTO `transactions` (`transaction_id`, `senderAccount_id`, `receiverAccount_id`, `amount`, `date`) VALUES
(1, 1, 3, 100, '2023-01-02 21:31:07');
INSERT INTO `transactions` (`transaction_id`, `senderAccount_id`, `receiverAccount_id`, `amount`, `date`) VALUES
(2, 22, 8, 200, '2023-05-02 21:32:30');
INSERT INTO `transactions` (`transaction_id`, `senderAccount_id`, `receiverAccount_id`, `amount`, `date`) VALUES
(3, 1, 3, 200, '2023-06-02 22:04:17');

INSERT INTO `users` (`user_id`, `name`, `password`, `personal_number`, `email`, `phone`, `address`, `created`) VALUES
(1, 'Maurice', '123', 920128, 'hello@yahoo.com', '0705289952', 'Ledebursgatan 5', '2023-06-01');
INSERT INTO `users` (`user_id`, `name`, `password`, `personal_number`, `email`, `phone`, `address`, `created`) VALUES
(2, 'Harald', '321', 891011, 'heraldo@yahoo.se', '0701234564', 'Silvergatan 3', '2023-06-01');
INSERT INTO `users` (`user_id`, `name`, `password`, `personal_number`, `email`, `phone`, `address`, `created`) VALUES
(3, 'Hampus', '123', 900210, 'showtime@gmail.com', '0725556245', 'Allerupsgatan 10', '2023-06-01');
INSERT INTO `users` (`user_id`, `name`, `password`, `personal_number`, `email`, `phone`, `address`, `created`) VALUES
(4, 'Gabriel', '223', 850202, 'gabbe@hotmail.com', '0701245365', 'Finlandsgatan 3', '2023-06-01'),
(5, 'Illmar', 'olf50', 501002, 'ille@gmail.com', '0734111212', 'Östergatan 1', '2023-06-01'),
(7, 'Karlsson', '123', 800101, 'pataket@yahoo.com', '0723435353', 'Silvergatan 5', '2023-06-02'),
(8, 'Filip', 'fille50', 700101, 'fillelup@yahoo.com', '0725412033', 'Värnhemsgatan 4', '2023-06-02'),
(9, 'yimmy', '123', 940523, 'yammer@gmail.com', '0705624520', 'Hullugatan 6', '2023-06-02'),
(10, 'Magnus', 'mange88', 950301, 'mange@gmail.com', '0703845472', 'Möllegatan 3', '2023-06-02');


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;