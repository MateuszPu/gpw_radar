'use strict';

angular.module('gpwRadarApp')
    .controller('StockTrendsDownController', function ($scope, $http, $filter, StockTrends, ParseLinks, StocksFollowed) {
        $scope.stocksTrendsDown10Days = [];
        $scope.stocksTrendsDown30Days = [];
        $scope.stocksTrendsDown60Days = [];
        $scope.stocksTrendsDown90Days = [];
        $scope.stocksFollowedByUser = [];

        $scope.getStocksTrendsDown = function (page, days) {
            StockTrends.getTrendsDown({page: page, per_page: 5, days: days}, function (result, headers) {
                switch (days) {
                    case 10:
                        $scope.stocksTrendsDown10Days = result;
                        $scope.trendsDown10DaysLinks = ParseLinks.parse(headers('link'));
                        $scope.trendsDown10DaysPage = page;
                        break;
                    case 30:
                        $scope.stocksTrendsDown30Days = result;
                        $scope.trendsDown30DaysLinks = ParseLinks.parse(headers('link'));
                        $scope.trendsDown30DaysPage = page;
                        break;
                    case 60:
                        $scope.stocksTrendsDown60Days = result;
                        $scope.trendsDown60DaysLinks = ParseLinks.parse(headers('link'));
                        $scope.trendsDown60DaysPage = page;
                        break;
                    case 90:
                        $scope.stocksTrendsDown90Days = result;
                        $scope.trendsDown90DaysLinks = ParseLinks.parse(headers('link'));
                        $scope.trendsDown90DaysPage = page;
                        break;
                    default:
                        break;
                }
            });
        };

        $scope.getStocksFollowedByUser = function () {
        	StocksFollowed.getStocksFollowed().then(function (data) {
                $scope.stocksFollowedByUser = data;
            });
        };

        $scope.loadPage = function (page, days) {
            switch (parseInt(days)) {
                case 10:
                    $scope.getStocksTrendsDown(page, 10);
                    break;
                case 30:
                    $scope.getStocksTrendsDown(page, 30);
                    break;
                case 60:
                    $scope.getStocksTrendsDown(page, 60);
                    break;
                case 90:
                    $scope.getStocksTrendsDown(page, 90);
                    break;
            }
        };

        $scope.loadAll = function () {
            $scope.loadPage(1, 10);
            $scope.loadPage(1, 30);
            $scope.loadPage(1, 60);
            $scope.loadPage(1, 90);

            $scope.getStocksFollowedByUser();
        };
        
        $scope.loadAll();

        $scope.followStock = function (id) {
        	StocksFollowed.followStock(id);
            $scope.loadAll();
        };

        $scope.stopFollowStock = function (id) {
        	StocksFollowed.stopFollowStock(id);
            $scope.loadAll();
        };

        $scope.isFollowed = function (id) {
            var found = $filter('getById')($scope.stocksFollowedByUser, id);
            return found;
        };
    });
