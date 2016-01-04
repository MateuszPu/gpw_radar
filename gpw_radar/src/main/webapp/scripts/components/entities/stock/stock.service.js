'use strict';

angular.module('gpwRadarApp')
    .factory('Stock', function ($resource, DateUtils) {
        return $resource('api/stocks/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method: 'PUT' },
            'getAllStockTickers': { method: 'GET', url: 'api/get/tickers', isArray:true},
            'getAllFetchStockIndicators': { method: 'GET', url: 'api/stocks/get/all', isArray: true}
        });
    });
