angular.module('gpwRadarApp')
    .controller('FollowStockController', function ($scope, $filter, StocksFollowed) {
        $scope.stocksFollowedByUser = [];

        $scope.getStocksFollowedByUser = function(){
            StocksFollowed.getStocksFollowed().then(function(data) {
                $scope.stocksFollowedByUser = data;
            });
        };
        $scope.getStocksFollowedByUser();

    	$scope.followStock = function(id){
    		StocksFollowed.followStock(id).then(function(data) {
                $scope.getStocksFollowedByUser();
                $scope.updateData();
            });
    	};

    	$scope.stopFollowStock = function(id){
    		StocksFollowed.stopFollowStock(id).then(function(data) {
                $scope.getStocksFollowedByUser();
                $scope.updateData();
            });
    	};

    	$scope.isFollowed = function(id){
    		var found = $filter('getById')($scope.stocksFollowedByUser, id);
    		return found;
    	};
    });
