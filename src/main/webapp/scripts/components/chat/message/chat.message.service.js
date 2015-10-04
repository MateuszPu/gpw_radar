'use strict';

angular.module('gpwradarApp')
    .factory('ChatMessage', function ($resource) {
        return $resource('', {}, {
            'getMessages': { method: 'GET', url: 'api/chat/last/10/messages', isArray: true}
        });
    });
