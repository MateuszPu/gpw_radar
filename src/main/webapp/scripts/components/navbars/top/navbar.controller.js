'use strict';

angular.module('gpwradarApp')
    .controller('NavbarController', function ($scope, $location, $state, Auth, Principal) {
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.isInRole = Principal.isInRole;
        $scope.$state = $state;
        
        $scope.logout = function () {
            Auth.logout(true);
            $state.go('home');
        };
    });
