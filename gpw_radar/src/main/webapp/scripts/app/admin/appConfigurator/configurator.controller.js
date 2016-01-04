angular.module('gpwRadarApp')
	.controller('ConfiguratorController', function ($scope, $filter, $timeout, Websocket, AppConfigurator, StatusConfigurator, Stock) {


		//Method of stock details parser
		$scope.allMethods = AppConfigurator.getAllMethods();

		AppConfigurator.getCurrentMethod(function(response) {
    		$scope.selectedMethod = response.parserMethod;
    	});

    	$scope.setMethod = function() {
    		AppConfigurator.setMethod({parserMethod: $scope.selectedMethod});
    	};

		//Fill database with data
    	$scope.getStockTickersCount = function() {
    		Stock.getAllStockTickers(function(response) {
    			$scope.stockTickersCount = response.length;
    		});
    	};

    	$scope.getFillDataStatus = function() {
    		AppConfigurator.getFillDataStatus(function(response){
    			$scope.fillDataStatus = response;
    		});
    	};

    	$scope.getStockTickersCount();
    	$scope.getFillDataStatus();
    	$scope.step = 0;

    	$scope.isDisabled = function(name) {
    		if(name === 'STOCK_DETAILS' || name === 'STOCK_DETAILS_FIVE_MINUTES') {
    			return (!$scope.isDisabled('STOCK') || $filter('getByType')($scope.fillDataStatus, name));
    		}
    		if(name === 'STOCK_FINANCE_EVENTS') {
    			return !($scope.isDisabled('STOCK') && $filter('getByType')($scope.fillDataStatus, 'STOCK_DETAILS') && !$filter('getByType')($scope.fillDataStatus, name));
    		}
    		return $filter('getByType')($scope.fillDataStatus, name);
    	};


    	$scope.fillDatabase = function(name) {
    		//$scope.getFillStep();
    		$scope.typeName = name;
    		AppConfigurator.fillDatabaseWithData({type: name}, function(response) {
    			$scope.updatingDB = false;
    			//$scope.getFillDataStatus();
    		});
    	};

    	$scope.getFillStep = function() {
    		$scope.updatingDB = true;
    	    (function tick() {
    	    	StatusConfigurator.getStep().then(function(response){
	    			  $scope.timeInMiliSeconds = 1000;
		              $scope.promiseTimeout = $timeout(tick, $scope.timeInMiliSeconds);
		              $scope.step = response;

		              if(!$scope.updatingDB) {
		                  $timeout.cancel($scope.promiseTimeout);
		              }
    	    	});
    	    })();
    	}
	});

angular.module('gpwRadarApp').filter('getByType', function() {
	return function(input, type) {
		var len=input.length;
			for (var i=0; i<len; i++) {
				if (input[i].type == type) {
					return input[i].filled;
				}
			}
		return null;
	}
});
