'use strict';

angular.module('gpwradarApp')
    .config(function(ngstompProvider){
            ngstompProvider.url('/socket').class(SockJS);
	})
    .controller('ChatController', function ($scope, $window, ngstomp, ChatMessage) {
    	
        $scope.messages = ChatMessage.getMessages({page: 0}); 
        
        ngstomp.send('/app/webchat/user/login');
        ngstomp.subscribe('/webchat/user',  newUserConnected);
        function newUserConnected(user) {
        	$scope.users = JSON.parse(user.body);
        };
	    
	    $scope.sendDataToWS = function(message) {
	        ngstomp.send('/app/webchat/send/message', message);
	    };
	    
	    ngstomp.subscribe('/webchat/recive',  messageFromServer);
        function messageFromServer(message) {
        	$scope.messages.push(JSON.parse(message.body));
        	if($scope.messages.length > 10) {
        		$scope.messages.splice(0, 1);
        	}
        };
        
        $window.onbeforeunload = function (evt) {
        	$scope.logout();
        }
        
	    $scope.$on('$destroy', function() {
	    	$scope.logout();
	    });
	    
	    $scope.logout = function() {
	    	ngstomp.send('/app/webchat/user/logout');
	    	ngstomp.unsubscribe('/webchat/recive', unsubscribe);
	    	ngstomp.unsubscribe('/webchat/user', unsubscribe);
	    }
    	
    	function unsubscribe() {
    		console.log('test');
 	    }
    });
