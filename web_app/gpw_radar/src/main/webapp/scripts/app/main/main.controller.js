'use strict';

angular.module('gpwRadarApp')
    .controller('MainController', function ($scope, StockStatistic) {

        StockStatistic.getStocksDownCount().then(function (response) {
            $scope.downCount = response;
        });

        StockStatistic.getStocksUpCount().then(function (response) {
            $scope.upCount = response;
        });

        StockStatistic.getStocksNoChangeCount().then(function (response) {
            $scope.noChangeCount = response;
        });

        StockStatistic.getMostFollowedStocks().then(function (response) {
            $scope.mostFollowedStocks = response;
        });

        StockStatistic.getFiveLatestNewsMessages().then(function (response) {
            $scope.topFiveNewsMessages = response;
        });
    });
