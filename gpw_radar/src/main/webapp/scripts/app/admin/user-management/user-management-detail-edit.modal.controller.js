'use strict';

angular.module('gpwRadarApp')
    .controller('UserDetailEditController', function($scope, $modalInstance, user, User, Language) {

    	$scope.user = user;
    	console.log(user);

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.authorities = ["ROLE_USER", "ROLE_ADMIN"];
        Language.getAll().then(function (languages) {
            $scope.languages = languages;
        });

        $scope.save = function () {
            User.update($scope.user,
	            function () {
	        		$modalInstance.close();
	            });
        };

    });
