'use strict';

angular.module('gpwRadarApp')
    .controller('UserDetailEditController', function($scope, $modalInstance, user, User) {

    	$scope.user = user;
    	console.log(user);

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
        
//        $scope.clear = function () {
//            $scope.user = {
//                id: null, login: null, firstName: null, lastName: null, email: null,
//                activated: null, langKey: null, createdBy: null, createdDate: null,
//                lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
//                resetKey: null, authorities: null
//            };
//            $scope.editForm.$setPristine();
//            $scope.editForm.$setUntouched();
//        };
        
        $scope.save = function () {
            User.update($scope.user,
	            function () {
	        		$modalInstance.close();
	            });
        };

    });
