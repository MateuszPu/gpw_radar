angular.module('gpwRadarApp')
    .directive('iboxContent', function() {
        return {
            scope: {
                tableData: '=',
                startDate: '=',
                endDate: '=',
                perPage: '='
            },
            compile: function(element, attrs){
                console.log(attrs);
                if (!attrs.perPage) { attrs.perPage = '8'; }
            },
            restrict: 'E',
            templateUrl: 'scripts/components/directives/ibox/content/ibox-content.html',
        }
    });
