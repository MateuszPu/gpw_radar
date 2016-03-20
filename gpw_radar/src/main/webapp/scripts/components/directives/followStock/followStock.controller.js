angular.module('gpwRadarApp')
    .controller('FollowStockController', function ($scope, $rootScope, $filter, StocksFollowed) {
    	$scope.followStock = function(stock) {
            StocksFollowed.followStock(stock.stockId).then(function(data) {
                $rootScope.stocksFollowedByUser.push(stock);
            });
    	};

    	$scope.stopFollowStock = function(stock) {
            StocksFollowed.stopFollowStock(stock.stockId).then(function(data) {
                $filter('removeById')($rootScope.stocksFollowedByUser, stock.stockId);
            });
    	};

    	$scope.isFollowed = function(stock) {
    		var found = $filter('getById')($rootScope.stocksFollowedByUser, stock.stockId);
    		return found;
    	};
    });
