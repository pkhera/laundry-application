-- ----------------------------------------------------------------------------
-- MySQL Workbench Migration
-- Migrated Schemata: laundry
-- Source Schemata: laundry
-- Created: Thu Jul 16 00:55:50 2015
-- ----------------------------------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;;

-- ----------------------------------------------------------------------------
-- Schema laundry
-- ----------------------------------------------------------------------------
DROP SCHEMA IF EXISTS `laundry` ;
CREATE SCHEMA IF NOT EXISTS `laundry` ;

-- ----------------------------------------------------------------------------
-- Table laundry.about_us
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`about_us` (
  `id` BIGINT(18) NOT NULL AUTO_INCREMENT COMMENT 'auto id',
  `about` VARCHAR(1000) NOT NULL COMMENT 'about content',
  `link_1` VARCHAR(500) NOT NULL,
  `link_2` VARCHAR(500) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.admin
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`admin` (
  `id` INT(100) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(100) NOT NULL,
  `pass` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = MyISAM
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.admin_user
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`admin_user` (
  `userId` INT(11) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `firstName` VARCHAR(45) NULL DEFAULT NULL,
  `lastName` VARCHAR(45) NULL DEFAULT NULL,
  `password` VARCHAR(45) NULL DEFAULT NULL,
  `phoneNumber` VARCHAR(45) NULL DEFAULT NULL,
  `registrationTime` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.category_master
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`category_master` (
  `categoryId` INT(11) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(200) NULL DEFAULT NULL,
  `defaultRate` DECIMAL(10,2) NOT NULL,
  `pluralName` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`categoryId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.category_society_price
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`category_society_price` (
  `categoryId` INT(11) NOT NULL,
  `societyId` INT(11) NOT NULL,
  `price` DECIMAL(10,2) NULL DEFAULT NULL,
  PRIMARY KEY (`categoryId`, `societyId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.client_device_token
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`client_device_token` (
  `client_id` INT(11) NOT NULL,
  `device_type` INT(11) NOT NULL,
  `device_token` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`client_id`, `device_type`, `device_token`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.delivery_person
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`delivery_person` (
  `deliveryPersonId` BIGINT(18) NOT NULL AUTO_INCREMENT COMMENT 'driver_unique_id',
  `firstName` VARCHAR(255) NOT NULL COMMENT 'driver name',
  `email` VARCHAR(255) NOT NULL COMMENT 'driver email',
  `password` VARCHAR(255) NOT NULL COMMENT 'driver password',
  `phoneNumber` VARCHAR(15) NOT NULL COMMENT 'criver contact',
  `lastName` VARCHAR(255) NOT NULL COMMENT 'driver device token for push',
  `registrationTime` DATETIME NOT NULL,
  `available` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '0=not busy,1=busy',
  PRIMARY KEY (`deliveryPersonId`),
  UNIQUE INDEX `email` (`email` ASC))
ENGINE = MyISAM
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.delivery_person_device_token
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`delivery_person_device_token` (
  `delivery_person_id` INT(11) NOT NULL,
  `device_type` INT(11) NOT NULL,
  `device_token` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`delivery_person_id`, `device_type`, `device_token`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.feedback_data
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`feedback_data` (
  `feedback_id` BIGINT(18) NOT NULL AUTO_INCREMENT COMMENT 'unique feedback_id',
  `request_id` BIGINT(18) NOT NULL COMMENT 'request id from request data',
  `driver_id` BIGINT(18) NOT NULL COMMENT 'driver id from driver data',
  `client_id` BIGINT(18) NOT NULL COMMENT 'client id from client data',
  `time` DATETIME NOT NULL COMMENT 'feedback time',
  `rating` FLOAT NOT NULL COMMENT 'rating given to driver',
  `comment` VARCHAR(500) NOT NULL COMMENT 'comments by client',
  PRIMARY KEY (`feedback_id`))
ENGINE = MyISAM
AUTO_INCREMENT = 24
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.operator_no
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`operator_no` (
  `operator` VARCHAR(20) NOT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.order_item
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`order_item` (
  `orderId` BIGINT(18) NOT NULL,
  `categoryId` INT(11) NOT NULL,
  `quantity` INT(11) NOT NULL,
  `rate` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`orderId`, `categoryId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.order_status
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`order_status` (
  `orderId` BIGINT(18) NOT NULL,
  `statusId` INT(11) NOT NULL,
  `updateTime` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`orderId`, `statusId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.orders
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`orders` (
  `orderId` BIGINT(18) NOT NULL AUTO_INCREMENT COMMENT 'unique request id',
  `orderTime` DATETIME NOT NULL COMMENT 'request_time',
  `pickupTime` DATETIME NOT NULL COMMENT 'pickup latitude',
  `dropTime` DATETIME NOT NULL COMMENT 'pickup longitude',
  `userId` BIGINT(18) NOT NULL COMMENT 'client id from client data',
  `deliveryPersonId` BIGINT(18) NULL DEFAULT NULL COMMENT 'driver id from driver data',
  `actualPickupTime` DATETIME NULL DEFAULT NULL COMMENT 'time of pickup by driver',
  `actualDropTime` DATETIME NULL DEFAULT NULL COMMENT 'driver reached to client place time',
  `status` INT(11) NOT NULL DEFAULT '0' COMMENT '0=no driver assign,1=driver assign',
  `isExpress` TINYINT(1) NOT NULL DEFAULT '0',
  `folded` TINYINT(1) NOT NULL DEFAULT '0',
  `hanger` TINYINT(1) NOT NULL DEFAULT '0',
  `billAmount` DECIMAL(10,2) NULL DEFAULT '0.00',
  `feedback` VARCHAR(200) NULL DEFAULT NULL,
  `addressId` INT(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`orderId`))
ENGINE = MyISAM
AUTO_INCREMENT = 87
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.societymaster
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`societymaster` (
  `societyId` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `landmark` VARCHAR(45) NULL DEFAULT NULL,
  `city` VARCHAR(45) NOT NULL,
  `state` VARCHAR(45) NOT NULL,
  `country` VARCHAR(45) NOT NULL,
  `pincode` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`societyId`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.status
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`status` (
  `statusId` INT(11) NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`statusId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.user
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`user` (
  `userId` BIGINT(18) NOT NULL AUTO_INCREMENT COMMENT 'unique client id',
  `firstName` VARCHAR(255) NOT NULL COMMENT 'client name',
  `email` VARCHAR(255) NOT NULL COMMENT 'email of client',
  `password` VARCHAR(255) NOT NULL COMMENT 'password',
  `phoneNumber` VARCHAR(15) NOT NULL COMMENT 'contact number',
  `dateOfBirth` DATE NULL DEFAULT NULL COMMENT 'client_DOB',
  `gender` VARCHAR(8) NOT NULL DEFAULT 'Male',
  `registrationTime` DATETIME NOT NULL,
  `countryCode` VARCHAR(45) NULL DEFAULT NULL,
  `defaultAddressId` INT(11) NULL DEFAULT NULL,
  `lastName` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE INDEX `email` (`email` ASC))
ENGINE = MyISAM
AUTO_INCREMENT = 71
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.useraddress
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`useraddress` (
  `addressId` INT(11) NOT NULL AUTO_INCREMENT,
  `userId` BIGINT(18) NULL DEFAULT NULL,
  `flatNumber` VARCHAR(45) NOT NULL,
  `societyId` INT(11) NOT NULL,
  `label` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`addressId`))
ENGINE = InnoDB
AUTO_INCREMENT = 63
DEFAULT CHARACTER SET = utf8;
SET FOREIGN_KEY_CHECKS = 1;;
