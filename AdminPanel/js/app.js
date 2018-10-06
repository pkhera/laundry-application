'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', [
	'ngRoute', 'ngCookies', 'ngAnimate',
	'myApp.filters', 'myApp.services', 'myApp.directives', 'myApp.controllers',
	'ui.directives','ui.filters','ui.bootstrap',
	'angular-bootstrap-select','angular-bootstrap-select.extra','datatables',
	'directive.loading'
])
.config(['$routeProvider', function($routeProvider, $httpProvider) {
	$routeProvider.when('/home', {templateUrl: 'home.html', controller: 'orderHistoryCtrl'});
	$routeProvider.when('/profile', {templateUrl: 'profile.html', controller: 'profileCtrl'});
	$routeProvider.when('/download', {templateUrl: 'download.html', controller: 'downloadCtrl'});
	$routeProvider.when('/login', {templateUrl: 'login.html', controller: 'loginCtrl'});
	$routeProvider.when('/deliveryPersons', {templateUrl: 'deliveryPersons.html', controller: 'deliveryPersonController'});
	$routeProvider.when('/ironPersons', {templateUrl: 'ironPersons.html', controller: 'ironPersonController'});
	$routeProvider.when('/promotion', {templateUrl: 'promotions.html', controller: 'promotionController'});
	$routeProvider.when('/payment', {templateUrl: 'payment.html', controller: 'paymentController'});
	$routeProvider.when('/stores', {templateUrl: 'stores.html', controller: 'storeController'});
	$routeProvider.when('/societies', {templateUrl: 'societies.html', controller: 'societyController'});
	$routeProvider.when('/admins', {templateUrl: 'admins.html', controller: 'adminController'});
	$routeProvider.when('/completedOrders', {templateUrl: 'completedOrders.html', controller: 'completedOrdersController'});
	$routeProvider.when('/maintenance', {templateUrl: 'maintenance.html', controller: 'maintenanceCtrl'});
	$routeProvider.otherwise({redirectTo: '/home'});
}])
.run(function($rootScope, $cookieStore, $http, $filter, $timeout, $location ) {
	$rootScope.baseURL = 'http://localhost:9001/Engine/rest';

	$rootScope.disableLoading = false;
	
	$rootScope.globals = $cookieStore.get('globals');
	if(typeof $rootScope.globals === 'undefined' || typeof $rootScope.globals.currentAdmin === 'undefined' ){
		
	}
	else{
		$http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentAdmin.authdata;
		$rootScope.admin = $rootScope.globals.currentAdmin.admin;
	}

	
	var smsServiceURL = $rootScope.baseURL + '/adminservice/sms';
	$http.get(smsServiceURL).success(function(data){
		$rootScope.smsService = data;
	})
	$rootScope.setSMSService = function(){
		var enable = $rootScope.smsService.enabled;
		var url = smsServiceURL + '?enable=' + enable;
		//console.log($rootScope.smsService.enabled);
		$http.post(url)
		.success(function(data){
			$rootScope.smsService = data;
		})
		.error(function(response, status){
			$rootScope.smsService = response;
			displayValidation(response.message, 5000);
		});
	}
	
	var emailServiceURL = $rootScope.baseURL + '/adminservice/email';
	$http.get(emailServiceURL).success(function(data){
		$rootScope.emailService = data;
	})
	$rootScope.setEmailService = function(){
		var enable = $rootScope.emailService.enabled;
		var url = emailServiceURL + '?enable=' + enable;
		//console.log($rootScope.emailService.enabled);
		$http.post(url)
		.success(function(data){
			$rootScope.emailService = data;
		})
		.error(function(response, status){
			$rootScope.emailService = response;
			displayValidation(response.message, 5000);
		});
	}
	
	var deliveryManagerURL = $rootScope.baseURL + '/adminservice/deliveryManager';
	$http.get(deliveryManagerURL).success(function(data){
		$rootScope.deliveryManager = data;
	})
	$rootScope.setDeliveryManager = function(){
		var enable = $rootScope.deliveryManager.enabled;
		var url = deliveryManagerURL + '?enable=' + enable;
		//console.log($rootScope.smsService.enabled);
		$http.post(url)
		.success(function(data){
			$rootScope.deliveryManager = data;
		})
		.error(function(response, status){
			$rootScope.deliveryManager = response;
			displayValidation(response.message, 5000);
		});
	}
	$rootScope.flag = {};
	$rootScope.openSelectOnFocus = function(id){
		
		$rootScope.flag[id]=1;
		$('[data-id=' + id + ']').on('focus', function(e){
			if($rootScope.flag[id] == 1){
				$rootScope.flag[id] = -1;
				
				$('[data-id=' + id + ']').trigger('click');
				if($rootScope.flag[id] == 0){
					$('[data-id=' + id + ']').trigger('click');
				}
			}
			else{
				e.preventDefault();
                e.stopPropagation();
			}
		});
		$('[data-id=' + id + ']').on('blur', function(e){
			$rootScope.flag[id] = 1;
		});
		$('[data-id=' + id + ']').on('click', function(e){
			if($rootScope.flag[id] == -1 || $rootScope.flag[id] == 1){
				e.preventDefault();
                e.stopPropagation();
				$rootScope.flag[id] = 0
			}
			else{
				$rootScope.flag[id] = 2;
			}
		});
		
	}
	
	$rootScope.getSocieties = function(callback){
		$http.get($rootScope.baseURL + '/admin/society')
		.success(function(data){
			callback(data);
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
	
	$rootScope.getAllOrders = function(orderStatuses){
		if(orderStatuses == null){
			orderStatuses='0,1,2,3,4,5,7'
		}
		var url = $rootScope.baseURL + '/admin/order?statuses=' + orderStatuses + '&storeId=' + $rootScope.defaultStore.storeId;
		var pendingOrdersURL = $rootScope.baseURL + '/admin/order?statuses=0,1,7&storeId=' + $rootScope.defaultStore.storeId;
		var receivedOrdersURL = $rootScope.baseURL + '/admin/order?statuses=2,3&storeId=' + $rootScope.defaultStore.storeId;
		var ironedOrdersURL = $rootScope.baseURL + '/admin/order?statuses=4,5&storeId=' + $rootScope.defaultStore.storeId;
		
		$http.get(pendingOrdersURL)
		.success(function(data){
			$rootScope.pendingOrders = data;
		})
		.error(function(response, status){
			if(status == 401){
				$location.path("/login");
			}
			else{
				displayValidation("Something went wrong - " + response.message, 5000);
			}
		});
		
		$http.get(receivedOrdersURL)
		.success(function(data){
			$rootScope.receivedOrders = data;
		})
		.error(function(response, status){
			if(status == 401){
				$location.path("/login");
			}
			else{
				displayValidation("Something went wrong - " + response.message, 5000);
			}
		});
		
		$http.get(ironedOrdersURL)
		.success(function(data){
			$rootScope.ironedOrders = data;
		})
		.error(function(response, status){
			if(status == 401){
				$location.path("/login");
			}
			else{
				displayValidation("Something went wrong - " + response.message, 5000);
			}
		});
	}
	
	$rootScope.getCompletedOrders = function(){
		var completedOrdersURL = $rootScope.baseURL + '/admin/order?statuses=6,10&storeId=' + $rootScope.defaultStore.storeId;
		
		$http.get(completedOrdersURL)
		.success(function(data){
			$rootScope.completedOrders = data;
		})
		.error(function(response, status){
			if(status == 401){
				$location.path("/login");
			}
			else{
				displayValidation("Something went wrong - " + response.message, 5000);
			}
		});
	}

	$rootScope.setDefaultStore = function(store, storeChangeListener){
		if(store){
			$rootScope.defaultStore = store;
			$cookieStore.put('defaultStoreId', $rootScope.defaultStore.storeId);
		}
		if(storeChangeListener){
			storeChangeListener();
		}
	}
	
	$rootScope.getAdmins = function(callback){
		$http.get($rootScope.baseURL + '/admin/adminusers')
		.success(function(data){
			callback(data);
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
	
	$rootScope.getUsers = function(callback){
		$http.get($rootScope.baseURL + '/admin/user/list')
		.success(function(data){
			callback(data);
		})
		.error(function(response,status){
			if(status == 401){
				$location.path("/login");
			}
			else{
				displayValidation(response, 5000);
			}
		});
	}
	
	$rootScope.getStores = function(scope, callback, callbackParam){
		$http.get($rootScope.baseURL + '/admin/store')
		.success(function(data){
			$rootScope.defaultStores = data;
			if(!$rootScope.defaultStore){
				var savedStoreId = $cookieStore.get('defaultStoreId');
				angular.forEach(data, function(store) {
					if(store.storeId == savedStoreId){
						$rootScope.defaultStore = store;
						return;
					}
				});
				if(!$rootScope.defaultStore){
					$rootScope.defaultStore = data[0];
				}
			}
			if(scope){
				scope.stores=data;
			}
			if(callback){
				if(callbackParam == null){
					callback();
				}
				else{
					callback(callbackParam);
				}
			}
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
	
	$rootScope.getTimeRanges = function(pickupDate){
		console.log(pickupDate);
		//Get today's date
		var todaysDate = new Date();
		var inputDate = new Date(pickupDate);

		//call setHours to take the time out of the comparison
		var h=7;
		if((new Date(pickupDate)).setHours(0,0,0,0) == todaysDate.setHours(0,0,0,0));
		{
			if(inputDate.getMinutes() > 29){
				h = inputDate.getHours()+1;
			}
			else{
				h = inputDate.getHours();
			}
			if(h<7){
				h=7;
			}
		}
		
		var timeRanges = [];
		
		for(var i=0;h<22;h++,i++){
			// console.log(i);
			if(h==12){
				timeRanges[i]={"time" : "12:00 PM" , "timeSlotText":"12 PM - 1 PM"};
			}
			else{
				timeRanges[i]={"time" : (h%12)+":00 " + ((h < 12 ? "AM" : "PM")), "timeSlotText":(h%12)+" " + (h < 12 ? "AM" : "PM") + " - " + ((h+1)%12)+" " + ((h+1) < 12 ? "AM" : "PM")};
			}
		}
		// console.log(timeRanges);
		return timeRanges;
	}	
	
	$rootScope.setPickupDropDateTime = function(sc){
		var now = new Date();
		var tomorrow = new Date();
		tomorrow.setDate(tomorrow.getDate() + 1);
		tomorrow.setHours(0,0,0,0);
		if(now.getHours() >= 20){
			sc.pickupDate = $filter('date')(tomorrow,'dd MMM yyyy');
			sc.dropDate = $filter('date')(tomorrow,'dd MMM yyyy');
			sc.dropTimeRanges = $rootScope.getTimeRanges( tomorrow );
			sc.pickupTimeRanges = $rootScope.getTimeRanges(tomorrow);
		}
		else if (now.getHours() > 16){
			sc.pickupDate = $filter('date')(now,'dd MMM yyyy');
			sc.dropDate = $filter('date')(tomorrow,'dd MMM yyyy');
			sc.dropTimeRanges = $rootScope.getTimeRanges( now );
			sc.pickupTimeRanges = $rootScope.getTimeRanges(tomorrow);
		}
		else{
			sc.pickupDate = $filter('date')(now,'dd MMM yyyy');
			sc.dropDate = $filter('date')(now,'dd MMM yyyy');
			sc.dropTimeRanges = $rootScope.getTimeRanges( now );
			sc.pickupTimeRanges = $rootScope.getTimeRanges(now);
		}
		
		$http.get($rootScope.baseURL + '/admin/order/timeslots?societyId=2&date=' + sc.pickupDate)
			.success(function(data){
				sc.pickupTimeRanges = data;
				sc.dropTimeRanges = data;
				sc.pickupTime = sc.pickupTimeRanges[0].time;
				$rootScope.populatePickupTimeSlots(sc,sc.pickupTimeRanges,0);
				if(sc.dropTimeRanges.length > 4){
					
					sc.dropTime = sc.dropTimeRanges[4].time;
					sc.populateDropTimeSlots(sc,sc.dropTimeRanges,4);
				}
				else{
					sc.dropDate = $filter('date')(tomorrow,'dd MMM yyyy');
					$http.get($rootScope.baseURL + '/admin/order/timeslots?societyId=2&date=' + sc.dropDate)
						.success(function(data){
							sc.dropTimeRanges = data;
							sc.dropTime = sc.dropTimeRanges[0].time;
							$rootScope.populateDropTimeSlots(sc,sc.dropTimeRanges,0);
						});
				}
			});
	}

	$rootScope.pickupDateChanged = function(sc){
		$http.get($rootScope.baseURL + '/admin/order/timeslots?societyId=2&date=' + $('#pickupDate').val())
		.success(function(data){
			sc.pickupTimeRanges = data;
			sc.pickupSelectize.clearOptions();
			angular.forEach(data, function(o) {
				sc.pickupSelectize.addOption(o);
			});
			//sc.pickupSelectize.refreshOptions
		})
		.error(function(response, status){
			
			sc.pickupTimeRanges = $rootScope.getTimeRanges( new Date($('#pickupDate').val()) );
			
			sc.pickupSelectize.clearOptions();
			angular.forEach(sc.pickupTimeRanges, function(o) {
				sc.pickupSelectize.addOption(o);
			});
			sc.pickupSelectize.refreshOptions();
		});
		
	}
	$rootScope.dropDateChanged = function(sc){
		$http.get($rootScope.baseURL + '/admin/order/timeslots?societyId=2&date=' + $('#dropDate').val())
		.success(function(data){
			sc.dropTimeRanges = data;			
			sc.dropSelectize.clearOptions();
			angular.forEach(data, function(o) {
				sc.dropSelectize.addOption(o);
			});
			//sc.dropSelectize.refreshOptions();
		})
		.error(function(response, status){
			sc.dropTimeRanges = $rootScope.getTimeRanges( new Date($('#dropDate').val()) );
			
			sc.dropSelectize.clearOptions();
			angular.forEach($scope.dropTimeRanges , function(o) {
				sc.dropSelectize.addOption(o);
			});
			sc.dropSelectize.refreshOptions();
		});
		
	}

	$rootScope.populatePickupTimeSlots = function(sc,o,i){
			$timeout(function(){
					var s = $("#pickupTime").selectize({
						persist: false,
						maxItems: 1,
						valueField: 'time',
						labelField: 'timeSlotText',
						searchField: ['time', 'timeSlotText'],
						
						options: o,
						render: {
							item: function(item, escape) {
								var name = item.timeSlotText;
								var label = name;
								var caption = null;
								var timeslotClass = "";
								if(item.availibility < 5 ){
									timeslotClass = "textOrange";
								}
								else if(item.availibility < 1){
									timeslotClass = "textRed";
								}
								return '<div>' +
									'<span class="timeSlot">' + escape(name) + ' </span>' +
									(caption ? '<span class="caption">' + escape(caption) + '</span>' : '') +
								'</div>';
							},
							option: function(item, escape) {
								var name = item.timeSlotText;
								var label = name;
								var caption = "Available";
								var timeslotClass = "";
								if(item.availibility < 5 ){
									caption = "Fast Filling";
									timeslotClass = "textOrange";
								}
								else if(item.availibility < 1){
									caption = "Unavailable";
									timeslotClass = "textRed";
								}
								return '<div>' +
									'<span class="timeSlot">' + escape(name) + '</span>' +
									(caption ? '<span class="caption">' + escape(caption) + '</span>' : '') +
								'</div>';
							}
						}
					});
					sc.pickupSelectize = s[0].selectize;
					sc.pickupSelectize.setValue(o[i].time, false);
				$('#orderAddress').selectpicker('refresh');
				
			});
		}
		
		$rootScope.populateDropTimeSlots = function(sc,o,i){
			$timeout(function(){
					var s = $("#dropTime").selectize({
						persist: false,
						maxItems: 1,
						valueField: 'time',
						labelField: 'timeSlotText',
						searchField: ['time', 'timeSlotText'],
						
						options: o,
						render: {
							item: function(item, escape) {
								var name = item.timeSlotText;
								var label = name;
								var caption = null;
								var timeslotClass = "";
								if(item.availibility < 5 ){
									timeslotClass = "textOrange";
								}
								else if(item.availibility < 1){
									timeslotClass = "textRed";
								}
								return '<div>' +
									'<span class="timeSlot">' + escape(name) + ' </span>' +
									(caption ? '<span class="caption">' + escape(caption) + '</span>' : '') +
								'</div>';
							},
							option: function(item, escape) {
								var name = item.timeSlotText;
								var label = name;
								var caption = "Available";
								var timeslotClass = "";
								if(item.availibility < 5 ){
									caption = "Fast Filling";
									timeslotClass = "textOrange";
								}
								else if(item.availibility < 1){
									caption = "Unavailable";
									timeslotClass = "textRed";
								}
								return '<div>' +
									'<span class="timeSlot">' + escape(name) + '</span>' +
									(caption ? '<span class="caption">' + escape(caption) + '</span>' : '') +
								'</div>';
							}
						}
					});
					sc.dropSelectize=s[0].selectize;
				sc.dropSelectize.setValue(o[i].time, false);
				
				$('#orderAddress').selectpicker('refresh');
				
			});
		}
		
	
})
 .directive('bootstrapSwitch', [
	function() {
		return {
			restrict: 'A',
			require: '?ngModel',
			link: function(scope, element, attrs, ngModel) {
				element.bootstrapSwitch();

				element.on('switchChange.bootstrapSwitch', function(event, state) {
					if (ngModel) {
						scope.$apply(function() {
							ngModel.$setViewValue(state);
						});
					}
				});

				scope.$watch(attrs.ngModel, function(newValue, oldValue) {
					if (newValue) {
						element.bootstrapSwitch('state', true, true);
					} else {
						element.bootstrapSwitch('state', false, true);
					}
				});
			}
		};
	}
]);


angular.module('directive.loading', [])
.directive('loading',   ['$http' ,function ($http)
{
	return {
		restrict: 'A',
		link: function (scope, elm, attrs)
		{
			scope.isLoading = function () {
				return $http.pendingRequests.length > 0;
			};

			scope.$watch(scope.isLoading, function (v)
			{
				if(v){
					if(!scope.$root.disableLoading){
						elm.show();
					}
				}else{
					elm.hide();
				}
			});
		}
	};

}]);

angular.module("angular-bootstrap-select", [])
    .directive("selectpicker",
        [
            "$timeout",
            function($timeout) {
                return {
                    restrict: "A",
                    require: ["?ngModel", "?ngCollection"],
                    compile: function(tElement, tAttrs, transclude) {
                        console.log("init bootstrap-select");
                        tElement.selectpicker();

                        if (angular.isUndefined(tAttrs.ngModel)) {
                            throw new Error("Please add ng-model attribute!");
                        } else if (angular.isUndefined(tAttrs.ngCollection)) {
                            throw new Error("Please add ng-collection attribute!");
                        }

                        return function(scope, element, attrs, ngModel) {
                            if (angular.isUndefined(ngModel)){
                                return;
                            }

                            scope.$watch(attrs.ngModel, function(newVal, oldVal) {
                                if (newVal !== oldVal) {
                                    $timeout(function() {
                                        console.log("value selected - " + element.val());
										element.selectpicker("refresh");
                                        element.selectpicker("val", element.val());
                                    });
                                }
                            });

                            scope.$watch(attrs.ngCollection, function(newVal, oldVal) {
                                if (newVal && newVal !== oldVal) {
                                    $timeout(function() {
                                        console.log("select collection updated");
                                        element.selectpicker("refresh");
                                    });
                                }
                            });

                            ngModel.$render = function() {
                                element.selectpicker("val", ngModel.$viewValue || "");
                            };

                            ngModel.$viewValue = element.val();
                        };
                    }
                }
            }
        ]
    );