'use strict';

angular.module('gpwRadarApp')
    .controller('UserManagementController', function ($scope, $modal, User, ParseLinks, Language) {
        $scope.users = [];

        $scope.page = 0;
        $scope.loadAll = function () {
            User.query({page: $scope.page, per_page: 20}, function (result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.users = result;
            });
        };

        $scope.loadPage = function (page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.setActive = function (user, isActivated) {
            user.activated = isActivated;
            User.update(user, function () {
                $scope.loadAll();
                $scope.clear();
            });
        };

        $scope.showUpdate = function(login) {
        	User.get({login: login}, function (result) {
                var modalInstance = $modal.open({
                    templateUrl: 'scripts/app/admin/user-management/user-management-detail-edit.modal.html',
                    controller: 'UserDetailEditController',
                    size: 'lg',
                    resolve: {
                    	user: function() {
                            return result;
                        }
                    }
                });
                modalInstance.result.then(function () {
            		$scope.loadAll();
        	    });
        	});
        };

    });
