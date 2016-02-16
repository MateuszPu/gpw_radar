'use strict';

angular.module('gpwRadarApp')
    .factory('NewsMessage', function ($resource) {
        return $resource('api/news/message', {}, {
            'getLatestMessagesByType': { method: 'GET', url: 'api/news/message/latest', isArray: true},
        });
    });
