'use strict';

angular.module('myApp.controllers')
	.controller('societyController', function($scope, $http, $rootScope, $location, $timeout) {
		$scope.getSocietiesCallback = function(societies){
			$scope.societies = societies;
		}
		$rootScope.getSocieties($scope.getSocietiesCallback);
		
		if(!$rootScope.defaultStores){
			$rootScope.getStores($scope);
		}
		
		$timeout(function(){
			$('#store').selectpicker('refresh');
		});
		
		$scope.deleteSociety = function(society){
			var sure = confirm('Are you sure you want to delete ' + society.name + ' society?');
			if(!sure){
				return;
			}
			$http.delete($rootScope.baseURL + '/admin/society?societyId=' + society.societyId)
			.success(function(data){
				$rootScope.getSocieties($scope.getSocietiesCallback);
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
		
		$scope.updateSociety = function(society){
			$scope.new = false;
			$scope.editSociety = society;
		}
		
		$scope.saveSociety = function(){
			if($scope.new){
				$http.post($rootScope.baseURL + '/admin/society', $scope.editSociety)
				.success(function(data){
					$rootScope.getSocieties($scope.getSocietiesCallback);
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
				$http.put($rootScope.baseURL + '/admin/society', $scope.editSociety)
				.success(function(data){
					$rootScope.getSocieties($scope.getSocietiesCallback);
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
	