'use strict';

angular.module('gpwradarApp')
    .controller('StockFinanceEventDetailController', function ($scope, $rootScope, $stateParams, entity, StockFinanceEvent) {
        $scope.stockFinanceEvent = entity;
        $scope.load = function (id) {
            StockFinanceEvent.get({id: id}, function(result) {
                $scope.stockFinanceEvent = result;
            });
        };
        $rootScope.$on('gpwradarApp:stockFinanceEventUpdate', function(event, result) {
            $scope.stockFinanceEvent = result;
        });
    });
