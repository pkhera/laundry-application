'use strict';

angular.module('myApp.controllers')
	.controller('adminController', function($scope, $http, $rootScope, $location, $timeout) {
		$timeout(function(){
			
			$scope.labels = [{label:"Home"},{label:"Office"},{label:"Other"}];
			$rootScope.openSelectOnFocus('label');
			$rootScope.openSelectOnFocus('society');
			$rootScope.openSelectOnFocus('store');
			
		});
		
		
		
		$scope.getAdmins = function(){
			$http.get($rootScope.baseURL + '/admin/adminusers')
			.success(function(data){
				$scope.admins = data;
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
		$scope.getAdmins();
		if(!$rootScope.defaultStores){
			$rootScope.getStores();
		}
		
		$scope.getUsersCallBack = function(users){
			$scope.users = users;
		}
		$rootScope.getUsers($scope.getUsersCallBack);
		
		$rootScope.getStores($scope,null);
		
		$scope.getSocietiesCallback = function(societies){
			$scope.societies = societies;
			$('#society').selectpicker('refresh');
		}
		$rootScope.getSocieties($scope.getSocietiesCallback);
		
		
		$timeout(function(){
			$('#store').selectpicker('refresh');
		});
		
		
		$scope.deleteAdmin = function(admin){
			var sure = confirm('Are you sure you want to delete ' + admin.firstName + ' ' + admin.lastName + '?');
			if(!sure){
				return;
			}
			$http.delete($rootScope.baseURL + '/admin?userId=' + admin.userId)
			.success(function(data){
				$scope.getAdmins();
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
		
		$scope.updateAdmin = function(admin){
			$scope.new = false;
			$scope.editAdmin = admin;
		}
		
		$scope.updateUser = function(user){
			$scope.new = false;
			$scope.editUser = user;
		}
		
		$scope.saveAdmin = function(){
			if($scope.new){
				$http.post($rootScope.baseURL + '/admin', $scope.editAdmin)
				.success(function(data){
					$scope.getAdmins();
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
				$http.put($rootScope.baseURL + '/admin', $scope.editAdmin)
				.success(function(data){
					$scope.getAdmins();
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
		
		$scope.saveUser = function(){
			if($scope.new){
				$http.post($rootScope.baseURL + '/admin/user', $scope.editUser)
				.success(function(data){
					$rootScope.getUsers($scope.getUsersCallBack);
					$timeout(function(){
						$('#userModal').modal('hide');
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
				$http.put($rootScope.baseURL + '/admin/user', $scope.editUser)
				.success(function(data){
					$rootScope.getUsers($scope.getUsersCallBack);
					$timeout(function(){
						$('#userModal').modal('hide');
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
		
		$scope.addressLabelChange = function(){
			if(!$scope.editUser.addresses){
				$scope.editUser.addresses = {"Home" : {"label": "Home"}, "Office" : {"label": "Office"}, "Other" : {"label": "Other"}}
			}
			if(!$scope.editUser.addresses[$scope.addressLabel]){
				$scope.editUser.addresses[$scope.addressLabel] = {"label": $scope.addressLabel};
			}
		}
		
		$scope.addCredit = function(user){
			var input = {'userId':user.userId, 'amount':$scope.creditAmount, 'masterPassword': $scope.masterPassword};
			$scope.masterPassword = '';
			$http.post($rootScope.baseURL + '/admin/user/addCredit', input)
			.success(function(data){			
				$rootScope.getUsers($scope.getUsersCallBack);
				$('#addCreditModal').modal('hide');
				displayValidation(data.message, 5000);
			})
			.error(function(response, status){
				displayValidation(response.message, 5000);
			});
		}
		$scope.creditAmount = 0;
		
		$scope.selectUser = function(user){
			$scope.selectedUser = user;
		}
	});
	