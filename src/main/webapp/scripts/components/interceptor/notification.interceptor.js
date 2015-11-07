 'use strict';

angular.module('gpwRadarApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-gpwRadarApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-gpwRadarApp-params')});
                }
                return response;
            }
        };
    });
