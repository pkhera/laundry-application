REM Workbench Table Data copy script
REM 
REM Execute this to copy table data from a source RDBMS to MySQL.
REM Edit the options below to customize it. You will need to provide passwords, at least.
REM 


@ECHO OFF
REM Source and target DB passwords
set arg_source_password=
set arg_target_password=

IF [%arg_source_password%] == [] (
    IF [%arg_target_password%] == [] (
        ECHO WARNING: Both source and target RDBMSes passwords are empty. You should edit this file to set them.
    )
)
set arg_worker_count=2
REM Uncomment the following options according to your needs

REM Whether target tables should be truncated before copy
REM set arg_truncate_target=--truncate-target
REM Enable debugging output
REM set arg_debug_output=--log-level=debug3


REM Creation of file with table definitions for copytable

set table_file="%TMP%\wb_tables_to_migrate.txt"
TYPE NUL > "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`status`	`laundry`	`status`	`statusId`	`statusId`	`statusId`, `status`, `description` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`user_promotion_xref`	`laundry`	`user_promotion_xref`	`userId`,`promotionId`	`userId`,`promotionId`	`userId`, `promotionId`, `usageCount` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`societymaster`	`laundry`	`societymaster`	`societyId`	`societyId`	`societyId`, `name`, `landmark`, `city`, `state`, `country`, `pincode`, `storeId`, `active` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`user_provider_xref`	`laundry`	`user_provider_xref`	`userId`,`providerName`	`userId`,`providerName`	`userId`, `providerName`, `providerUserId`, `profilePicURL` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`admin_user`	`laundry`	`admin_user`	`userId`	`userId`	`userId`, `email`, `firstName`, `lastName`, `password`, `phoneNumber`, `registrationTime`, `storeId` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`store_bag`	`laundry`	`store_bag`	`bagId`,`storeId`	`bagId`,`storeId`	`bagId`, `storeId`, `bagNumber`, `available` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`support`	`laundry`	`support`			`phone`, `whatsapp`, `email`, `whatsappId` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`faq`	`laundry`	`faq`	`id`	`id`	`id`, `question`, `answer`, `type` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`user_device_token`	`laundry`	`user_device_token`	`userId`,`deviceType`,`deviceToken`	`userId`,`deviceType`,`deviceToken`	`userId`, `deviceType`, `deviceToken` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`transactions`	`laundry`	`transactions`	`transactionId`	`transactionId`	`transactionId`, `userId`, `amount`, `orderId`, `paymentTransactionId` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`delivery_person`	`laundry`	`delivery_person`	`deliveryPersonId`	`deliveryPersonId`	`deliveryPersonId`, `firstName`, `email`, `password`, `phoneNumber`, `lastName`, `registrationTime`, `available`, `storeId` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`user`	`laundry`	`user`	`userId`	`userId`	`userId`, `firstName`, `email`, `password`, `phoneNumber`, `dateOfBirth`, `gender`, `registrationTime`, `countryCode`, `defaultAddressId`, `lastName`, `profilePicURL`, `usercol`, `providerUserId` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`category_society_price`	`laundry`	`category_society_price`	`categoryId`,`societyId`	`categoryId`,`societyId`	`categoryId`, `societyId`, `price`, `expressDeliveryPrice` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`orders`	`laundry`	`orders`	`orderId`	`orderId`	`orderId`, `orderTime`, `pickupTime`, `dropTime`, `userId`, `pickupPersonId`, `actualPickupTime`, `actualDropTime`, `status`, `isExpress`, `folded`, `hanger`, `billAmount`, `feedback`, `addressId`, `dropPersonId`, `ironPersonId`, `storeId`, `bagId`, `feedbackComments`, `cancellationReason`, `comments`, `modeOfPayment`, `numberOfItems`, `transactionId`, `promotionId` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`order_cancellation_reason`	`laundry`	`order_cancellation_reason`	`id`	`id`	`id`, `description` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`order_status`	`laundry`	`order_status`	`orderId`,`statusId`	`orderId`,`statusId`	`orderId`, `statusId`, `updateTime` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`payment_transaction`	`laundry`	`payment_transaction`	`id`	`id`	`id`, `orderId`, `userId`, `gatewayName`, `responseMessage`, `bankName`, `paymentMode`, `responseCode`, `transactionId`, `transactionAmount`, `transactionType`, `transactionDateTime`, `status`, `bankTransactionId`, `refundAmount`, `promotionId` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`promotion`	`laundry`	`promotion`	`promotionId`	`promotionId`	`promotionId`, `promotionCode`, `validFrom`, `validTo`, `maxUsage`, `percentageCredit`, `maxCredit`, `fixedCredit`, `active`, `minAmount` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`delivery_person_device_token`	`laundry`	`delivery_person_device_token`	`delivery_person_id`,`device_type`,`device_token`	`delivery_person_id`,`device_type`,`device_token`	`delivery_person_id`, `device_type`, `device_token` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`useraddress`	`laundry`	`useraddress`	`addressId`	`addressId`	`addressId`, `userId`, `flatNumber`, `societyId`, `label` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`iron_person`	`laundry`	`iron_person`	`ironPersonId`	`ironPersonId`	`ironPersonId`, `firstName`, `lastName`, `email`, `phoneNumber`, `password`, `registrationTime`, `storeId` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`store`	`laundry`	`store`	`storeId`	`storeId`	`storeId`, `name`, `openingDate`, `closingDate`, `active` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`order_item`	`laundry`	`order_item`	`orderId`,`categoryId`	`orderId`,`categoryId`	`orderId`, `categoryId`, `quantity`, `rate`, `expressDeliveryPrice` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`about_us`	`laundry`	`about_us`	`id`	`id`	`id`, `about`, `link_1`, `link_2` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`category_master`	`laundry`	`category_master`	`categoryId`	`categoryId`	`categoryId`, `name`, `description`, `defaultRate`, `pluralName` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`order_feedback`	`laundry`	`order_feedback`	`orderId`,`feedbackCriteriaId`	`orderId`,`feedbackCriteriaId`	`orderId`, `feedbackCriteriaId`, `rating` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`feedback_criteria`	`laundry`	`feedback_criteria`	`id`	`id`	`id`, `name`, `description` >> "%TMP%\wb_tables_to_migrate.txt"
ECHO `laundry`	`client_device_token`	`laundry`	`client_device_token`	`client_id`,`device_type`,`device_token`	`client_id`,`device_type`,`device_token`	`client_id`, `device_type`, `device_token` >> "%TMP%\wb_tables_to_migrate.txt"


wbcopytables.exe --mysql-source="user@source.com:3306" --target="user@target.com:3306" --source-password="%arg_source_password%" --target-password="%arg_target_password%" --table-file="%table_file%" --thread-count=%arg_worker_count% %arg_truncate_target% %arg_debug_output%

REM Removes the file with the table definitions
DEL "%TMP%\wb_tables_to_migrate.txt"


