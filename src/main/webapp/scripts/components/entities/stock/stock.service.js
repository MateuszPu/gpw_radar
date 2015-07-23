'use strict';

angular.module('gpwradarApp')
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
            'update': { method:'PUT' }
        });
    });
