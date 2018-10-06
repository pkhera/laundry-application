'use strict';

angular.module('myApp.controllers')
	.controller('completedOrdersController', function($scope, $http, $rootScope, $location, $timeout) {
		$scope.max=5;
		if($rootScope.defaultStore){
			$rootScope.getCompletedOrders();
		}
		else{
			$rootScope.getStores(null, $rootScope.getCompletedOrders);
		}
		
		$scope.orderSelect = function(order){
			$scope.selectedOrder = order;
			$scope.orderReadonly = true;
			$('#editOrderModal').modal("show");
		}
		$scope.storeChangeListener = function(){
			$rootScope.getCompletedOrders();
		}
		
		$scope.orderStatusHistory = function(orderStatuses, currentStatus){
			$scope.orderStatuses = orderStatuses;
			$scope.currentStatus = currentStatus;
		}
		
		$scope.setFeedbackOrder = function(order){
			$scope.feedbackOrder = order;
			angular.forEach($scope.feedbackOrder.orderFeedback, function(feedback) {
				if(!feedback.rating){
					feedback.rating=0;
				}
				if(!feedback.orderId){
					feedback.orderId=$scope.feedbackOrder.orderId;
				}
			});
		}
	});
	