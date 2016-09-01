'use strict';

angular.module('gpwRadarApp')
    .factory('ChatMessage', function ($resource) {
        return $resource('', {}, {
            'getMessages': { method: 'GET', url: 'api/chat/last/10/messages', isArray: true}
        });
    });
