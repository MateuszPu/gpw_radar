angular.module('gpwRadarApp')
    .controller('RecommendationsController', function ($scope, NewsMessage) {

        $scope.endDate = moment();
        $scope.startDate = moment().subtract(30, "days"),

        $scope.datePicker = {
            startDate: $scope.startDate,
            endDate: $scope.endDate
        };

        $scope.opts = {
            ranges: {
                'Last 7 Days': [moment().subtract(6, 'days'), moment()],
                'Last 30 Days': [moment().subtract(30, 'days'), moment()]
            }
        };
    });
