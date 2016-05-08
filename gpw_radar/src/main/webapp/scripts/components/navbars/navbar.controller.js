'use strict';

angular.module('gpwRadarApp')
    .controller('NavbarController', function ($scope, $location, $state, Auth, Principal, Account) {
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.isInRole = Principal.isInRole;
        $scope.$state = $state;
        $scope.account = Account.get();

        $scope.logout = function () {
            Auth.logout(true);
            $state.go('home');
        };
    });
