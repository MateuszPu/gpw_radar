angular.module('gpwradarApp')
    .factory('ApplicationStatus', function ($resource) {
        return $resource('', {}, {
            'isUpdating': { method: 'GET', url: 'api/is/updating'},
            'getStatusOfStocks': { method: 'GET', url: 'api/get/top/by/date'}
        });
    });
