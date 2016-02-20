angular.module('gpwRadarApp')
    .controller('ChallengeController', function ($scope, NewsMessage) {

        $scope.endDate = moment();
        $scope.startDate = moment().subtract(30, "days");

        $scope.$watch('startDate', function(newVal, oldVal){
            $scope.smartTableSafeCopy = NewsMessage.getLatestMessagesDateRange({type: 'CHALLENGE', startDate: $scope.startDate, endDate: $scope.endDate});
            $scope.messages = [].concat($scope.smartTableSafeCopy);
        }, true);

        $scope.$watch('endDate', function(newVal, oldVal){
            $scope.smartTableSafeCopy = NewsMessage.getLatestMessagesDateRange({type: 'CHALLENGE', startDate: $scope.startDate, endDate: $scope.endDate});
            $scope.messages = [].concat($scope.smartTableSafeCopy);
        }, true);

    });
