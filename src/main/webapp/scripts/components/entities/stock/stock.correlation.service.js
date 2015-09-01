'use strict';

angular.module('gpwradarApp')
    .factory('Correlation', function ($resource) {
        return $resource('api/statistic/stock/correlation', {}, {
            'computeCorrelation': { method: 'GET', isArray: true},
            'getCorrelationTypes': { method: 'GET', url: 'api/statistic/all/type/correlation', isArray: true},
            'getStepOfCorrelation': { method: 'GET', url: 'api/statistic/stock/correlation/step'}
        });
    })
    .factory('CorrelationStep', function($http){
    	var getStepServ = {
    			getStep: function() {
    				var promise = $http.get('api/statistic/stock/correlation/step').then(function (response){
    					return response.data;
    				})
    				return promise;
    			}
    	}
    	return getStepServ;
    })