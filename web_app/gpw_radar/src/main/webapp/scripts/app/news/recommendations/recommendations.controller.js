angular.module('gpwRadarApp')
    .controller('RecommendationsController', function ($scope, NewsMessage, NewsMessageFormat) {

        $scope.endDate = moment();
        $scope.startDate = moment().subtract(30, "days");
        $scope.formatOfDate = NewsMessageFormat.getDateFormat()

        $scope.$watch('startDate', function(newVal, oldVal){
            $scope.smartTableSafeCopy = NewsMessage.getLatestMessagesDateRange({type: 'RECOMMENDATIONS',
                startDate: $scope.startDate.format($scope.formatOfDate),
                endDate: $scope.endDate.format($scope.formatOfDate)});
            $scope.messages = [].concat($scope.smartTableSafeCopy);
        }, true);

        $scope.$watch('endDate', function(newVal, oldVal){
            $scope.smartTableSafeCopy = NewsMessage.getLatestMessagesDateRange({type: 'RECOMMENDATIONS',
                startDate: $scope.startDate.format($scope.formatOfDate),
                endDate: $scope.endDate.format($scope.formatOfDate)});
            $scope.messages = [].concat($scope.smartTableSafeCopy);
        }, true);
    });
