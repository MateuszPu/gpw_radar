angular.module('gpwradarApp')
    .directive('trendingTable', function() {
        return {
            scope: {
                trendingStocks: '=',
                startFollow: '&',
                stopFollow: '&',
                stockFollowedyUser: '&',
                isFollowed: '&'
            },
            restrict: 'E',
            templateUrl: 'scripts/components/directives/tables/table-template.html'
        }
    });
