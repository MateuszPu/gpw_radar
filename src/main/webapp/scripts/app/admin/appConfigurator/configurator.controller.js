angular.module('gpwradarApp')
	.controller('ConfiguratorController', function ($scope, $filter, AppConfigurator) {

		$scope.allMethods = AppConfigurator.getAllMethods();
    	$scope.fillDataStatus = AppConfigurator.getFillDataStatus();
    	
    	$scope.isButtonDisabled = function(name) {
    		for(var i = 0; i<$scope.fillDataStatus.length; i++) {
    			if($scope.fillDataStatus[i].type === name){
    				console.log(name);
    				return $scope.fillDataStatus[i].filled;
    			}
    		}
    	}
	    
		AppConfigurator.getCurrentMethod(function(response) {
    		$scope.selectedMethod = response.parserMethod;
    	});
    	
    	$scope.setMethod = function() {
    		AppConfigurator.setMethod({parserMethod: $scope.selectedMethod});
    	};
	});
