angular.module('gpwRadarApp')
    .controller('EspiController', function ($scope, NewsMessage) {

        $scope.endDate = new Date();
        $scope.startDate = new Date($scope.endDate.getUTCFullYear(), $scope.endDate.getUTCMonth()-1, $scope.endDate.getUTCDate());

        $scope.datePicker = {startDate: $scope.startDate, endDate: $scope.endDate};

    });
