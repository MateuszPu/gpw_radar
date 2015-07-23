'use strict';

angular.module('gpwradarApp')
    .controller('StockDetailController', function ($scope, $rootScope, $stateParams, entity, Stock) {
        $scope.stock = entity;
        $scope.load = function (id) {
            Stock.get({id: id}, function(result) {
                $scope.stock = result;
            });
        };
        $rootScope.$on('gpwradarApp:stockUpdate', function(event, result) {
            $scope.stock = result;
        });
    });
