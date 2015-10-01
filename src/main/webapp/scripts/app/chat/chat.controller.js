'use strict';

angular.module('gpwradarApp')
    .controller('ChatController', function ($scope) {
    	
    	$scope.names = [];
    	
    	$scope.connect = function(){
    		console.log('connect');
    	}
    	
    	$scope.disconnect = function(){
    		console.log('disconnect');
    	}
    	
    	$scope.sendName = function() {
    		console.log($scope.name);
    		$scope.names.push($scope.name);
    	}

    });
