'use strict';

angular.module('gpwradarApp').controller('StockDetailsDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'StockDetails',
        function($scope, $stateParams, $modalInstance, entity, StockDetails) {

        $scope.stockDetails = entity;
        $scope.load = function(id) {
            StockDetails.get({id : id}, function(result) {
                $scope.stockDetails = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('gpwradarApp:stockDetailsUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.stockDetails.id != null) {
                StockDetails.update($scope.stockDetails, onSaveFinished);
            } else {
                StockDetails.save($scope.stockDetails, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
