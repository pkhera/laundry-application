'use strict';

/* Controllers */

angular.module('myApp.controllers', ['ngCookies','googleplus'])
	.controller('orderCtrl', function($scope, $location, $http, $rootScope, $filter, $timeout, $route, $window) {
		$rootScope.getWalletBalance();
		$scope.showMenu = true;
		$scope.validateOrderPromoCode = function(amount){
			$rootScope.validatePromoCode(amount, $scope.orderPromo);
		}
		
		$scope.expressDelivery = function(){
			$scope.pickup.express=true;
			$scope.placeOrder();
		}
		
		$timeout(function(){
			$scope.pickupAddressSelectize = $('#pickupAddressLabel').selectize()[0].selectize;
			$scope.pickupAddressExSelectize = $('#pickupAddressLabelEx').selectize()[0].selectize;
			$scope.numberOfItemsExSelectize = $('#numberOfItemsEx').selectize()[0].selectize;
			$('#addressLabel').selectize();
		});
		
		$scope.init=function(){
			$scope.orderPromo = {};
			$scope.pickupAddress = $rootScope.user.addresses[$scope.pickupAddressLabel];
			
			angular.forEach($scope.categories, function(category) {
				category.quantity = 0;
			});
			
			$scope.numberOfItems = 0;
			$scope.amount = 0;
			$timeout(function(){
				initSpinners();
			});
		}
		$timeout(function(){
			initSpinners();
		});
		
		$scope.countItems=function(){
			var orderItems = $filter('filter')($scope.categories, {"societyId": $scope.pickupAddress.society.societyId});
			var count = 0;
			var amount = 0;
			angular.forEach(orderItems, function(o) {
				count = count + parseInt(o.quantity);
				amount = amount + (o.quantity * o.rate);
			});
			$scope.amount = amount;
			$scope.numberOfItems = count;
		}
		
		if(!$rootScope.user.defaultAddress){
			$timeout(function(){
				$scope.getSocieties();
				$('#addressModal').modal('show');
			});
		}
		else{
			$scope.pickupAddress = $rootScope.user.defaultAddress;
			$scope.pickupAddressLabel = $rootScope.user.defaultAddress.label;
		}
	
	
		//Fix for categories filter when no address is selected
		if(!$scope.pickupAddress){
			$scope.pickupAddress = {"society": {"societyId": -1}};
		}
		var schedule = $rootScope.user.schedule;
		if(!schedule){
			schedule = {};
		}
		$scope.pickup = {"userId": $rootScope.user.userId, "modeOfPayment" : 'PAYTM', "schedule" : schedule};
				
		if($('.modal-backdrop')){
			$('.modal-backdrop').hide();
		}
		
		
		
		$rootScope.setPickupDropDateTime($scope);
		
		$('#pickupDate').datepicker().on('changeDate', function (ev) {
			$rootScope.pickupDateChanged($scope);
		});
		
		$('#dropDate').datepicker().on('changeDate', function (ev) {
			$rootScope.dropDateChanged($scope);
		});
		
		if($rootScope.categories == null){
			
			$http.get($rootScope.baseURL + '/order/category')
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
					
					displayValidation("Oops, looks like something went wrong. Please try again.",5000);
				}
			});
		}
		else{
			$scope.categories = $rootScope.categories;
			
		}
	
		$scope.placeOrder = function() {
			
			$scope.pickup.orderItems = $filter('filter')($scope.categories, {"societyId": $scope.pickupAddress.society.societyId});
			$scope.pickup.addressId = $scope.pickupAddress.addressId;
			$scope.pickup.pickupTime = $scope.pickupDate + " " + $scope.pickupTime;
			$scope.pickup.dropTime = $scope.dropDate + " " + $scope.dropTime;
 			if($scope.orderPromo && $scope.orderPromo.applied && $scope.pickup.modeOfPayment == 'PAYTM'){
				$scope.pickup.promotionId = $scope.orderPromo.promotionId;
			}
			//var order = {"pickup": $scope.pickup, "items": $scope.items, "client_id": $rootScope.globals.currentUser.user.client_id};
			$http.post($rootScope.baseURL + '/order', $scope.pickup)
			.success(function(data){
				$rootScope.trackOrder = data;
				$location.path("/orderHistory");
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
	
		$scope.getSocieties = function(){
			$rootScope.getSocieties('#society');
		}
		$scope.editAddress = {};
		$scope.saveAddress = function(){
			if($scope.user.phoneNumber == '' || $scope.user.phoneNumber.length < 10){
				displayValidation("Please enter a valid phone number.", 5000);
			}
			else{
				$scope.editAddress.userId = $rootScope.user.userId;
				$scope.editAddress.addressId = '';
				$http.post($rootScope.baseURL + '/address', $scope.editAddress)
				.success(function(data){
					
					$http.put($rootScope.baseURL + '/user', $scope.user)
					.success(function(data){
						$window.location.reload();
					})
					.error(function(response, status){
						displayValidation(response.message, 5000);
					});
				})
				.error(function(response, status){
					displayValidation(response.message, 5000);
				});
			}
		}
	})
	.controller('orderHistoryCtrl', function($scope, $http, $rootScope, $location, $timeout, $interval, $routeParams) {
		$scope.showMenu = true;
		$scope.rating=1;
		$scope.max=5;
		$scope.sinceDays = 7;
		
		$scope.getOrders = function(orderId){
			$http.get($rootScope.baseURL + '/order')
			.success(function(data){
				$scope.orders = data;
				$scope.setGraph();
				if(orderId){
					angular.forEach(data, function(order) {
						if(order.orderId == orderId){
							if(order.currentStatus.statusId == 10 || order.currentStatus.statusId == 6){
								$scope.setFeedbackOrder(order);
								$rootScope.getWalletBalance();
								$('#feedbackModal').modal('show');
							}
							else{
								$scope.setTrackOrder(order);
								$('#trackMyOrderModal').modal('show');
							}
						}
					});
				}
			})
			.error(function(response, status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Oops, looks like something went wrong. Please try again.", 5000);
				}
			});
		}
		$scope.getOrders($routeParams.orderId);
		
		$scope.setGraph = function(){
			$scope.labels = [];
			$scope.options={
				"showScale": true,
				"responsive": true,
				"scaleShowGridLines": true,
				"scaleShowHorizontalLines": false,
				"scaleShowVerticalLines": false,
				"barShowStroke" : true,
				"barStrokeWidth" : 2,
				"barValueSpacing" :25 ,
				"barDatasetSpacing" : 1,
			}
			$scope.colors= [{
				"fillColor": '#8dd1c2',
				"strokeColor": '#8dd1c2',
				"highlightFill": '#8dd1c2',
				"highlightStroke": '#8dd1c2'
			},
			{
				"fillColor": 'rgba(0, 132, 71, 0.8)',
				"strokeColor": 'rgba(0, 132, 71, 0.8)',
				"highlightFill": 'rgba(0, 132, 71, 0.8)',
				"highlightStroke": 'rgba(0, 132, 71, 0.8)'
			},
			{
				"fillColor": 'rgba(47, 132, 0, 0.8)',
				"strokeColor": 'rgba(47, 132, 0, 0.8)',
				"highlightFill": 'rgba(47, 132, 0, 0.8)',
				"highlightStroke": 'rgba(47, 132, 0, 0.8)'
			}];
			  $scope.series = ['Orders'];
			  $scope.data = [[]];
			  var count = 0;
			  angular.forEach($scope.orders, function(order){
				if(count < 7){
					count = count + 1;
					$scope.data[0].push(order.numberOfItems);
					$scope.labels.push("");
				}
				else{
					return;
				}
			  });
				
			  $scope.onClick = function (points, evt) {
				console.log(points, evt);
			  };
		}
	 
		
		$scope.getOrderStatistics = function(){
			$http.get($rootScope.baseURL + '/order/statistics?sinceDays=' + $scope.sinceDays)
			.success(function(data){
				$scope.orderStatistics = data;
			})
			.error(function(response, status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Oops, looks like something went wrong. Please try again.", 5000);
				}
			});
		}
		$scope.getOrderStatistics();
		
		$scope.orderStatusHistory = function(orderStatuses, currentStatus){
			$scope.orderStatuses = orderStatuses;
			$scope.currentStatus = currentStatus;
		}
		
		$scope.setInvoiceOrder = function(order){
			$scope.invoiceOrder=order;
		}
		
		$scope.setFeedbackOrder = function(order){
			$scope.feedbackOrder = order;
			// angular.forEach($scope.feedbackOrder.orderFeedback, function(feedback) {
				// if(!feedback.rating){
					// feedback.rating=0;
				// }
				// if(!feedback.orderId){
					// feedback.orderId=$scope.feedbackOrder.orderId;
				// }
			// });
		}
		
		$scope.submitFeedback = function(){
			// var feedback = {"orderId": $scope.feedbackOrder.orderId, "orderFeedback": $scope.feedbackOrder.orderFeedback, "feedbackComments": $scope.feedbackOrder.feedbackComments};
			$http.put($rootScope.baseURL + '/order/' + $scope.feedbackOrder.orderId + '/feedback?feedback=' + $scope.feedbackOrder.feedback + '&feedbackComments=' + $scope.feedbackOrder.feedbackComments)
			.success(function(data){
				// displaySuccess(data.message, 5000);
				// $scope.getOrders();
				$('#feedbackModal').modal('hide');
			})
			.error(function(response, status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation(response.message, 5000);
				}
			});
		}
		
		$('#trackMyOrderModal').on('hidden.bs.modal', function () {
		  $scope.stopPing();
		  $scope.getOrders();
		})
		
		$('#feedbackModal').on('hidden.bs.modal', function () {
		  $scope.getOrders();
		})

		$scope.setOrderItems = function(order){
			$scope.itemsOrder = order;
		}
		$scope.setTrackOrder = function(order){
			$scope.trackOrder = order;
			$rootScope.disableLoading = true;
			
			$scope.ping=true;
			$scope.stop = $interval(function(){
				if($scope.ping){
					if(order.currentStatus.statusId != 6){
						$http.get($rootScope.baseURL + '/order/' + order.orderId)
						.success(function(data, status){
							$scope.trackOrder = data;
							if(data.currentStatus.statusId == 6){
								$('#trackMyOrderModal').modal('hide');
								$scope.setFeedbackOrder($scope.trackOrder);
								$rootScope.getWalletBalance();
								$('#feedbackModal').modal('show');
							}
						});
						
					}
				}
			},10000);
		}
		
		$scope.stopPing = function(){
			$scope.ping = false;
			if (angular.isDefined($scope.stop)) {
				$interval.cancel($scope.stop);
				$scope.stop = undefined;
				$rootScope.disableLoading = false;
			}
		}
		
		$scope.$on('$destroy', function() {
		  // Make sure that the interval is destroyed too
		  $scope.stopPing();
		});
		
		if($rootScope.trackOrder){
			$scope.setTrackOrder($rootScope.trackOrder);
			$('#trackMyOrderModal').modal('show');
		}
		
		
		$scope.getCancellationReasons = function(){
			$http.get($rootScope.baseURL + '/order/orderCancellationReasons')
			.success(function(data){
				$scope.cancellationReasons = data;
			})
			.error(function(response, status){
				if(status==401){
					$location.path("/login");
				}
				else{
					$scope.errMessage = 'Oops, looks like something went wrong. Please try again.';
					
					displayValidation($scope.errMessage, 5000);
				}
			});
		}
		$scope.getCancellationReasons();
		//$scope.cancellationReasonId = 0;
		$scope.orderCancellationReason={};
		$scope.cancelOrder = function(orderId){
			$http.delete($rootScope.baseURL + '/order?orderId=' + orderId + '&orderCancellationReason=' + $scope.orderCancellationReason.id)
			.success(function(data){
				displaySuccess(data.message, 5000);
				$scope.getOrders();
				$("#cancellationReasonsModal").modal('hide');
				$("#trackMyOrderModal").modal('hide');
			})
			.error(function(response, status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation(response.message, 5000);
				}
			});
		}
		
		$scope.rescheduleOrder = function(){
			$scope.pickup = angular.copy($scope.trackOrder);
			$scope.pickup.pickupTime = $scope.pickupDate + " " + $scope.pickupTime;
			$scope.pickup.dropTime = $scope.dropDate + " " + $scope.dropTime;
			$http.put($rootScope.baseURL + '/order', $scope.pickup)
			.success(function(data){
				displaySuccess(data.message, 5000);
				$scope.getOrders();
				$("#rescheduleModal").modal('hide');
				$("#trackMyOrderModal").modal('hide');
			})
			.error(function(response, status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation(response.message, 5000);
				}
			});
		}
		
		$rootScope.setPickupDropDateTime($scope);
		
		$('#pickupDate').datepicker().on('changeDate', function (ev) {
			$rootScope.pickupDateChanged($scope);
		});
		
		$('#dropDate').datepicker().on('changeDate', function (ev) {
			$rootScope.dropDateChanged($scope);
		});
		
		$timeout(function(){
				$('select.input-sm').selectize();
				$(".orderHistoryTable").niceScroll();
		});
		
	})
	.controller('categoriesCtrl', function($scope, $http, $rootScope, $location) {
		$scope.showMenu = true;
		$http.get($rootScope.baseURL + '/order/category?societyId=4')
		.success(function(data){
			$scope.categories = data.categories;
			
		})
		.error(function(response, status){
			if(status == 401){
				$location.path("/login");
			}
			else{
				
				displayValidation("Oops, looks like something went wrong. Please try again.", 5000);
			}
		});
	})
	.controller('landingCtrl', function($scope, $http, $rootScope, $location, $timeout) {
		$scope.showMenu = false;
		$http.get($rootScope.baseURL + '/help/support')
		.success(function(data, status){
			$scope.support = data;
		})
		.error(function(data,status){
			displayValidation("Unknown error.",5000);
		});
		$timeout(function(){
			$rootScope.loadSocieties('#societySearch');
		})
	})
	.controller('profileCtrl', function($scope, $http, $rootScope, $location, $timeout, $routeParams) {
		$timeout(function(){
			//scrollToID('#'+$routeParams.subMenu, 750);
			var offSet = 50;
			if($('#'+$routeParams.subMenu).offset()){
				var targetOffset = $('#'+$routeParams.subMenu).offset().top - offSet;
				var mainNav = $('#main-nav');
				$('html,body').animate({scrollTop:targetOffset}, 750);
				if (mainNav.hasClass("open")) {
					mainNav.css("height", "1px").removeClass("in").addClass("collapse");
					mainNav.removeClass("open");
				}
			}
		});
		$scope.showMenu = true;	
		$rootScope.getUser($scope.getUserCallback);
		//$scope.user = $rootScope.user;
		
		$scope.getSocieties = function(){
			$rootScope.getSocieties('#society');
		}
		
		$scope.getUserCallback = function(data){
			$scope.user = data;
		}
		
		$scope.saveAddress = function(){
			
			$scope.editAddress.userId = $scope.user.userId;
			if($scope.isNew){
				$scope.editAddress.addressId = '';
				$http.post($rootScope.baseURL + '/address', $scope.editAddress)
				.success(function(data){
					$timeout(function(){
						$('#addressModal').modal('hide');
						displaySuccess(data.message, 5000);
					});
					$http.get($rootScope.baseURL + "/user")
					.success(function(data){
						$scope.user = data;
						$('#editAddressModal').modal('hide');
						
					})
					.error(function(response, status){
						displayValidation('Unable to get profile. Please refresh.', 5000);
						
					});
				})
				.error(function(response, status){
					displayValidation(response.message, 5000);
				});
			}
			else{
				$http.put($rootScope.baseURL + '/address', $scope.editAddress)
				.success(function(data){
					$timeout(function(){
						$('#addressModal').modal('hide');
						displaySuccess(data.message, 5000);
					});
					$http.get($rootScope.baseURL + "/user")
					.success(function(data){
						$scope.user = data;
						$('#editAddressModal').modal('hide');
						
					})
					.error(function(response, status){
						displayValidation('Unable to get profile. Please refresh.', 5000);
						
					});
				})
				.error(function(){
					displayValidation('Oops, looks like something went wrong. Please try again.', 5000);
				});
			}
				
			
		}
		
		$scope.edit = function(){
			var address = $scope.user.addresses[$scope.editLabel];
			if(!address){
				$scope.isNew = true;
				$scope.editAddress = {};
			}
			else{
				$scope.isNew = false;
				$scope.editAddress = angular.copy(address);
				$rootScope.selectSociety(address.society.societyId);
			}
		}
		
		$scope.deleteAddress = function(address){
			var r = confirm('Are you sure you want to delete this address?');
			if(!r){
				return;
			}
			
			$http.delete($rootScope.baseURL + '/address/' + address.addressId)
			.success(function(data){
				$timeout(function(){
					$('#addressModal').modal('hide');
					displaySuccess('Address has been removed.', 5000);
				});
				$http.get($rootScope.baseURL + "/user")
				.success(function(data){
					$scope.user = data;
					
				})
				.error(function(response, status){
					displayValidation('Unable to get profile. Please refresh.', 5000);
					
				});
			})
			.error(function(response, status){
				
				displayValidation(response.message, 5000);
			});
		}
		
		$scope.setDefaultAddress = function(address){
			$http.put($rootScope.baseURL + '/address/' + address.addressId + '/default')
			.success(function(data){
				$timeout(function(){
					displaySuccess('Default address changed successfully.', 5000);
				});
				$http.get($rootScope.baseURL + "/user")
				.success(function(data){
					$scope.user = data;
				})
				.error(function(response, status){
					displayValidation('Unable to get profile. Please refresh.', 5000);
					
				});
			})
			.error(function(response, status){
				displayValidation(response.message, 5000);
			});
		}
		
		$scope.userCopy = function(){
			$scope.editUser = angular.copy($scope.user);
		}
		
		$scope.editUserInfo = function(saveUser){
			if(saveUser){
				$http.put($rootScope.baseURL + '/user', $scope.user)
				.success(function(data){
					$timeout(function(){
						displaySuccess(data.message, 5000);
					});
				})
				.error(function(response, status){
					displayValidation('Oops, looks like something went wrong. Please try again.', 5000);
					$scope.user = angular.copy($scope.userBackup);
				});
			}
			else{
				$('#firstName').focus();
				$scope.userBackup = angular.copy($scope.user);
			}
		}
		
		$scope.cancelUserEdit = function(){
			$scope.editAccount = false;
		}
		
		$scope.getPaymentHistory = function(){
			$http.get($rootScope.baseURL + '/payment/list')
			.success(function(data){
				$scope.paymentHistory = data;
			})
			.error(function(response, status){
				displayValidation(response.message, 5000);
			});
		}
		$scope.getPaymentHistory();
	})
	.controller('paymentCtrl', function($scope, $http, $rootScope, $location, $timeout, $routeParams) {
		$scope.showMenu = false;
		$timeout(function(){
			$("[name='f1']").attr('action',$rootScope.paymentData.url);
			$("[name='f1']").submit();
		});
	})
	.controller('confirmOrderCtrl', function($scope, $http, $rootScope, $location, $timeout, $routeParams) {
		$scope.showMenu = true;
		//$scope.output=$routeParams;
		$scope.transactionId = $routeParams.subMenu;
		
		$http.get($rootScope.baseURL + '/payment/status?transactionId=' + $scope.transactionId)// + angular.toJson($scope.paymentStatusInput, false))
		.success(function(data){
			$scope.output = data;
			$rootScope.getWalletBalance();
		})
		.error(function(response, status, headers, config){
			$scope.output = response;
			
		});
	})
	.controller('loginCtrl', function($scope, $http, $location, $rootScope, $cookieStore, $timeout, $window, GooglePlus, $facebook) {
		$scope.showMenu = false;
		$http.defaults.headers.common['Authorization'] = ''; 
		$cookieStore.put('globals', {});
		$scope.user = {};
		
		$timeout(function(){
			$('#addressLabel').selectize();
		});
		
		$scope.getSocieties = function(){
			$rootScope.getSocieties('#society');
		}
		
		$scope.sendPost = function() {
			$scope.message='';
			$scope.errMessage='';
			
			$http.post($rootScope.baseURL + '/user', $scope.user)
			.success(function(data){
				var authdata = btoa($scope.user.email + ':' + $scope.user.password);
				$rootScope.user = data.user;
				$rootScope.globals = {
					currentUser: {
						authdata: authdata,
						user: data.user
					}
				};
	  
				$http.defaults.headers.common['Authorization'] = 'Basic ' + authdata; // jshint ignore:line
				$cookieStore.put('globals', $rootScope.globals);
				$('#signUpModal').modal('hide');
				$location.path("/home");
			})
			.error(function(response, status){
				if(status == 409){
					$scope.resetEmail = $scope.user.email;
				}
				displayValidation(response.message,5000);
			});
		}
		
		$scope.loginUser = function(){
			$scope.doLogin($scope.login.email, $scope.login.password);
		}

		$scope.doLogin = function(email, password, errCallback) {
			var authdata = btoa(email + ':' + password);
			$http.defaults.headers.common['Authorization'] = 'Basic ' + authdata;
			$http.get($rootScope.baseURL + '/user')
			.success(function(data){
				$rootScope.user = data;
				$rootScope.globals = {
					currentUser: {
						authdata: authdata,
						user: data
					}
				};
				$cookieStore.put('globals', $rootScope.globals);
				$location.path("/home");
				return false;
			})
			.error(function(response, status){
				if(errCallback){
					errCallback();
				}
				else{
					displayValidation(response.message,5000);
				}
				return false;
			});
		}
		
		$scope.forgotPassword = function() {
			
			$http.get($rootScope.baseURL + '/user/recoverPassword?email=' + $scope.forgotPasswordEmail)
			.success(function(data){
				$('#forgotPassowordModal').modal('hide');
				displaySuccess(data.message, 5000);
				
			})
			.error(function(response, status){
				displayValidation(response.message,5000);
			});
		}
		
		$scope.resetPasswordFunction = function() {
			$scope.reset = {"email": $scope.resetEmail, "password": $scope.resetPassword, "confirmPassword": $scope.resetConfirmPassword};
			$http.post($rootScope.baseURL + '/user/resetPassword', $scope.reset)
			.success(function(data){
				$('#resetPassowordModal').modal('hide');
				displaySuccess(data.message, 5000);
				
			})
			.error(function(response, status){
				displayValidation(response.message,5000);
			});
		}
		
		$scope.googleLogin = function () {
			GooglePlus.login().then(function (authResult) {

				GooglePlus.getUser().then(function (user) {
					$scope.googleUser = user;
					var password = "GOOGLE~~" + user.id;
					$scope.doLogin(user.email, password, $scope.googleSignup);
				});
			}, function (err) {
				console.log(err);
				displayValidation('Error logging in using Google.',5000);
			});
		};
		
		$scope.googleSignup = function(){
			$scope.login = {};
			$scope.user = {"email": $scope.googleUser.email, "firstName": $scope.googleUser.given_name, "lastName": $scope.googleUser.family_name, "type": 1, 
				"profilePicURL": $scope.googleUser.picture, "providerName": "GOOGLE", "providerUserId": $scope.googleUser.id};
			$scope.user.password = "GOOGLE~~" + $scope.googleUser.id;
			$scope.sendPost();
		}
		
		$scope.fbSignup = function(){
			$scope.login = {};
			$scope.user = {"email": $scope.fbUser.email, "firstName": $scope.fbUser.first_name, "lastName": $scope.fbUser.last_name, "type": 2, 
				"profilePicURL": $scope.fbPicture.data.url, "providerName": "FACEBOOK", "providerUserId": $scope.fbUser.id};
			$scope.user.password = "FACEBOOK~~" + $scope.fbUser.id;
			$scope.sendPost();
		}
	
		$scope.$on('fb.auth.authResponseChange', function() {
		  if($scope.fbLoginClicked && $facebook.isConnected()) {
			$facebook.api('/me?fields=id,email,first_name,last_name').then(function(user) {
				$facebook.api('/me/picture?width=150&height=150&redirect=false').then(function(picture) {
					$scope.fbUser = user;
					$scope.fbPicture = picture;
					var password = "FACEBOOK~~" + user.id;
					$scope.doLogin(user.email, password, $scope.fbSignup);
				});
			});
		  }
		});

		$scope.fbLogin = function() {
			$scope.fbLoginClicked=true;
			$facebook.login();
		};
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
	.controller('supportCtrl', function($scope, $http, $rootScope, $location, $timeout) {
		$scope.showMenu = true;
		$http.get($rootScope.baseURL + '/help/support')
		.success(function(data, status){
			$scope.support = data;
		})
		.error(function(data,status){
			displayValidation("Unknown error.",5000);
		});
		
	})
	.controller('faqCtrl', function($scope, $http, $rootScope, $location, $timeout, $routeParams) {
		$scope.showMenu = true;
		if($('.modal-backdrop')){
			$('.modal-backdrop').hide();
		}
		
		$http.get($rootScope.baseURL + '/help/faq')
		.success(function(data, status){
			$scope.faqs = data;
		})
		.error(function(data,status){
			displayValidation("Unknown error.",5000);
		});
		
		$timeout(function(){
			console.log($routeParams.subMenu);
			var offSet = 170;
			if($('#'+$routeParams.subMenu).offset()){
				var targetOffset = $('#'+$routeParams.subMenu).offset().top - offSet;
				console.log(targetOffset);
				$("#faqs-tab-content").getNiceScroll(0).doScrollTop(targetOffset, 0);
				//var mainNav = $('#main-nav');
				//$('html,body').animate({scrollTop:targetOffset}, 750);
				// if (mainNav.hasClass("open")) {
					// mainNav.css("height", "1px").removeClass("in").addClass("collapse");
					// mainNav.removeClass("open");
				// }
			}
		});
		
	})
	.controller('faq2Ctrl', function($scope, $http, $rootScope, $location, $timeout) {
		$scope.showMenu = true;
		if($('.modal-backdrop')){
			$('.modal-backdrop').hide();
		}
		
		$http.get($rootScope.baseURL + '/help/faq')
		.success(function(data, status){
			$scope.faqs = data;
		})
		.error(function(data,status){
			displayValidation("Unknown error.",5000);
		});
		
	})
	.controller('tncCtrl', function($scope, $http, $rootScope, $location, $timeout, $routeParams) {
		
		$timeout(function(){
			console.log($routeParams.subMenu);
			var offSet = 50;
			if($('#'+$routeParams.subMenu).offset()){
				var targetOffset = $('#'+$routeParams.subMenu).offset().top - offSet;
				$("#faqs-tab-content").getNiceScroll(0).doScrollTop(targetOffset, 0);
				//var mainNav = $('#main-nav');
				//$('html,body').animate({scrollTop:targetOffset}, 750);
				// if (mainNav.hasClass("open")) {
					// mainNav.css("height", "1px").removeClass("in").addClass("collapse");
					// mainNav.removeClass("open");
				// }
			}
		});
	})
	.controller('pricesCtrl', function($scope, $http, $rootScope, $location, $timeout) {
		$scope.showMenu = true;
		if($('.modal-backdrop')){
			$('.modal-backdrop').hide();
		}
		if($rootScope.categories == null){
			
			$http.get($rootScope.baseURL + '/order/category')
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
					
					displayValidation("Oops, looks like something went wrong. Please try again.",5000);
				}
			});
		}
		else{
			$scope.categories = $rootScope.categories;
			
		}
	})
	.filter("asDate", function () {
		return function (input) {
			return new Date(Date.parse(input));
		}
	})
	.filter("asTimePlusOneHour", function () {
		return function (input) {
			if(input){
			var dt = new Date(Date.parse(input));
			dt.setHours(dt.getHours() + 1);
			return dt.format('h TT');
			}
		}
	})
	.filter("asDateTimeString", function () {
		return function (input) {
			return (new Date(Date.parse(input))).format('dd mmm yyyy h:MM TT');
		}
	})
	.filter("asDateString", function () {
		return function (input) {
			return (new Date(Date.parse(input))).format('dd mmm yyyy');
		}
	})
	.filter("asTimeString", function () {
		return function (input) {
			return (new Date(Date.parse(input))).format('h:MM TT');
		}
	})
	.filter("asShortTimeString", function () {
		return function (input) {
			if(input){
				return (new Date(Date.parse(input))).format('h TT');
			}
		}
	})
	.filter("asShortDateTimeString", function () {
		return function (input) {
			if(input){
				return (new Date(Date.parse(input))).format('dd mmm h TT');
			}
		}
	});