angular.module('gpwRadarApp')
    .controller('StockController', function ($scope, $http, $filter, Stock, StocksFollowed) {
        $scope.stocksIndicatorsWithStocks = [];
        $scope.stocksFollowedByUser = [];

    	$scope.getAllStocks = function(){
    		Stock.getAllFetchStockIndicators(function(result){
    			$scope.smartTableSafeCopy = result;
    			$scope.stocksIndicatorsWithStocks = [].concat($scope.smartTableSafeCopy);
    			$scope.getStocksFollowedByUser();
    		});
    	};
    	
        $scope.getStocksFollowedByUser = function(){
        	StocksFollowed.getStocksFollowed().then(function(data) {
	    	    $scope.stocksFollowedByUser = data;
	    	});
        };

        $scope.loadPage = function() {
        	$scope.getAllStocks();
        };

        $scope.getAllStocks();

    	$scope.followStock = function(id){
    		StocksFollowed.followStock(id);
    		$scope.loadPage();
    	};

    	$scope.stopFollowStock = function(id){
    		StocksFollowed.stopFollowStock(id);
    		$scope.loadPage();
    	};

    	$scope.isFollowed = function(id){
    		var found = $filter('getById')($scope.stocksFollowedByUser, id);
    		return found;
    	};
    });