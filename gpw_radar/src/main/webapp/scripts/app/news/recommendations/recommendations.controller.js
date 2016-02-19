angular.module('gpwRadarApp')
    .controller('RecommendationsController', function ($scope, NewsMessage) {

        $scope.endDate = moment();
        $scope.startDate = moment().subtract(30, "days");
        $scope.itemsByPage = 8;

        $scope.$watch('startDate', function(newVal, oldVal){
            $scope.smartTableSafeCopy = NewsMessage.getLatestMessagesDateRange({type: 'RECOMMENDATIONS', startDate: $scope.startDate, endDate: $scope.endDate});
            $scope.messages = [].concat($scope.smartTableSafeCopy);
        }, true);

        $scope.$watch('endDate', function(newVal, oldVal){
            $scope.smartTableSafeCopy = NewsMessage.getLatestMessagesDateRange({type: 'RECOMMENDATIONS', startDate: $scope.startDate, endDate: $scope.endDate});
            $scope.messages = [].concat($scope.smartTableSafeCopy);
        }, true);

    })
