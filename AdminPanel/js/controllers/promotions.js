'use strict';

angular.module('myApp.controllers')
	.controller('promotionController', function($scope, $http, $rootScope, $location, $timeout) {
		$scope.getPromotions = function(){
			$http.get($rootScope.baseURL + '/promotion/list')
			.success(function(data){
				$scope.promotions = data;
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
		$scope.getPromotions();
		
		$scope.deletePromotion = function(promotion){
			var sure = confirm('Are you sure you want to delete ' + promotion.promotionCode + ' promotion?');
			if(!sure){
				return;
			}
			$http.delete($rootScope.baseURL + '/promotion?promotionId=' + promotion.promotionId)
			.success(function(data){
				$scope.getPromotions();
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
		
		$scope.updatePromotion = function(promotion){
			$scope.new = false;
			$scope.editPromotion = promotion;
		}
		
		$scope.savePromotion = function(){
			if($scope.editPromotion.validFrom){
				$scope.editPromotion.validFrom = $scope.editPromotion.validFrom + " 00:00 am";
			}
			if($scope.editPromotion.validTo){
				$scope.editPromotion.validTo = $scope.editPromotion.validTo + " 00:00 am";
			}
			if($scope.new){
				$http.post($rootScope.baseURL + '/promotion', $scope.editPromotion)
				.success(function(data){
					$scope.getPromotions();
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
				$http.put($rootScope.baseURL + '/promotion', $scope.editPromotion)
				.success(function(data){
					$scope.getPromotions();
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
	});
	