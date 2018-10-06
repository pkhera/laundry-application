'use strict';

/* Controllers */

angular.module('myApp.controllers', ['ngCookies'])

	.controller('orderHistoryCtrl', function($scope, $http, $rootScope, $location, $timeout, $interval, $filter) {
		$timeout(function(){
			initSpinners();
			$rootScope.openSelectOnFocus('society');
			$rootScope.openSelectOnFocus('flatNumber');
			$rootScope.openSelectOnFocus('user');
			$rootScope.openSelectOnFocus('address');
		});
		
		$scope.multiSelect = [];
		
		$('#pickupDate').datepicker().on('changeDate', function (ev) {
			$rootScope.pickupDateChanged($scope);
		});
		
		$('#dropDate').datepicker().on('changeDate', function (ev) {
			$rootScope.dropDateChanged($scope);
		});
		$rootScope.setPickupDropDateTime($scope);
		
		$scope.addNewOrder = function(){
			$scope.pickup = {};
			$scope.orderFlatNumber = '';
			$rootScope.setPickupDropDateTime($scope);
			$('#orderDetailsTab').tab('show');
			$timeout(function(){
				initSpinners();
			});
		}
		
		$scope.createNewOrder = function(){
			$scope.pickup = {};
			$scope.orderFlatNumber = '';
			$scope.userName = '';
			$rootScope.setPickupDropDateTime($scope);
			$timeout(function(){
				initSpinners();
			});
		}
		
		$scope.placeOrder = function() {
			
			//$scope.pickup.orderItems = $filter('filter')($scope.orderCategories, {"societyId": $scope.orderSociety.societyId});
			//$scope.pickup.addressId = $scope.pickupAddress.addressId;
			//$scope.pickup.modeOfPayment = "COD"
			$scope.pickup.pickupTime = $scope.pickupDate + " " + $scope.pickupTime;
			//$scope.pickup.dropTime = $scope.dropDate + " " + $scope.dropTime;
			//var order = {"pickup": $scope.pickup, "items": $scope.items, "client_id": $rootScope.globals.currentUser.user.client_id};
			if($scope.pickup.user){
				angular.forEach($scope.pickup.user.addresses, function(address) {
					if(address.flatNumber == $scope.orderFlatNumber){
						$scope.pickup.address = address;
						console.log(address);
						return;
					}
				});
			}
			else{
				//var name = $scope.orderFlatNumber + "_" + $scope.userName + "_" + $rootScope.defaultSociety.name;
				var address = {"label":"Home", "flatNumber" : $scope.orderFlatNumber, "society": {"societyId":  $scope.orderSociety.societyId}};
				$scope.pickup.user = {"firstName" : $scope.userName, "addresses" : {"Home" : address}};
				$scope.pickup.storeId = $rootScope.defaultStore.storeId;
			}
			$http.post($rootScope.baseURL + '/admin/order', $scope.pickup)
			.success(function(data){
				$scope.pickup = {};
				$rootScope.getAllOrders();
				$('#addOrderModal').modal('hide');
				$('#createOrderModal').modal('hide');
				displayValidation("Order Placed Successfully!", 5000);
			})
			.error(function(response, status){
				if(status==401){
					$location.path("/login");
				}
				else if (status==400){
					$scope.errMessage = response.message;
					displayValidation($scope.errMessage, 5000);
				}
				else{
					$scope.errMessage = 'Oops, looks like something went wrong. Please try again.';
					
					displayValidation($scope.errMessage, 5000);
				}
			});
		}
		
		$scope.getUsersCallBack = function(users){
			$scope.users = users;
			$scope.flatNumbers = [];
			angular.forEach(users, function(user) {
				angular.forEach(user.addresses, function(address) {
					if(($scope.flatNumbers.indexOf(address.flatNumber) == -1)){
						$scope.flatNumbers.push(address.flatNumber);
					}
					
				});
			});
		}
		$rootScope.getUsers($scope.getUsersCallBack);
		
		$scope.usersForFlatNumber = function(){
			$scope.flatUsers = [];
			angular.forEach($scope.users, function(user){
				if(user.addresses['Home'] && user.addresses['Home'].flatNumber == $scope.orderFlatNumber){
					$scope.flatUsers.push(user);
					//console.log(user);
				}
				else if(user.addresses['Office'] && user.addresses['Office'].flatNumber == $scope.orderFlatNumber){
					$scope.flatUsers.push(user);
					//console.log(user);
				}
				else if(user.addresses['Other'] && user.addresses['Other'].flatNumber == $scope.orderFlatNumber){
					$scope.flatUsers.push(user);
					//console.log(user);
				}
			});
			if($scope.flatUsers.length > 0){
				$scope.pickup.user = $scope.flatUsers[0];
			}
			return $scope.flatUsers;
		}
		
		$rootScope.disableLoading = true;
		var ping=true;
		
		var stop = $interval(function(){
			if(ping){
				$rootScope.getAllOrders();
			}
		},60000);
		
		$scope.stopPing = function(){
			ping = false;
			$rootScope.disableLoading = false;
			if (angular.isDefined(stop)) {
				$interval.cancel(stop);
				stop = undefined;
			}
		}
		
		$scope.$on('$destroy', function() {
          // Make sure that the interval is destroyed too
          $scope.stopPing();
        });
		
		
		$scope.getIronPersonQueue = function(){
			var url = $rootScope.baseURL + '/admin/ironPerson/queue?storeId=' + $rootScope.defaultStore.storeId;
			
			$http.get(url)
			.success(function(data){
				$scope.queues ={} ;
				angular.forEach(data, function(order) {
					if(!$scope.queues.hasOwnProperty(order.ironPerson.ironPersonId)){
						$scope.queues[order.ironPerson.ironPersonId] = [];
					}
					$scope.queues[order.ironPerson.ironPersonId].push(order);
				});
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		$scope.orderSelect = function(order){
			$scope.selectedOrder = order;
			$('#editOrderModal').modal("show");
		}
		
		$scope.setOrderCategories = function(){
			var societyId = $scope.orderSociety.societyId;
			$('#selectClothesTab').tab('show');
			$scope.orderCategories = $filter('filter')($rootScope.categories, {"societyId": societyId});
			$scope.getCategories();
		}
		$scope.goBack = function(){
			$('#orderDetailsTab').tab('show');
		}
		
		$scope.viewItems = function(order){
			$scope.selectedOrder = order;
			$scope.orderCategories = $filter('filter')($rootScope.categories, {"societyId": order.address.society.societyId});
			angular.forEach(order.orderItems, function(o) {
				angular.forEach($scope.orderCategories , function(oc) {
					if(oc.category.categoryId == o.category.categoryId){
						oc.quantity = o.quantity;
					}
				});
			});
			$timeout(function(){
				initSpinners();
			});
		}
		
		$scope.saveOrderCategories = function(){
			var url = $rootScope.baseURL + '/admin/order/items?orderId=' + $scope.selectedOrder.orderId;
			$http.put(url, $scope.orderCategories)
			.success(function(data){
				$("#itemsModal").modal("hide");
				displayValidation("Order items updated successfully",5000);
				$rootScope.getAllOrders();
				var orderURL = $rootScope.baseURL + '/admin/order/' + $scope.selectedOrder.orderId;
				$http.get(orderURL)
				.success(function(data){
					$scope.selectedOrder = data;
				});
			})
			.error(function(response,status){
				displayValidation(response.message, 5000);
			});
		}
		
		$scope.changePasswordFunc = function(){
			$http.post($rootScope.baseURL + '/admin/changePassword', $scope.changePassword)
			.success(function(data){
				$("#changePwdModal").modal("hide");
				displayValidation("Password changed successfully",5000);
			})
			.error(function(response,status){
				displayValidation("Error changing password.",5000);
			});
		}
		if($rootScope.admin == null){
			$http.get($rootScope.baseURL + '/admin')
			.success(function(data){
				//$scope.admin = data;
				$rootScope.admin = data;
			})
			.error(function(response, status){
				if(status == 401){
					$location.path("/login");
				}
				else if(status == 0){
					$location.path("/maintenance");
				}
				else{
					displayValidation("Error : " + response.message,5000);
				}
			});
		}
		
		$scope.getCategories = function(){
			$http.get($rootScope.baseURL + '/admin/order/category')
			.success(function(data){
				$scope.message = "";
				$scope.categories = data;
				$rootScope.categories = data;
				angular.forEach($scope.categories, function(category) {
					category.quantity = 0;
				});
				$timeout(function(){
					initSpinners();
				});
				
			})
			.error(function(response, status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					
					displayValidation("Something went wrong",5000);
				}
			});
		}
		
		if($rootScope.categories == null){
			$scope.getCategories();
		}
		else{
			$scope.categories = $rootScope.categories;
		}
		
		
		$scope.getStoresCallback = function(){
			$rootScope.getAllOrders();
			$scope.pickup = {"userId": 37, "modeOfPayment" : 'COD', "address": {"society": {"store": {"storeId": $rootScope.defaultStore.storeId}}}};
			$http.get($rootScope.baseURL + '/admin/society?storeId=' + $rootScope.defaultStore.storeId)
			.success(function(data){
				//$scope.admin = data;
				$rootScope.defaultSociety = data[0];
				$rootScope.societies = [data];
				$scope.orderSociety = data;
			})
			.error(function(response, status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong",5000);
				}
			});
		}
		
		if($rootScope.defaultStore){
			$scope.getStoresCallback();
		}
		else{
			$rootScope.getStores(null, $scope.getStoresCallback);
		}
		
		$scope.storeChangeListener = function(){
			$rootScope.getAllOrders();
		}
		
		$scope.orderStatusHistory = function(orderStatuses, currentStatus){
			$scope.orderStatuses = orderStatuses;
			$scope.currentStatus = currentStatus;
		}
		
		$scope.getSubTotal = function(order){
			if(order){
				var subTotal = 0;
				angular.forEach(order.orderItems, function(orderItem){
					subTotal = subTotal + (orderItem.quantity * (order.express ? orderItem.expressDeliveryPrice : orderItem.rate));
				});
				return subTotal;
			}
		}
		
		$scope.getAvailableDeliveryPersons = function(order, pickup){
			$scope.assignOrder = order;
			$scope.pickup = pickup;
			
			var url = $rootScope.baseURL + '/admin/deliveryPerson?available=true&storeId=' + $rootScope.defaultStore.storeId;
			$http.get(url)
			.success(function(data){
				$scope.availableDeliveryPersons = data;
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		$scope.getAvailableIronPersons = function(order){
			$scope.assignOrder = order;
			
			var url = $rootScope.baseURL + '/admin/ironPerson?storeId=' + $rootScope.defaultStore.storeId;
			
			$http.get(url)
			.success(function(data){
				$scope.availableIronPersons = data;
				$scope.getIronPersonQueue();
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		$scope.getBags = function(order){
			$scope.assignOrder = order;
			$http.get($rootScope.baseURL + '/admin/bag?storeId=' + order.storeId)
			.success(function(data){
				$scope.availableBags = data;
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		$scope.assignDeliveryPerson = function(order, pickupPerson, dropPerson){
			var url = $rootScope.baseURL + '/admin/deliveryPerson/assign?orderId=' + order.orderId;
			if(pickupPerson != null){
				url = url + '&pickupPersonId=' + pickupPerson.deliveryPersonId;
			}
			if(dropPerson != null){
				url = url + '&dropPersonId=' + dropPerson.deliveryPersonId;
			}
			$http.put(url)
			.success(function(data){
				$('#availableDeliveryPersonsModal').modal('hide');
				if($scope.selectedOrder != null){
					if(pickupPerson != null){
						$scope.selectedOrder.pickupPerson = pickupPerson;
					}
					if(dropPerson != null){
						$scope.selectedOrder.dropPerson = dropPerson;
					}
				}
				$rootScope.getAllOrders();
				$timeout(function(){
					$('#availableDeliveryPersonsModal').modal('hide');
				});
				
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		$scope.assignIronPerson = function(order, ironPerson){
			var url = $rootScope.baseURL + '/admin/ironPerson/assign?orderId=' + order.orderId + '&ironPersonId=' + ironPerson.ironPersonId;
			
			$http.put(url)
			.success(function(data){
				$('#availableIronPersonsModal').modal('hide');
				$rootScope.getAllOrders();
				$timeout(function(){
					$('#availableIronPersonsModal').modal('hide');
				});
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		$scope.assignBag = function(order, bag){
			var url = $rootScope.baseURL + '/admin/bag/assign?orderId=' + order.orderId + '&bagId=' + bag.bagId;
			
			$http.put(url)
			.success(function(data){
				order.bag = bag;
				$rootScope.getAllOrders();
				$timeout(function(){
					$('#bagsModal').modal('hide');
				});
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		$scope.setOrderStatus = function(order, statusId){
			$http.put($rootScope.baseURL + '/admin/order/status?orderId=' + order.orderId + '&statusId=' + statusId)
			.success(function(data){
				if($scope.selectedOrder != null){
					$scope.selectedOrder.currentStatus = data.order.currentStatus;
				}
				$rootScope.getAllOrders();
				
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		$scope.setBulkOrderStatus = function(orderIds, statusId){
			var orderIdList = '';
			angular.forEach(orderIds, function(orderId) {
				if(orderIdList != ''){
					orderIdList = orderIdList + ',';
				}
				orderIdList = orderIdList + orderId;
			});
			$http.put($rootScope.baseURL + '/admin/order/status?orderId=' + orderIdList + '&statusId=' + statusId)
			.success(function(data){
				$rootScope.getAllOrders();
				
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		$scope.showCashPaid = function(order){
			if(order.modeOfPayment == 'COD'){
				$scope.cashPaid = order.billAmount;
			}
			else{
				$scope.cashPaid = 0;
			}
			$scope.completedOrder=order;
		}
		
		$scope.completeOrder = function(order, cashPaid){
			$http.put($rootScope.baseURL + '/admin/order/status?orderId=' + order.orderId + '&statusId=' + 6, {'userId': order.userId, 'cashPaid': cashPaid, 'orderId' : order.orderId})
			.success(function(data){
				if($scope.selectedOrder != null){
					$scope.selectedOrder.currentStatus = data.order.currentStatus;
				}
				$rootScope.getAllOrders();
				$('#addCashPaymentModal').modal('hide');
				
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		$scope.deleteOrder = function(order){
			var sure = confirm('Are you sure you want to delete order no. ' + order.orderId);
			if(!sure){
				return;
			}
			$http.delete($rootScope.baseURL + '/admin/order?orderId=' + order.orderId)
			.success(function(data){
				$rootScope.getAllOrders();
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation(response.message, 5000);
				}
			});
		}
		
		$scope.filterApprovedAndPending = function(order){
			return ([0,1,7].indexOf(order.currentStatus.statusId) !== -1);
		}
		
		$scope.filterOrdersAwaitingIroning = function(order){
			return ([2,3].indexOf(order.currentStatus.statusId) !== -1);
		}
		
		$scope.filterOrdersIroned = function(order){
			return ([4,5].indexOf(order.currentStatus.statusId) !== -1);
		}
		
		$scope.addBag = function(order){
			$scope.editBag = {"storeId": order.storeId };
		}
		
		$scope.saveBag = function(){
			$http.post($rootScope.baseURL + '/admin/bag', $scope.editBag)
			.success(function(data){
				$scope.getBags($scope.assignBag);
				$timeout(function(){
					$('#addBagModal').modal('hide');
				});
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation(response.message, 5000);
				}
			});
		}
	})
	.controller('deliveryPersonController', function($scope, $http, $rootScope, $location, $timeout) {
		$scope.getAllDeliveryPersons = function(){
			var url = $rootScope.baseURL + '/admin/deliveryPerson?storeId=' + $rootScope.defaultStore.storeId;
			
			$http.get(url)
			.success(function(data){
				$scope.deliveryPersons = data;
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		if($rootScope.defaultStore){
			$scope.getAllDeliveryPersons();
		}
		else{
			$rootScope.getStores(null, $scope.getAllDeliveryPersons);
		}
		
		$scope.storeChangeListener = function(){
			$scope.getAllDeliveryPersons();
		}
		
		$scope.deleteDeliveryPerson = function(dp){
			var sure = confirm('Are you sure you want to delete Delivery Person. ' + dp.firstName + ' ' + dp.lastName);
			if(!sure){
				return;
			}
			$http.delete($rootScope.baseURL + '/admin/deliveryPerson?deliveryPersonId=' + dp.deliveryPersonId)
			.success(function(data){
				$scope.getAllDeliveryPersons();
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation(response.message, 5000);
				}
			});
		}
		
		$scope.editDeliveryPerson = function(deliveryPerson){
			$scope.new = false;
			$scope.deliveryPerson = deliveryPerson;
		}
		
		$scope.saveDeliveryPerson = function(){
			if($scope.new){
				if($rootScope.defaultStore){
					$scope.deliveryPerson.storeId = $rootScope.defaultStore.storeId;
				}
				$http.post($rootScope.baseURL + '/admin/deliveryPerson', $scope.deliveryPerson)
				.success(function(data){
					$scope.getAllDeliveryPersons();
					$timeout(function(){
						$('#basicInfoModal').modal('hide');
					});
				})
				.error(function(response,status){
					if(status == 401){
						$location.path("/login");
					}
					else{
						displayValidation(response.message, 5000);
					}
				});
			}
			else{
				$http.put($rootScope.baseURL + '/admin/deliveryPerson', $scope.deliveryPerson)
				.success(function(data){
					$scope.getAllDeliveryPersons();
					$timeout(function(){
						$('#basicInfoModal').modal('hide');
					});
				})
				.error(function(response,status){
					if(status == 401){
						$location.path("/login");
					}
					else{
						displayValidation(response.message, 5000);
					}
				});
			}
		}
	})
	.controller('ironPersonController', function($scope, $http, $rootScope, $location, $timeout, $interval) {
		
		var ping=true;
		var stop = $interval(function(){
			if(ping){
				$scope.getIronPersonQueue();
			}
		},30000);
		
		$scope.stopPing = function(){
			ping = false;
			if (angular.isDefined(stop)) {
				$interval.cancel(stop);
				stop = undefined;
			}
		}
		
		$scope.$on('$destroy', function() {
          // Make sure that the interval is destroyed too
          $scope.stopPing();
        });
		
		$scope.getIronPersonQueue = function(){
			var url = $rootScope.baseURL + '/admin/ironPerson/queue?storeId=' + $rootScope.defaultStore.storeId;
			
			$http.get(url)
			.success(function(data){
				$scope.queues ={} ;
				angular.forEach(data, function(order) {
					if(!$scope.queues.hasOwnProperty(order.ironPerson.ironPersonId)){
						$scope.queues[order.ironPerson.ironPersonId] = [];
					}
					$scope.queues[order.ironPerson.ironPersonId].push(order);
				});
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		$scope.getAllIronPersons = function(){
			var url = $rootScope.baseURL + '/admin/ironPerson?storeId=' + $rootScope.defaultStore.storeId;
			
			$http.get(url)
			.success(function(data){
				$scope.ironPersons = data;
				$scope.getIronPersonQueue();
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		if($rootScope.defaultStore){
			$scope.getAllIronPersons();
		}
		else{
			$rootScope.getStores(null, $scope.getAllIronPersons);
		}
		
		$scope.storeChangeListener = function(){
			$scope.getAllIronPersons();
		}
		
		$scope.setOrderStatus = function(order, statusId){
			$http.put($rootScope.baseURL + '/admin/order/status?orderId=' + order.orderId + '&statusId=' + statusId)
			.success(function(data){
				$scope.getIronPersonQueue();
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong.", 5000);
				}
			});
		}
		
		$scope.deleteIronPerson = function(ip){
			var sure = confirm('Are you sure you want to delete Iron Person. ' + ip.firstName + ' ' + ip.lastName);
			if(!sure){
				return;
			}
			$http.delete($rootScope.baseURL + '/admin/ironPerson?ironPersonId=' + ip.ironPersonId)
			.success(function(data){
				$scope.getAllIronPersons();
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation(response.message, 5000);
				}
			});
		}
		
		$scope.editIronPerson = function(ironPerson){
			$scope.new = false;
			$scope.ironPerson = ironPerson;
		}
		
		$scope.saveIronPerson = function(){
			if($scope.new){
				if($rootScope.defaultStore){
					$scope.ironPerson.storeId = $rootScope.defaultStore.storeId;
				}
				$http.post($rootScope.baseURL + '/admin/ironPerson', $scope.ironPerson)
				.success(function(data){
					$scope.getAllIronPersons();
					$timeout(function(){
						$('#basicInfoModal').modal('hide');
					});
				})
				.error(function(response,status){
					if(status == 401){
						$location.path("/login");
					}
					else{
						displayValidation(response.message, 5000);
					}
				});
			}
			else{
				$http.put($rootScope.baseURL + '/admin/ironPerson', $scope.ironPerson)
				.success(function(data){
					$scope.getAllIronPersons();
					$timeout(function(){
						$('#basicInfoModal').modal('hide');
					});
				})
				.error(function(response,status){
					if(status == 401){
						$location.path("/login");
					}
					else{
						displayValidation(response.message, 5000);
					}
				});
			}
		}
	})
	.controller('profileCtrl', function($scope, $http, $rootScope, $location, $timeout) {
		$scope.changePasswordFunc = function(){
			$http.post($rootScope.baseURL + '/admin/changePassword', $scope.changePassword)
			.success(function(data){
				$("#changePwdModal").modal("hide");
				displayValidation("Password changed successfully",5000);
			})
			.error(function(response,status){
				displayValidation("Error changing password.",5000);
			});
		}
		if($rootScope.admin == null){
			$http.get($rootScope.baseURL + '/admin')
			.success(function(data){
				$scope.admin = data;
				$rootScope.admin = data;
			})
			.error(function(response, status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Something went wrong",5000);
				}
			});
		}
		else{
			$scope.admin = $rootScope.admin;
		}

		$scope.adminCopy = function(){
			$scope.editAdmin = angular.copy($scope.admin);
		}
		
		$scope.editAdminInfo = function(){
			$http.put($rootScope.baseURL + '/admin', $scope.editAdmin)
			.success(function(data){
				$scope.admin = $scope.editAdmin;
				$timeout(function(){
					$('#basicInfoModal').modal('hide');
					displayValidation('Profile updated.', 5000);
				});
			})
			.error(function(response, status){
				displayValidation('Something went wrong.', 5000);
			});
		}
	})
	.controller('downloadCtrl', function($scope, $http, $rootScope, $location, $timeout) {
		$http.get($rootScope.baseURL + '/admin/table/list')
		.success(function(data){
			$scope.tables = data;
		})
		.error(function(response,status){
			displayValidation("Error getting tables",5000);
		});
		
		$scope.downloadCsv = function(tableName) {
			$http.get($rootScope.baseURL + '/admin/table/get?tableName=' + tableName + '&authorization=Basic ' + $rootScope.globals.currentAdmin.authdata)
			.success(function(data){
				var blob = new Blob([data], {type: "application/csv"});
				var objectUrl = URL.createObjectURL(blob);
				window.open(objectUrl);
				console.log('table downloaded');
			})
			.error(function(response,status){
				displayValidation("Error getting tables",5000);
			});
		}
	})
	.controller('maintenanceCtrl', function($scope, $http, $rootScope, $location, $interval) {
		$scope.showMenu = false;
		$rootScope.disableLoading = true;
		var ping=true;
		var stop = $interval(function(){
			if(ping){
				ping=false;
				$http.get($rootScope.baseURL + '/address/society')
				.success(function(data, status){
					$scope.stopPing();
					$rootScope.disableLoading = false;
					$location.path("/home");
				})
				.error(function(data,status){
					ping = true;
				});
			}
		},10000);
		
		$scope.stopPing = function(){
			ping = false;
			if (angular.isDefined(stop)) {
				$interval.cancel(stop);
				stop = undefined;
			}
		}
		
		$scope.$on('$destroy', function() {
          // Make sure that the interval is destroyed too
          $scope.stopPing();
        });
	})
	.controller('loginCtrl', function($scope, $http, $location, $rootScope, $cookieStore, $timeout, $window) {
		
		$http.defaults.headers.common['Authorization'] = ''; 
		$cookieStore.put('globals', {});
		$scope.user = {};
		
		$scope.loginUser = function() {
			var authdata = btoa($scope.login.email + ':' + $scope.login.password);
			$http.defaults.headers.common['Authorization'] = 'Basic ' + authdata;
			$http.get($rootScope.baseURL + '/admin')
			.success(function(data){
								
				$rootScope.admin = data;
				$rootScope.setDefaultStore(data.store, null);
				$rootScope.globals = {
					currentAdmin: {
						authdata: authdata,
						admin: data
					}
				};
				$cookieStore.put('globals', $rootScope.globals);
				$location.path("/home");
			})
			.error(function(response, status){
				displayValidation("Error : " + response.message,5000);
			});
		}
		
		$scope.forgotPassword = function() {
			$http.get($rootScope.baseURL + '/admin/recoverPassword?email=' + $scope.forgotPasswordEmail)
			.success(function(data){
				$('#forgotModal').modal('hide');
				displayValidation(data.message, 5000);
			})
			.error(function(response, status){
				displayValidation("Error recovering password.",5000);
			});
		}
	})
	.filter("asDate", function () {
		return function (input) {
			return new Date(Date.parse(input));
		}
	})
	.filter("asDateTimeString", function () {
		return function (input) {
			return (new Date(Date.parse(input))).format('dd mmm yyyy h:MM TT');
		}
	});