angular.module('gpwradarApp')
	.controller('ConfiguratorController', function ($scope, $filter, AppConfigurator) {

		$scope.allMethods = AppConfigurator.getAllMethods();
    	AppConfigurator.getCurrentMethod(function(data) {
    		$scope.selectedMethod = data.parserMethod;
    	});
	});
