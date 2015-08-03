'use strict';

angular.module('gpwradarApp')
    .controller('StockFinanceEventController', function ($scope, StockFinanceEvent, ParseLinks) {
        $scope.stockFinanceEvents = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            StockFinanceEvent.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.stockFinanceEvents = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            StockFinanceEvent.get({id: id}, function(result) {
                $scope.stockFinanceEvent = result;
                $('#deleteStockFinanceEventConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            StockFinanceEvent.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteStockFinanceEventConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.stockFinanceEvent = {date: null, message: null, id: null};
        };
    });
