-- ----------------------------------------------------------------------------
-- MySQL Workbench Migration
-- Migrated Schemata: laundry
-- Source Schemata: laundry
-- Created: Sat May 28 19:47:52 2016
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
-- Table laundry.admin_user
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`admin_user` (
  `userId` BIGINT(18) NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(45) NOT NULL,
  `firstName` VARCHAR(45) NULL DEFAULT NULL,
  `lastName` VARCHAR(45) NULL DEFAULT NULL,
  `password` VARCHAR(45) NULL DEFAULT NULL,
  `phoneNumber` VARCHAR(45) NULL DEFAULT NULL,
  `registrationTime` DATETIME NULL DEFAULT NULL,
  `storeId` BIGINT(18) NOT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 5
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
  `expressDeliveryPrice` DECIMAL(10,2) NULL DEFAULT NULL,
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
  `password` VARCHAR(255) NOT NULL DEFAULT 'changeit' COMMENT 'driver password',
  `phoneNumber` VARCHAR(15) NOT NULL COMMENT 'criver contact',
  `lastName` VARCHAR(255) NOT NULL COMMENT 'driver device token for push',
  `registrationTime` DATETIME NOT NULL,
  `available` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '0=not busy,1=busy',
  `storeId` BIGINT(18) NULL DEFAULT NULL,
  PRIMARY KEY (`deliveryPersonId`),
  UNIQUE INDEX `email` (`email` ASC))
