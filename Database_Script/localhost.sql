-- phpMyAdmin SQL Dump
-- version 4.4.9
-- http://www.phpmyadmin.net
--
-- Host: localhost:3306
-- Generation Time: Sep 01, 2015 at 10:44 AM
-- Server version: 5.1.73-community
-- PHP Version: 5.5.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `laundry`
--

-- --------------------------------------------------------

--
-- Table structure for table `about_us`
--

CREATE TABLE IF NOT EXISTS `about_us` (
  `id` bigint(18) NOT NULL COMMENT 'auto id',
  `about` varchar(1000) NOT NULL COMMENT 'about content',
  `link_1` varchar(500) NOT NULL,
  `link_2` varchar(500) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `about_us`
--

INSERT INTO `about_us` (`id`, `about`, `link_1`, `link_2`) VALUES
(1, 'about us updated<br />\n<br />\nand also changed all the things<br />\n<br />\nusing this app you can get help in problem while your vehicle was break down.', 'http://www.hotmail.com', 'http://www.google.co.in');

-- --------------------------------------------------------

--
-- Table structure for table `admin_user`
--

CREATE TABLE IF NOT EXISTS `admin_user` (
  `userId` bigint(18) NOT NULL,
  `email` varchar(45) NOT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `phoneNumber` varchar(45) DEFAULT NULL,
  `registrationTime` datetime DEFAULT NULL,
  `storeId` bigint(18) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `admin_user`
--

INSERT INTO `admin_user` (`userId`, `email`, `firstName`, `lastName`, `password`, `phoneNumber`, `registrationTime`, `storeId`) VALUES
(1, 'a@b.c', 'A', 'B', 'changeit', '1234567890', '2014-12-31 12:00:00', 2);

-- --------------------------------------------------------

--
-- Table structure for table `category_master`
--

CREATE TABLE IF NOT EXISTS `category_master` (
  `categoryId` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `defaultRate` decimal(10,2) NOT NULL,
  `pluralName` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `category_master`
--

INSERT INTO `category_master` (`categoryId`, `name`, `description`, `defaultRate`, `pluralName`) VALUES
(1, 'Hoodie', 'Hoodies', '5.00', 'Hoodies'),
(2, 'T-Shirt', 'T-Shirts', '5.00', 'T-Shirts'),
(3, 'Dress', 'Dress', '7.00', 'Dresses'),
(4, 'Cotton Trouser', 'Trousers', '5.00', 'Cotton Trousers'),
(5, 'Skirt', 'Skirt', '3.00', 'Skirts'),
(6, 'Suit', 'Suit', '15.00', 'Suits');

-- --------------------------------------------------------

--
-- Table structure for table `category_society_price`
--

CREATE TABLE IF NOT EXISTS `category_society_price` (
  `categoryId` int(11) NOT NULL,
  `societyId` int(11) NOT NULL,
  `price` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `category_society_price`
--

INSERT INTO `category_society_price` (`categoryId`, `societyId`, `price`) VALUES
(1, 1, '8.00'),
(1, 2, '7.00'),
(1, 3, '6.00'),
(1, 4, '5.00'),
(2, 1, '9.00'),
(2, 2, '8.00'),
(2, 3, '7.00'),
(2, 4, '6.00'),
(3, 1, '10.00'),
(3, 2, '9.00'),
(3, 3, '8.00'),
(3, 4, '7.00'),
(4, 1, '11.00'),
(4, 2, '10.00'),
(4, 3, '9.00'),
(4, 4, '7.00'),
(5, 1, '12.00'),
(5, 2, '11.00'),
(5, 3, '10.00'),
(5, 4, '8.00'),
(6, 1, '13.00'),
(6, 2, '12.00'),
(6, 3, '11.00'),
(6, 4, '9.00');

-- --------------------------------------------------------

--
-- Table structure for table `client_device_token`
--

CREATE TABLE IF NOT EXISTS `client_device_token` (
  `client_id` int(11) NOT NULL,
  `device_type` int(11) NOT NULL,
  `device_token` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `delivery_person`
--

CREATE TABLE IF NOT EXISTS `delivery_person` (
  `deliveryPersonId` bigint(18) NOT NULL COMMENT 'driver_unique_id',
  `firstName` varchar(255) NOT NULL COMMENT 'driver name',
  `email` varchar(255) NOT NULL COMMENT 'driver email',
  `password` varchar(255) NOT NULL COMMENT 'driver password',
  `phoneNumber` varchar(15) NOT NULL COMMENT 'criver contact',
  `lastName` varchar(255) NOT NULL COMMENT 'driver device token for push',
  `registrationTime` datetime NOT NULL,
  `available` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0=not busy,1=busy',
  `storeId` bigint(18) DEFAULT NULL
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `delivery_person`
--

INSERT INTO `delivery_person` (`deliveryPersonId`, `firstName`, `email`, `password`, `phoneNumber`, `lastName`, `registrationTime`, `available`, `storeId`) VALUES
(14, 'Delivery Person 1', 'd@b.c', 'changeit', '9876543210', '1', '2015-08-02 16:48:29', 0, 3);

-- --------------------------------------------------------

--
-- Table structure for table `delivery_person_device_token`
--

CREATE TABLE IF NOT EXISTS `delivery_person_device_token` (
  `delivery_person_id` int(11) NOT NULL,
  `device_type` int(11) NOT NULL,
  `device_token` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `feedback_criteria`
--

CREATE TABLE IF NOT EXISTS `feedback_criteria` (
  `id` bigint(18) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(100) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `feedback_criteria`
--

INSERT INTO `feedback_criteria` (`id`, `name`, `description`) VALUES
(1, 'Pickup Punctuality', 'Pickup Punctuality'),
(2, 'Drop Punctuality', 'Drop Punctuality'),
(3, 'Packaging', 'Packaging'),
(4, 'Ironing', 'Ironing'),
(5, 'Ease of Transaction', 'Ease of Transaction');

-- --------------------------------------------------------

--
-- Table structure for table `iron_person`
--

CREATE TABLE IF NOT EXISTS `iron_person` (
  `ironPersonId` bigint(18) NOT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  `email` varchar(45) NOT NULL,
  `phoneNumber` varchar(15) NOT NULL,
  `password` varchar(45) DEFAULT NULL,
  `registrationTime` datetime DEFAULT NULL,
  `storeId` bigint(18) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `iron_person`
--

INSERT INTO `iron_person` (`ironPersonId`, `firstName`, `lastName`, `email`, `phoneNumber`, `password`, `registrationTime`, `storeId`) VALUES
(3, 'Ironman', '1', 'i1@m.c', '9876543210', 'changeit', '2015-08-02 16:42:42', 3),
(4, 'Ironman', '2', 'i2@m.c', '1234567890', 'changeit', '2015-08-02 18:06:29', 2);

-- --------------------------------------------------------

--
-- Table structure for table `order_feedback`
--

CREATE TABLE IF NOT EXISTS `order_feedback` (
  `orderId` bigint(18) NOT NULL,
  `feedbackCriteriaId` bigint(18) NOT NULL,
  `rating` decimal(5,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- --------------------------------------------------------

--
-- Table structure for table `order_item`
--

CREATE TABLE IF NOT EXISTS `order_item` (
  `orderId` bigint(18) NOT NULL,
  `categoryId` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `rate` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `order_item`
--

INSERT INTO `order_item` (`orderId`, `categoryId`, `quantity`, `rate`) VALUES
(91, 1, 2, '8.00'),
(91, 2, 2, '9.00'),
(93, 1, 1, '8.00');

-- --------------------------------------------------------

--
-- Table structure for table `order_status`
--

CREATE TABLE IF NOT EXISTS `order_status` (
  `orderId` bigint(18) NOT NULL,
  `statusId` int(11) NOT NULL,
  `updateTime` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `order_status`
--

INSERT INTO `order_status` (`orderId`, `statusId`, `updateTime`) VALUES
(91, 0, '2015-07-21 23:42:13'),
(91, 1, '2015-07-23 02:50:21'),
(91, 2, '2015-08-02 13:04:19'),
(91, 7, '2015-07-22 23:38:32'),
(91, 8, '2015-08-10 15:50:00'),
(93, 0, '2015-08-10 15:19:11'),
(93, 1, '2015-08-10 15:20:35'),
(93, 8, '2015-08-10 15:52:09');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE IF NOT EXISTS `orders` (
  `orderId` bigint(18) NOT NULL COMMENT 'unique request id',
  `orderTime` datetime NOT NULL COMMENT 'request_time',
  `pickupTime` datetime NOT NULL COMMENT 'pickup latitude',
  `dropTime` datetime NOT NULL COMMENT 'pickup longitude',
  `userId` bigint(18) NOT NULL COMMENT 'client id from client data',
  `pickupPersonId` bigint(18) DEFAULT NULL COMMENT 'driver id from driver data',
  `actualPickupTime` datetime DEFAULT NULL COMMENT 'time of pickup by driver',
  `actualDropTime` datetime DEFAULT NULL COMMENT 'driver reached to client place time',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0=no driver assign,1=driver assign',
  `isExpress` tinyint(1) NOT NULL DEFAULT '0',
  `folded` tinyint(1) NOT NULL DEFAULT '0',
  `hanger` tinyint(1) NOT NULL DEFAULT '0',
  `billAmount` decimal(10,2) DEFAULT '0.00',
  `feedback` varchar(200) DEFAULT NULL,
  `addressId` bigint(18) NOT NULL DEFAULT '0',
  `dropPersonId` bigint(18) DEFAULT NULL,
  `ironPersonId` bigint(18) DEFAULT NULL,
  `storeId` bigint(18) DEFAULT NULL,
  `bagId` bigint(18) DEFAULT NULL,
  `feedbackComments` varchar(255) DEFAULT NULL
) ENGINE=MyISAM AUTO_INCREMENT=136 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`orderId`, `orderTime`, `pickupTime`, `dropTime`, `userId`, `pickupPersonId`, `actualPickupTime`, `actualDropTime`, `status`, `isExpress`, `folded`, `hanger`, `billAmount`, `feedback`, `addressId`, `dropPersonId`, `ironPersonId`, `storeId`, `bagId`, `feedbackComments`) VALUES
(91, '2015-07-21 23:42:13', '2015-07-22 12:30:00', '2015-07-22 13:30:00', 37, 12, NULL, NULL, 8, 0, 1, 1, '17.00', NULL, 64, NULL, 2, 2, 1, NULL),
(93, '2015-08-10 15:19:11', '2015-08-11 03:00:00', '2015-08-11 04:00:00', 37, 13, NULL, NULL, 8, 1, 0, 1, '8.00', NULL, 64, NULL, NULL, 2, NULL, NULL);
-- --------------------------------------------------------

--
-- Table structure for table `societymaster`
--

CREATE TABLE IF NOT EXISTS `societymaster` (
  `societyId` bigint(18) NOT NULL,
  `name` varchar(45) NOT NULL,
  `landmark` varchar(45) DEFAULT NULL,
  `city` varchar(45) NOT NULL,
  `state` varchar(45) NOT NULL,
  `country` varchar(45) NOT NULL,
  `pincode` varchar(45) DEFAULT NULL,
  `storeId` bigint(18) NOT NULL,
  `active` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `societymaster`
--

INSERT INTO `societymaster` (`societyId`, `name`, `landmark`, `city`, `state`, `country`, `pincode`, `storeId`, `active`) VALUES
(2, 'Sardar Center', 'Vastrapur', 'Ahmedabad', 'Gujarat', 'India', '380004', 2, 1),
(3, 'Tulsi', 'ORR', 'Rajkot', 'Gujarat', 'India', '360005', 2, 1),
(4, 'Pushkar Residency', 'Paldi', 'Ahmedabad', 'Gujarat', 'India', '380006', 2, 0);

-- --------------------------------------------------------

--
-- Table structure for table `status`
--

CREATE TABLE IF NOT EXISTS `status` (
  `statusId` int(11) NOT NULL,
  `status` varchar(45) NOT NULL,
  `description` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `status`
--

INSERT INTO `status` (`statusId`, `status`, `description`) VALUES
(-1, 'Deleted', 'Order was deleter'),
(0, 'Pending', 'Order is pending'),
(1, 'Pickup Arriving', 'Order Placed, delivery boy <deliveryPersonName> will be at your door at <pickupTime>'),
(2, 'Clothes Received', 'Your clothes have been received at our store'),
(3, 'Ironing', 'Your clothes are being ironed'),
(4, 'Clothes Packed', 'Your clothes are packed'),
(5, 'Clothes Dispatched', 'Your clothes have been dispatched'),
(6, 'Delivered', 'Your clothes have been delivered'),
(7, 'Approved', 'Order is approved'),
(8, 'Cancelled', 'Order has been cancelled'),
(9, 'Rejected', 'Order was rejected'),
(10, 'Completed', 'Order is completed');

-- --------------------------------------------------------

--
-- Table structure for table `store`
--

CREATE TABLE IF NOT EXISTS `store` (
  `storeId` bigint(18) NOT NULL,
  `name` varchar(45) NOT NULL,
  `openingDate` datetime DEFAULT NULL,
  `closingDate` datetime DEFAULT NULL,
  `active` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `store`
--

INSERT INTO `store` (`storeId`, `name`, `openingDate`, `closingDate`, `active`) VALUES
(2, 'First Store', '2015-07-30 05:30:00', NULL, 1),
(3, 'Second Store', '2015-08-06 05:30:00', NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `store_bag`
--

CREATE TABLE IF NOT EXISTS `store_bag` (
  `bagId` bigint(18) NOT NULL,
  `storeId` bigint(18) NOT NULL,
  `bagNumber` varchar(45) NOT NULL,
  `available` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `store_bag`
--

INSERT INTO `store_bag` (`bagId`, `storeId`, `bagNumber`, `available`) VALUES
(1, 2, 'FS01', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `userId` bigint(18) NOT NULL COMMENT 'unique client id',
  `firstName` varchar(255) NOT NULL COMMENT 'client name',
  `email` varchar(255) NOT NULL COMMENT 'email of client',
  `password` varchar(255) NOT NULL COMMENT 'password',
  `phoneNumber` varchar(15) DEFAULT NULL COMMENT 'contact number',
  `dateOfBirth` date DEFAULT NULL COMMENT 'client_DOB',
  `gender` varchar(8) NOT NULL DEFAULT 'Male',
  `registrationTime` datetime NOT NULL,
  `countryCode` varchar(45) DEFAULT NULL,
  `defaultAddressId` bigint(18) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `profilePicURL` varchar(255) DEFAULT NULL,
  `usercol` varchar(45) DEFAULT NULL
) ENGINE=MyISAM AUTO_INCREMENT=102 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`userId`, `firstName`, `email`, `password`, `phoneNumber`, `dateOfBirth`, `gender`, `registrationTime`, `countryCode`, `defaultAddressId`, `lastName`, `profilePicURL`, `usercol`) VALUES
(37, 'User', '1@u.c', 'chageit', '9876543210', NULL, 'Male', '2015-07-11 15:43:00', NULL, 121, '1', NULL, NULL),;

-- --------------------------------------------------------

--
-- Table structure for table `user_device_token`
--

CREATE TABLE IF NOT EXISTS `user_device_token` (
  `userId` bigint(18) NOT NULL,
  `deviceType` int(11) NOT NULL,
  `deviceToken` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `useraddress`
--

CREATE TABLE IF NOT EXISTS `useraddress` (
  `addressId` bigint(18) NOT NULL,
  `userId` bigint(18) DEFAULT NULL,
  `flatNumber` varchar(45) NOT NULL,
  `societyId` bigint(18) NOT NULL,
  `label` varchar(45) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `useraddress`
--

INSERT INTO `useraddress` (`addressId`, `userId`, `flatNumber`, `societyId`, `label`) VALUES
(63, 37, '321', 4, 'New Address'),
(64, 37, '23', 1, 'My Address'),


--
-- Indexes for dumped tables
--

--
-- Indexes for table `about_us`
--
ALTER TABLE `about_us`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `admin_user`
--
ALTER TABLE `admin_user`
  ADD PRIMARY KEY (`userId`),
  ADD UNIQUE KEY `email_UNIQUE` (`email`);

--
-- Indexes for table `category_master`
--
ALTER TABLE `category_master`
  ADD PRIMARY KEY (`categoryId`);

--
-- Indexes for table `category_society_price`
--
ALTER TABLE `category_society_price`
  ADD PRIMARY KEY (`categoryId`,`societyId`);

--
-- Indexes for table `client_device_token`
--
ALTER TABLE `client_device_token`
  ADD PRIMARY KEY (`client_id`,`device_type`,`device_token`);

--
-- Indexes for table `delivery_person`
--
ALTER TABLE `delivery_person`
  ADD PRIMARY KEY (`deliveryPersonId`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `delivery_person_device_token`
--
ALTER TABLE `delivery_person_device_token`
  ADD PRIMARY KEY (`delivery_person_id`,`device_type`,`device_token`);

--
-- Indexes for table `feedback_criteria`
--
ALTER TABLE `feedback_criteria`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `iron_person`
--
ALTER TABLE `iron_person`
  ADD PRIMARY KEY (`ironPersonId`);

--
-- Indexes for table `order_feedback`
--
ALTER TABLE `order_feedback`
  ADD PRIMARY KEY (`orderId`,`feedbackCriteriaId`);

--
-- Indexes for table `order_item`
--
ALTER TABLE `order_item`
  ADD PRIMARY KEY (`orderId`,`categoryId`);

--
-- Indexes for table `order_status`
--
ALTER TABLE `order_status`
  ADD PRIMARY KEY (`orderId`,`statusId`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`orderId`);

--
-- Indexes for table `societymaster`
--
ALTER TABLE `societymaster`
  ADD PRIMARY KEY (`societyId`);

--
-- Indexes for table `status`
--
ALTER TABLE `status`
  ADD PRIMARY KEY (`statusId`);

--
-- Indexes for table `store`
--
ALTER TABLE `store`
  ADD PRIMARY KEY (`storeId`);

--
-- Indexes for table `store_bag`
--
ALTER TABLE `store_bag`
  ADD PRIMARY KEY (`bagId`,`storeId`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`userId`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_device_token`
--
ALTER TABLE `user_device_token`
  ADD PRIMARY KEY (`userId`,`deviceType`,`deviceToken`);

--
-- Indexes for table `useraddress`
--
ALTER TABLE `useraddress`
  ADD PRIMARY KEY (`addressId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `about_us`
--
ALTER TABLE `about_us`
  MODIFY `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT 'auto id',AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `admin_user`
--
ALTER TABLE `admin_user`
  MODIFY `userId` bigint(18) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `delivery_person`
--
ALTER TABLE `delivery_person`
  MODIFY `deliveryPersonId` bigint(18) NOT NULL AUTO_INCREMENT COMMENT 'driver_unique_id',AUTO_INCREMENT=16;
--
-- AUTO_INCREMENT for table `feedback_criteria`
--
ALTER TABLE `feedback_criteria`
  MODIFY `id` bigint(18) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `iron_person`
--
ALTER TABLE `iron_person`
  MODIFY `ironPersonId` bigint(18) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `orderId` bigint(18) NOT NULL AUTO_INCREMENT COMMENT 'unique request id',AUTO_INCREMENT=136;
--
-- AUTO_INCREMENT for table `societymaster`
--
ALTER TABLE `societymaster`
  MODIFY `societyId` bigint(18) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `store`
--
ALTER TABLE `store`
  MODIFY `storeId` bigint(18) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `store_bag`
--
ALTER TABLE `store_bag`
  MODIFY `bagId` bigint(18) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `userId` bigint(18) NOT NULL AUTO_INCREMENT COMMENT 'unique client id',AUTO_INCREMENT=102;
--
-- AUTO_INCREMENT for table `useraddress`
--
ALTER TABLE `useraddress`
  MODIFY `addressId` bigint(18) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=127;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
