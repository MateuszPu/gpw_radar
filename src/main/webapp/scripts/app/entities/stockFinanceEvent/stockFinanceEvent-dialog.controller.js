'use strict';

angular.module('gpwradarApp').controller('StockFinanceEventDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'StockFinanceEvent',
        function($scope, $stateParams, $modalInstance, entity, StockFinanceEvent) {

        $scope.stockFinanceEvent = entity;
        $scope.load = function(id) {
            StockFinanceEvent.get({id : id}, function(result) {
                $scope.stockFinanceEvent = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('gpwradarApp:stockFinanceEventUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.stockFinanceEvent.id != null) {
                StockFinanceEvent.update($scope.stockFinanceEvent, onSaveFinished);
            } else {
                StockFinanceEvent.save($scope.stockFinanceEvent, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
