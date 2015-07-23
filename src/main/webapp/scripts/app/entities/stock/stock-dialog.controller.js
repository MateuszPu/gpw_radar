'use strict';

angular.module('gpwradarApp').controller('StockDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Stock',
        function($scope, $stateParams, $modalInstance, entity, Stock) {

        $scope.stock = entity;
        $scope.load = function(id) {
            Stock.get({id : id}, function(result) {
                $scope.stock = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('gpwradarApp:stockUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.stock.id != null) {
                Stock.update($scope.stock, onSaveFinished);
            } else {
                Stock.save($scope.stock, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
