'use strict';

angular.module('gpwRadarApp')
    .factory('StockFiveMinutesDetails', function ($resource, DateUtils) {
        return $resource('', {}, {
            'getTodaysDetails': { method: 'GET', url:'api/5/minutes/stocks/most/active'}
        });
    });
