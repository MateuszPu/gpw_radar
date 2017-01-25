angular.module('gpwRadarApp')
    .factory('SmaIndicator', function ($resource) {
        return $resource('api/technical/indicators', {}, {
            'getSmaCrossover': { method: 'GET', url: 'api/technical/indicators/sma/crossover', isArray: true},
            'getPriceCrossSma': { method: 'GET', url: 'api/technical/indicators/price/cross/sma', isArray: true}
        });
    });
