angular.module('gpwRadarApp')
    .directive('followStock', function() {
        return {
            scope: {
                stock: '='
            },
            controller: 'FollowStockController',
            restrict: 'E',
            templateUrl: 'scripts/components/directives/followStock/followStock-template.html',
        }
    });
