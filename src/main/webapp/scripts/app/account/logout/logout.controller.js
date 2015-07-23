'use strict';

angular.module('gpwradarApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
