'use strict';

angular.module('gpwRadarApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


