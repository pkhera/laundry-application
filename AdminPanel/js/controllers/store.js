'use strict';

angular.module('myApp.controllers')
	.controller('storeController', function($scope, $http, $rootScope, $location, $timeout) {
		$scope.getStores = function(){
			$http.get($rootScope.baseURL + '/admin/store')
			.success(function(data){
				$scope.stores = data;
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
		//$scope.getStores();
		$rootScope.getStores($scope);
			
		$scope.closeStore = function(store){
			var sure = confirm('Are you sure you want to close ' + store.name + ' store?');
			if(!sure){
				return;
			}
			$http.delete($rootScope.baseURL + '/admin/store?storeId=' + store.storeId)
			.success(function(data){
				$rootScope.getStores($scope);
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
		
		$scope.updateStore = function(store){
			$scope.new = false;
			$scope.editStore = store;
		}
		
		$scope.saveStore = function(){
			if($scope.new){
				$http.post($rootScope.baseURL + '/admin/store', $scope.editStore)
				.success(function(data){
					$rootScope.getStores($scope);
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
				$http.put($rootScope.baseURL + '/admin/store', $scope.editStore)
				.success(function(data){
					$rootScope.getStores($scope);
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
	