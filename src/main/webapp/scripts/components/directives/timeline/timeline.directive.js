angular.module('gpwradarApp')
    .directive('timelineBox', function() {
        return {
            scope: {
                newsMessages: '='
            },
            restrict: 'E',
            templateUrl: 'scripts/components/directives/timeline/timeline-template.html'
        }
    });
