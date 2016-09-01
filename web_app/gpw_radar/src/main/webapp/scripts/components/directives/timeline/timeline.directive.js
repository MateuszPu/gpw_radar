angular.module('gpwRadarApp')
    .directive('timelineBox', function() {
        return {
            scope: {
                newsMessages: '=',
                type: '@'
            },
            restrict: 'E',
            templateUrl: 'scripts/components/directives/timeline/timeline-template.html'
        }
    });
