angular.module('gpwRadarApp')
    .directive('simplePagination', function() {
        return {
            scope: {
            	loadPage: '&',
                page: '=',
                links: '='
            },
            restrict: 'E',
            templateUrl: 'scripts/components/directives/pagination/simple-pagination-template.html'
        }
    })
    .directive('advancedPagination', function() {
        return {
            scope: {
                loadPage: '&',
                page: '=',
                links: '=',
                daysParameter: '@'
            },
            restrict: 'E',
            templateUrl: 'scripts/components/directives/pagination/advanced-pagination-template.html'
        }
    });
