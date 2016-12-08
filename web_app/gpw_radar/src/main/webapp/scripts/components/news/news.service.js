'use strict';

angular.module('gpwRadarApp')
    .factory('NewsMessage', function ($resource) {
        return $resource('api/news/message', {}, {
            'getLatestMessagesByType': { method: 'GET', url: 'api/news/message/latest', isArray: true},
            'getLatestMessagesDateRange': { method: 'GET', url: 'api/news/message/range', isArray: true}
        });
    })
    .service("NewsMessageFormat", function () {
        this.getDateFormat = function () {
            return "YYYY-MM-DD HH:mm:ss.SSS"
        }
    });
