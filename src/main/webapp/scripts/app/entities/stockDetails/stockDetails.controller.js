'use strict';

angular.module('gpwradarApp')
    .controller('StockDetailsController', function ($scope, StockDetails, ParseLinks) {
        $scope.stockDetailss = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            StockDetails.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.stockDetailss = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            StockDetails.get({id: id}, function(result) {
                $scope.stockDetails = result;
                $('#deleteStockDetailsConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            StockDetails.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteStockDetailsConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.stockDetails = {date: null, openPrice: null, maxPrice: null, minPrice: null, closePrice: null, volume: null, averageVolume10Days: null, averageVolume30Days: null, volumeRatio10: null, volumeRatio30: null, percentReturn: null, id: null};
        };
    });
