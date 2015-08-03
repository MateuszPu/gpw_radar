'use strict';

angular.module('gpwradarApp')
    .controller('StockController', function ($scope, Stock, ParseLinks) {
        $scope.stocks = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Stock.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.stocks = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Stock.get({id: id}, function(result) {
                $scope.stock = result;
                $('#deleteStockConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Stock.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteStockConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.stock = {ticker: null, stockName: null, stockShortName: null, id: null};
        };
    });
