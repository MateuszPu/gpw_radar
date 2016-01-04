'use strict';

angular.module('gpwRadarApp')
    .factory('StockFiveMinutesDetails', function ($resource, DateUtils) {
        return $resource('', {}, {
            'getTodaysDetails': { method: 'GET', url:'api/get/5/minutes/stocks/today', isArray: true},
        });
    });
