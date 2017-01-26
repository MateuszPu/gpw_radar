'use strict';

angular.module('gpwRadarApp')
    .controller('StockTrendsUpController', function ($scope, $http, $filter, StockTrends, ParseLinks) {
        $scope.stocksTrendsUp10Days = [];
        $scope.stocksTrendsUp30Days = [];
        $scope.stocksTrendsUp60Days = [];
        $scope.stocksTrendsUp90Days = [];

        $scope.getStocksTrendsUp = function(page, days){
        	StockTrends.getTrendsUp({page: page, per_page: 5, days: days}, function(result, headers){
        		switch(days) {
                    case 10:
                        $scope.stocksTrendsUp10Days = result;
                        $scope.trendsUp10DaysLinks = ParseLinks.parse(headers('link'));
                        $scope.trendsUp10DaysPage = page;
                        break;
                    case 30:
                        $scope.stocksTrendsUp30Days = result;
                        $scope.trendsUp30DaysLinks = ParseLinks.parse(headers('link'));
                        $scope.trendsUp30DaysPage = page;
                        break;
                    case 60:
                        $scope.stocksTrendsUp60Days = result;
                        $scope.trendsUp60DaysLinks = ParseLinks.parse(headers('link'));
                        $scope.trendsUp60DaysPage = page;
                        break;
                    case 90:
                        $scope.stocksTrendsUp90Days = result;
                        $scope.trendsUp90DaysLinks = ParseLinks.parse(headers('link'));
                        $scope.trendsUp90DaysPage = page;
                        break;
                    default:
        	            break;
        	    }
        	});
        };

        $scope.loadPage = function (page, days) {
            switch (parseInt(days)) {
                case 10:
                    $scope.getStocksTrendsUp(page, 10);
                    break;
                case 30:
                    $scope.getStocksTrendsUp(page, 30);
                    break;
                case 60:
                    $scope.getStocksTrendsUp(page, 60);
                    break;
                case 90:
                    $scope.getStocksTrendsUp(page, 90);
                    break;
            }
        };

        $scope.loadAll = function(){
        	$scope.loadPage(1, 10);
        	$scope.loadPage(1, 30);
        	$scope.loadPage(1, 60);
        	$scope.loadPage(1, 90);

        };

        $scope.loadAll();

    });
