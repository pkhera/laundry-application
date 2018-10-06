'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', [
	'ngRoute', 'ngCookies', 'ngAnimate',
	'myApp.filters', 'myApp.services', 'myApp.directives', 'myApp.controllers',
	'ui.directives','ui.filters','ui.bootstrap',
	'angular-bootstrap-select','angular-bootstrap-select.extra','datatables',
	'directive.loading',
	'googleplus', 'ngFacebook', 'chart.js'
])
.config( [
    '$compileProvider',
    function( $compileProvider )
    {   
        $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|intent|ftp|mailto|chrome-extension):/);
        // Angular before v1.2 uses $compileProvider.urlSanitizationWhitelist(...)
    }
]).
config(['$routeProvider',function($routeProvider) {
	$routeProvider.when('/home', {templateUrl: 'home.html', controller: 'orderCtrl',
		resolve: { 
            loadData: function($q,$http,$location,$rootScope){ 
				var deferred = $q.defer();
                $http.get($rootScope.baseURL + "/user")
				.success(function(data){
					$rootScope.user = data;
					deferred.resolve();
				})
				.error(function(response, status){
					if(status == 401){
						$location.path('/login');
					}
				});
				return deferred.promise;
            } 
          } 
	});
	//$routeProvider.when('/prices', {templateUrl: 'price.html', controller: 'categoriesCtrl'});
	$routeProvider.when('/landing', {templateUrl: 'landing.html', controller: 'landingCtrl'});
	$routeProvider.when('/profile/:subMenu', {templateUrl: 'myAccount.html', controller: 'profileCtrl'});
	$routeProvider.when('/payment', {templateUrl: 'payment.html', controller: 'paymentCtrl'});
	$routeProvider.when('/faq/:subMenu', {templateUrl: 'faqs.html', controller: 'faqCtrl'});
	$routeProvider.when('/faq', {templateUrl: 'faqs.html', controller: 'faqCtrl'});
	$routeProvider.when('/faq2', {templateUrl: 'faqs2.html', controller: 'faq2Ctrl'});
	$routeProvider.when('/tnc/:subMenu', {templateUrl: 'tnc.html', controller: 'tncCtrl'});
	$routeProvider.when('/privacyPolicy', {templateUrl: 'privacyPolicy.html'});
	$routeProvider.when('/orderHistory/:orderId', {templateUrl: 'orderHistory.html', controller: 'orderHistoryCtrl'});
	$routeProvider.when('/orderHistory', {templateUrl: 'orderHistory.html', controller: 'orderHistoryCtrl'});
	$routeProvider.when('/login', {templateUrl: 'login.html', controller: 'loginCtrl'});
	$routeProvider.when('/support', {templateUrl: 'support.html', controller: 'supportCtrl'});
	$routeProvider.when('/maintenance', {templateUrl: 'maintenance.html', controller: 'maintenanceCtrl'});
	$routeProvider.when('/prices', {templateUrl: 'prices.html', controller: 'pricesCtrl'});
	$routeProvider.when('/confirmOrder', {templateUrl: 'confirmOrder.html', controller: 'confirmOrderCtrl'});
	$routeProvider.when('/confirmOrder/:subMenu', {templateUrl: 'confirmOrder.html', controller: 'confirmOrderCtrl'});
	$routeProvider.otherwise({redirectTo: '/landing'});
}])
.config(['$facebookProvider', function($facebookProvider) {
    $facebookProvider.setAppId('1655509644660747').setPermissions(['email','user_friends']);
  }])
.config(['GooglePlusProvider', function(GooglePlusProvider) {
     GooglePlusProvider.init({
        clientId: '459483820919-cmlk183iei8868dkddfr3oknu3e90ebu.apps.googleusercontent.com',
        apiKey: 'AIzaSyDhE41S1Um7CNRLlQAe0i1QvXXTEyt9kuo',
		scopes: 'https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/userinfo.email'
     });
}])
.config(['ChartJsProvider', function (ChartJsProvider) {
    // Configure all charts
    ChartJsProvider.setOptions({
      colours: ['#FF5252', '#FF8A80'],
      responsive: true
    });
    // Configure all line charts
    ChartJsProvider.setOptions('Line', {
      datasetFill: true
    });
  }])
