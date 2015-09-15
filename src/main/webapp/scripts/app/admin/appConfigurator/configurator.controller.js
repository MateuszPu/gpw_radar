angular.module('gpwradarApp')
	.controller('ConfiguratorController', function ($scope, $filter, AppConfigurator) {

		$scope.allMethods = AppConfigurator.getAllMethods();
    	
		AppConfigurator.getCurrentMethod(function(response) {
    		$scope.selectedMethod = response.parserMethod;
    	});
    	
    	$scope.setMethod = function() {
    		AppConfigurator.setMethod({parserMethod: $scope.selectedMethod});
    	};
	});