ENGINE = MyISAM
AUTO_INCREMENT = 19
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
-- Table laundry.faq
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`faq` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `question` VARCHAR(128) NULL DEFAULT NULL,
  `answer` VARCHAR(512) NULL DEFAULT NULL,
  `type` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 11
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.feedback_criteria
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`feedback_criteria` (
  `id` BIGINT(18) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.iron_person
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`iron_person` (
  `ironPersonId` BIGINT(18) NOT NULL AUTO_INCREMENT,
  `firstName` VARCHAR(45) NULL DEFAULT NULL,
  `lastName` VARCHAR(45) NULL DEFAULT NULL,
  `email` VARCHAR(45) NOT NULL,
  `phoneNumber` VARCHAR(15) NOT NULL,
  `password` VARCHAR(45) NULL DEFAULT NULL,
  `registrationTime` DATETIME NULL DEFAULT NULL,
  `storeId` BIGINT(18) NULL DEFAULT NULL,
  PRIMARY KEY (`ironPersonId`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.order_cancellation_reason
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`order_cancellation_reason` (
  `id` BIGINT(18) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.order_feedback
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`order_feedback` (
  `orderId` BIGINT(18) NOT NULL,
  `feedbackCriteriaId` BIGINT(18) NOT NULL,
  `rating` DECIMAL(5,2) NOT NULL,
  PRIMARY KEY (`orderId`, `feedbackCriteriaId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.order_item
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`order_item` (
  `orderId` BIGINT(18) NOT NULL,
  `categoryId` INT(11) NOT NULL,
  `quantity` INT(11) NOT NULL,
  `rate` DECIMAL(10,2) NOT NULL,
  `expressDeliveryPrice` DECIMAL(10,2) NOT NULL,
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
  `pickupPersonId` BIGINT(18) NULL DEFAULT NULL COMMENT 'driver id from driver data',
  `actualPickupTime` DATETIME NULL DEFAULT NULL COMMENT 'time of pickup by driver',
  `actualDropTime` DATETIME NULL DEFAULT NULL COMMENT 'driver reached to client place time',
  `status` INT(11) NOT NULL DEFAULT '0' COMMENT '0=no driver assign,1=driver assign',
  `isExpress` TINYINT(1) NOT NULL DEFAULT '0',
  `folded` TINYINT(1) NOT NULL DEFAULT '0',
  `hanger` TINYINT(1) NOT NULL DEFAULT '0',
  `billAmount` DECIMAL(10,2) NULL DEFAULT '0.00',
  `feedback` INT(11) NULL DEFAULT NULL,
  `addressId` BIGINT(18) NOT NULL DEFAULT '0',
  `dropPersonId` BIGINT(18) NULL DEFAULT NULL,
  `ironPersonId` BIGINT(18) NULL DEFAULT NULL,
  `storeId` BIGINT(18) NULL DEFAULT NULL,
  `bagId` BIGINT(18) NULL DEFAULT NULL,
  `feedbackComments` VARCHAR(255) NULL DEFAULT NULL,
  `cancellationReason` BIGINT(18) NULL DEFAULT NULL,
  `comments` VARCHAR(255) NULL DEFAULT NULL,
  `modeOfPayment` VARCHAR(45) NULL DEFAULT 'COD',
  `numberOfItems` BIGINT(18) NULL DEFAULT NULL,
  `transactionId` BIGINT(18) NULL DEFAULT NULL,
  `promotionId` BIGINT(18) NULL DEFAULT NULL,
  PRIMARY KEY (`orderId`))
ENGINE = MyISAM
AUTO_INCREMENT = 365
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.payment_transaction
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`payment_transaction` (
  `id` BIGINT(18) NOT NULL AUTO_INCREMENT,
  `orderId` BIGINT(18) NULL DEFAULT NULL,
  `userId` BIGINT(18) NOT NULL,
  `gatewayName` VARCHAR(45) NULL DEFAULT NULL,
  `responseMessage` VARCHAR(200) NULL DEFAULT NULL,
  `bankName` VARCHAR(45) NULL DEFAULT NULL,
  `paymentMode` VARCHAR(45) NULL DEFAULT NULL,
  `responseCode` INT(11) NULL DEFAULT NULL,
  `transactionId` VARCHAR(45) NULL DEFAULT NULL,
  `transactionAmount` DECIMAL(10,2) NULL DEFAULT NULL,
  `transactionType` VARCHAR(45) NULL DEFAULT NULL,
  `transactionDateTime` DATETIME NULL DEFAULT NULL,
  `status` VARCHAR(45) NULL DEFAULT NULL,
  `bankTransactionId` VARCHAR(45) NULL DEFAULT NULL,
  `refundAmount` DECIMAL(10,2) NULL DEFAULT NULL,
  `promotionId` BIGINT(18) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 100075
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.promotion
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`promotion` (
  `promotionId` BIGINT(18) NOT NULL AUTO_INCREMENT,
  `promotionCode` VARCHAR(45) NOT NULL,
  `validFrom` DATETIME NOT NULL,
  `validTo` DATETIME NULL DEFAULT NULL,
  `maxUsage` INT(11) NULL DEFAULT NULL,
  `percentageCredit` INT(11) NULL DEFAULT NULL,
  `maxCredit` DECIMAL(10,2) NULL DEFAULT NULL,
  `fixedCredit` DECIMAL(10,2) NULL DEFAULT NULL,
  `active` TINYINT(1) NOT NULL,
  `minAmount` DECIMAL(10,2) NULL DEFAULT NULL,
  PRIMARY KEY (`promotionId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.societymaster
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`societymaster` (
  `societyId` BIGINT(18) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `landmark` VARCHAR(45) NULL DEFAULT NULL,
  `city` VARCHAR(45) NOT NULL,
  `state` VARCHAR(45) NOT NULL,
  `country` VARCHAR(45) NOT NULL,
  `pincode` VARCHAR(45) NULL DEFAULT NULL,
  `storeId` BIGINT(18) NOT NULL,
  `active` TINYINT(1) NULL DEFAULT NULL,
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
-- Table laundry.store
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`store` (
  `storeId` BIGINT(18) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `openingDate` DATETIME NULL DEFAULT NULL,
  `closingDate` DATETIME NULL DEFAULT NULL,
  `active` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`storeId`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.store_bag
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`store_bag` (
  `bagId` BIGINT(18) NOT NULL AUTO_INCREMENT,
  `storeId` BIGINT(18) NOT NULL,
  `bagNumber` VARCHAR(45) NOT NULL,
  `available` TINYINT(1) NULL DEFAULT '1',
  PRIMARY KEY (`bagId`, `storeId`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.support
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`support` (
  `phone` VARCHAR(50) NOT NULL,
  `whatsapp` VARCHAR(45) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `whatsappId` VARCHAR(45) NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.transactions
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`transactions` (
  `transactionId` BIGINT(18) NOT NULL AUTO_INCREMENT,
  `userId` BIGINT(18) NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `orderId` BIGINT(18) NULL DEFAULT NULL,
  `paymentTransactionId` BIGINT(18) NULL DEFAULT NULL,
  PRIMARY KEY (`transactionId`))
ENGINE = InnoDB
AUTO_INCREMENT = 78
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.user
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`user` (
  `userId` BIGINT(18) NOT NULL AUTO_INCREMENT COMMENT 'unique client id',
  `firstName` VARCHAR(255) NOT NULL COMMENT 'client name',
  `email` VARCHAR(255) NOT NULL COMMENT 'email of client',
  `password` VARCHAR(255) NULL DEFAULT NULL COMMENT 'password',
  `phoneNumber` VARCHAR(15) NULL DEFAULT NULL COMMENT 'contact number',
  `dateOfBirth` DATE NULL DEFAULT NULL COMMENT 'client_DOB',
  `gender` VARCHAR(8) NOT NULL DEFAULT 'Male',
  `registrationTime` DATETIME NOT NULL,
  `countryCode` VARCHAR(45) NULL DEFAULT NULL,
  `defaultAddressId` BIGINT(18) NULL DEFAULT NULL,
  `lastName` VARCHAR(255) NULL DEFAULT NULL,
  `profilePicURL` VARCHAR(255) NULL DEFAULT NULL,
  `usercol` VARCHAR(45) NULL DEFAULT NULL,
  `providerUserId` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE INDEX `email` (`email` ASC))
ENGINE = MyISAM
AUTO_INCREMENT = 264
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.user_device_token
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`user_device_token` (
  `userId` BIGINT(18) NOT NULL,
  `deviceType` INT(11) NOT NULL,
  `deviceToken` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`userId`, `deviceType`, `deviceToken`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.user_promotion_xref
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`user_promotion_xref` (
  `userId` BIGINT(18) NOT NULL,
  `promotionId` BIGINT(18) NOT NULL,
  `usageCount` INT(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`userId`, `promotionId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table laundry.user_provider_xref
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`user_provider_xref` (
  `userId` BIGINT(18) NOT NULL,
  `providerName` VARCHAR(45) NOT NULL,
  `providerUserId` VARCHAR(255) NULL DEFAULT NULL,
  `profilePicURL` VARCHAR(1024) NULL DEFAULT NULL,
  PRIMARY KEY (`userId`, `providerName`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table laundry.useraddress
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `laundry`.`useraddress` (
  `addressId` BIGINT(18) NOT NULL AUTO_INCREMENT,
  `userId` BIGINT(18) NULL DEFAULT NULL,
  `flatNumber` VARCHAR(45) NOT NULL,
  `societyId` BIGINT(18) NOT NULL,
  `label` VARCHAR(45) NULL DEFAULT 'NONE',
  PRIMARY KEY (`addressId`))
ENGINE = InnoDB
AUTO_INCREMENT = 279
DEFAULT CHARACTER SET = utf8;
SET FOREIGN_KEY_CHECKS = 1;;