.run(function($rootScope, $cookieStore, $http, $timeout, $location, $window, $filter) {
	// Facebook Login
	(function(d, s, id) {
      var js, fjs = d.getElementsByTagName(s)[0];
      if (d.getElementById(id)) return;
      js = d.createElement(s); js.id = id;
      js.src = "//connect.facebook.net/en_US/sdk.js";
      fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));
    $rootScope.$on('fb.load', function() {
      $window.dispatchEvent(new Event('fb.load'));
    });
	$rootScope.baseURL = 'http://localhost:9001/Engine/rest';

	$rootScope.paytmURL = 'https://pguat.paytm.com';
	
	$rootScope.disableLoading = false;
	$rootScope.Math = window.Math;
	$rootScope.getUser = function(successCallback){
		$http.get($rootScope.baseURL + "/user")
		.success(function(data){
			$rootScope.user = data;
			if(successCallback){
				successCallback(data);
			}
		})
		.error(function(response, status){
			if(status == 401){
				$location.path('/login');
			}
			else{
				displayValidation('Unable to get your profile. Please refresh the page.', 5000);
			}
			
		});
	}

	$rootScope.loadSocieties = function(selectElement){
		$http.get($rootScope.baseURL + '/address/society')
		.success(function(data, status){
			$rootScope.societies = data;
			if(selectElement){
				$rootScope.getSocieties(selectElement);
			}
		})
		.error(function(response, status){
			if(status == 0){
				$location.path("/maintenance");
			}
		});
	}
	$rootScope.loadSocieties();
	
	$rootScope.globals = $cookieStore.get('globals');
	if(typeof $rootScope.globals === 'undefined' || typeof $rootScope.globals.currentUser === 'undefined' ){
		
	}
	else{
		$http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata;
		$rootScope.user = $rootScope.globals.currentUser.user;
	}
	
	$rootScope.changePasswordFunc = function(){
		overlay(1);
		$http.post($rootScope.baseURL + '/user/changePassword', $rootScope.changePassword)
		.success(function(data){
			$("#changePwdModal").modal("hide");
			overlay(0);
			displaySuccess("Password changed successfully",5000);
		})
		.error(function(response,status){
			overlay(0);
			displayValidation(response.message,5000);
		});
	}
	
	$rootScope.getSocieties = function(selectElement){
			$timeout(function(){
				var s = $(selectElement).selectize({
					plugins: ['no_results'],
					persist: false,
					openOnFocus: false,
					maxItems: 1,
					valueField: 'societyId',
					labelField: 'name',
					searchField: ['name', 'landmark', 'city', 'state', 'country'],
					sortField: [
						{field: 'name', direction: 'asc'},
						{field: 'city', direction: 'asc'}
					],
					options: $rootScope.societies,
					render: {
						item: function(item, escape) {
							//console.log("in Item");
							var name = item.name;
							var label = name;
							var caption = item.landmark;
							return '<div>' +
								'<span class="societyName">' + escape(item.name) + ', </span>' +
								(caption ? '<span class="caption">' + escape(caption) + '</span>' : '') +
							'</div>';
						},
						option: function(item, escape) {
							//console.log("in option");
							var name = item.name;
							var label = name;
							var caption = item.landmark + ", " + item.city + ", " + item.state + ", " + item.country;
							return '<div>' +
								'<span class="societyName">' + escape(item.name) + '</span>' +
								(caption ? '<span class="caption">' + escape(caption) + '</span>' : '') +
							'</div>';
						}
					}
				});
				$rootScope.societySelectize = s[0].selectize;
			});
		}
		
	$rootScope.selectSociety = function(societyId){
		console.log('selecting ' + societyId);
		$rootScope.societySelectize.setValue(societyId, true);
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
		
		$http.get($rootScope.baseURL + '/order/timeslots?societyId=2&date=' + sc.pickupDate)
			.success(function(data){
				sc.pickupTimeRanges = data;
				sc.dropTimeRanges = data;
				sc.pickupTime = sc.pickupTimeRanges[0].time;
				$rootScope.populatePickupTimeSlots(sc,sc.pickupTimeRanges,0);
				if(sc.dropTimeRanges.length > 8){
					
					sc.dropTime = sc.dropTimeRanges[8].time;
					sc.populateDropTimeSlots(sc,sc.dropTimeRanges,8);
				}
				else{
					sc.dropDate = $filter('date')(tomorrow,'dd MMM yyyy');
					$http.get($rootScope.baseURL + '/order/timeslots?societyId=2&date=' + sc.dropDate)
						.success(function(data){
							sc.dropTimeRanges = data;
							var index = 8 - sc.pickupTimeRanges.length;
							if(index < 0){
								index = 0;
							}
							sc.dropTime = sc.dropTimeRanges[index].time;
							$rootScope.populateDropTimeSlots(sc,sc.dropTimeRanges,index);
						});
				}
			});
	}

	$rootScope.pickupDateChanged = function(sc){
		$http.get($rootScope.baseURL + '/order/timeslots?societyId=2&date=' + $('#pickupDate').val())
		.success(function(data){
			sc.pickupTimeRanges = data;
			sc.pickupSelectize.clearOptions();
			angular.forEach(data, function(o) {
				sc.pickupSelectize.addOption(o);
			});
			sc.pickupSelectize.refreshOptions
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
		$http.get($rootScope.baseURL + '/order/timeslots?societyId=2&date=' + $('#dropDate').val())
		.success(function(data){
			sc.dropTimeRanges = data;
			
			sc.dropSelectize.clearOptions();
			angular.forEach(data, function(o) {
				sc.dropSelectize.addOption(o);
			});
			sc.dropSelectize.refreshOptions();
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
			o = o.slice(i);
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
				sc.dropSelectize.setValue(o[0].time, false);
				
				$('#orderAddress').selectpicker('refresh');
				
			});
		}
		
		$rootScope.getClass = function (path) {
		  if ($location.path().substr(0, path.length) === path) {
			return 'active';
		  } else {
			return '';
		  }
		}
		$rootScope.paymentPromo = {};
		$rootScope.validatePromoCode = function(amount, promo){
			$http.get($rootScope.baseURL + '/promotion?promotionCode=' + promo.promoCode + '&amount=' + amount)
			.success(function(data){
				promo.promoCodeMessage = data.message;
				promo.promoCodeErrorMessage = null;
				promo.applied = true;
				promo.promotionId = data.promotionId;
			})
			.error(function(response, status){
				if(status == 401){
					$location.path("/login");
				}
				else{
					promo.promoCodeMessage = null;
					promo.promoCodeErrorMessage = response.message;
				}
			});
		}
		
		$rootScope.walletBalance = 0;
		$rootScope.showLowBalanceNotification = true;
		$rootScope.getWalletBalance = function () {
			if($rootScope.user){
				$http.get($rootScope.baseURL + '/payment/balance')
				.success(function(data){
					$rootScope.walletBalance = data.balance;
					if(data.balance < 50){
						if($rootScope.showLowBalanceNotification){
							displayValidation("Your wallet balance is low. <a href='' data-target='#rechargeModal' data-toggle='modal' data-dismiss='modal'>Recharge now</a> to enjoy cashless transaction.");
							$rootScope.showLowBalanceNotification = false;
						}
					}
				})
				.error(function(response,status){
					// if(status == 401){
						// //$location.path("/login");
					// }
					// else{
						// displayValidation(response.message,5000);
					// }
				});
			}
		}
		$rootScope.getWalletBalance();
		
		$rootScope.rechargeAmount = 0;
		$rootScope.rechargeWallet = function(rechargeAmount){
			$http.get($rootScope.baseURL + '/payment?amount=' + rechargeAmount + ($rootScope.paymentPromo.promotionId ? ('&promotionId=' + $rootScope.paymentPromo.promotionId) : '') )
			.success(function(data){
				$rootScope.paymentData = data;
				$location.path("/payment");
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
});

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
	
	

