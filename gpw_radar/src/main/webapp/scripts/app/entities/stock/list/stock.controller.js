angular.module('gpwRadarApp')
    .controller('StockController', function ($scope, Stock) {
        $scope.stocksFetchIndicator = [];

    	$scope.getAllStocks = function(){
    		Stock.getAllFetchStockIndicators(function(result){
    			$scope.smartTableSafeCopy = result;
    			$scope.stocksFetchIndicator = [].concat($scope.smartTableSafeCopy);
    		});
    	};

        $scope.getAllStocks();
    });
