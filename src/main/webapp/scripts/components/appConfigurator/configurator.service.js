angular.module('gpwradarApp')
    .factory('AppConfigurator', function ($resource) {
        return $resource('', {}, {
            'getAllMethods': {method: 'GET', url: 'api/configurator/all/stock/details/parser/methods', isArray: true},
            'getCurrentMethod': {method: 'GET', url: 'api/configurator/current/stock/details/parser/method'}
        });
    });
