'use strict';

angular.module('gpwradarApp')
    .factory('StockTrends', function ($resource) {
        return $resource('', {}, {
            'getTrendsUp': { method: 'GET', url: 'api/stocks/trends/UP/days', isArray: true},
            'getTrendsDown': { method: 'GET', url: 'api/stocks/trends/DOWN/days', isArray: true}
        });
    });
