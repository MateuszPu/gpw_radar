angular.module('gpwRadarApp')
	.directive('breadcrumbsWithTranslateValues', function() {
	    return {
	        scope: {
	        	translatePath: '@',
	        	translateValuesPath: '@',
	        	dupa: '@'
	        },
	        restrict: 'E',
	        templateUrl: 'scripts/components/directives/breadcrumbs/breadcrumbs-translate-values-template.html'
	    }
	})
    .directive('breadcrumbs', function() {
        return {
            scope: {
            	translatePath: '@'
            },
            restrict: 'E',
            templateUrl: 'scripts/components/directives/breadcrumbs/breadcrumbs-template.html'
        }
    });