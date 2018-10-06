'use strict';

angular.module('myApp.controllers')
	.controller('paymentController', function($scope, $http, $rootScope, $location, $timeout) {
		$scope.getPayments = function(){
			$http.get($rootScope.baseURL + '/admin/payment/list')
			.success(function(data){
				$scope.payments = data;
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Error : " + response.message, 5000);
				}
			});
		}
		$scope.getPayments();
		
		$scope.completePaymentTransaction = function(payment){
			$http.get($rootScope.baseURL + '/admin/payment/status?transactionId=' + payment.paymentTransactionId + '&userId=' + payment.userId)
			.success(function(data){
				$scope.getPayments();
			})
			.error(function(response,status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					displayValidation("Error : " + response.message, 5000);
				}
			});
		}
	});
	
	