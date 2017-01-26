angular.module('gpwRadarApp')
    .controller('StockController', function ($scope, Stock) {
        $scope.stocksFetchIndicator = [];

        Stock.getAllFetchStockIndicators(function(result){
            $scope.smartTableSafeCopy = result;
            $scope.stocksFetchIndicator = [].concat($scope.smartTableSafeCopy);
        });
    });
