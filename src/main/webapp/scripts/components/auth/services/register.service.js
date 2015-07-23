'use strict';

angular.module('gpwradarApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


