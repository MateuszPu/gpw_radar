angular.module('gpwRadarApp')
    .controller('PapController', function ($scope, NewsMessage) {

        $scope.endDate = moment();
        $scope.startDate = moment().subtract(30, "days");

        $scope.$watch('startDate', function(newVal, oldVal){
            $scope.smartTableSafeCopy = NewsMessage.getLatestMessagesDateRange({type: 'PAP', startDate: $scope.startDate, endDate: $scope.endDate});
            $scope.messages = [].concat($scope.smartTableSafeCopy);
        }, true);

        $scope.$watch('endDate', function(newVal, oldVal){
            $scope.smartTableSafeCopy = NewsMessage.getLatestMessagesDateRange({type: 'PAP', startDate: $scope.startDate, endDate: $scope.endDate});
            $scope.messages = [].concat($scope.smartTableSafeCopy);
        }, true);

    });
