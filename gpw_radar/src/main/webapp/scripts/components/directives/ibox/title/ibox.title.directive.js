angular.module('gpwRadarApp')
    .directive('iboxTitle', function() {
        return {
            scope: {
                translatePath: '@'
            },
            restrict: 'E',
            templateUrl: 'scripts/components/directives/ibox/title/ibox-title.html',
        }
    });
