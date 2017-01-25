'use strict';

angular.module('gpwRadarApp')
    .factory('ChatMessage', function ($resource) {
        return $resource('', {}, {
            'getMessages': { method: 'GET', url: 'api/chat/messages', isArray: true}
        });
    });
