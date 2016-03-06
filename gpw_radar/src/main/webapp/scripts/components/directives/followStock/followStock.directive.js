angular.module('gpwRadarApp')
    .directive('followStock', function() {
        return {
            scope: {
                id: '=',
                updateData: '&'
            },
            controller: 'FollowStockController',
            restrict: 'E',
            templateUrl: 'scripts/components/directives/followStock/followStock-template.html',
        }
    });
