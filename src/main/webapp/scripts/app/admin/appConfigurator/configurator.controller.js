angular.module('gpwradarApp')
	.controller('ConfiguratorController', function ($scope, $filter, AppConfigurator) {

		$scope.allMethods = AppConfigurator.getAllMethods();
    	$scope.fillDataStatus = AppConfigurator.getFillDataStatus();
    	
    	$scope.isDisabled = function(name) {
    		if(name === 'STOCK_DETAILS') {
    			return (!$scope.isDisabled('STOCK') || $filter('getByType')($scope.fillDataStatus, name));
    		}
    		if(name === 'STOCK_FINANCE_EVENTS') {
//    			console.log(!$scope.isDisabled('STOCK_DETAILS'));
//    			console.log($filter('getByType')($scope.fillDataStatus, name));
//    			return ($scope.isDisabled('STOCK_DETAILS') || $filter('getByType')($scope.fillDataStatus, name));
    			return !$scope.isDisabled('STOCK_DETAILS') && !$filter('getByType')($scope.fillDataStatus, name);
    		}
    		return $filter('getByType')($scope.fillDataStatus, name);
    	};
    	
    	$scope.fillDatabase = function(name) {
    		AppConfigurator.fillDatabaseWithData({type: name}, function(result) {
    			console.log(result);
    		});
    	}
	    
		AppConfigurator.getCurrentMethod(function(response) {
    		$scope.selectedMethod = response.parserMethod;
    	});
    	
    	$scope.setMethod = function() {
    		AppConfigurator.setMethod({parserMethod: $scope.selectedMethod});
    	};
	});

angular.module('gpwradarApp').filter('getByType', function() {
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