'use strict';

angular.module('gpwradarApp')
    .controller('StockDetailsDetailController', function ($scope, $rootScope, $stateParams, entity, StockDetails) {
        $scope.stockDetails = entity;
        $scope.load = function (id) {
            StockDetails.get({id: id}, function(result) {
                $scope.stockDetails = result;
            });
        };
        $rootScope.$on('gpwradarApp:stockDetailsUpdate', function(event, result) {
            $scope.stockDetails = result;
        });
    });
