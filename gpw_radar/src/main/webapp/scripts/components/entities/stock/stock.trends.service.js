'use strict';

angular.module('gpwRadarApp')
    .factory('StockTrends', function ($resource) {
        return $resource('', {}, {
            'getTrendsUp': { method: 'GET', url: 'api/stocks/trends/UP/days', isArray: true},
            'getTrendsDown': { method: 'GET', url: 'api/stocks/trends/DOWN/days', isArray: true}
        });
    });
