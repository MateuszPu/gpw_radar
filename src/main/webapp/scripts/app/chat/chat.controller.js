angular.module('gpwradarApp')
    .controller('ChatController', function ($scope, $window, $http, ngstomp, ChatMessage, Principal, Auth) {
    	
    	$scope.showSystemMessage = true;
    	
    	$scope.showMessage = function(login) {
    		if ($scope.showSystemMessage) {
    			return true;
    		}
    		else {
    			return login != 'system';
    		}
    	}
    	
    	Principal.identity().then(function(account) {
    		ngstomp.send('/app/webchat/user/login', account.login);
    		$scope.login = account.login;
    	});
    	
        $scope.messages = ChatMessage.getMessages({page: 0}); 
        
        var counter = 1;
        $scope.loadOlder = function() {
        	var promise = $http({
        	    url: 'api/chat/older/messages', 
        	    method: "GET",
        	    params: {page: counter}
        	 });
        	promise.then(
			  function(response) {
				  $scope.olderMessages = response.data;
				  for (var i = 0; i < $scope.olderMessages.length ; i++) {
		    			$scope.messages.unshift($scope.olderMessages[i]);
		    		}
			  });
            counter++;
        };
        
        ngstomp.subscribe('/webchat/user',  newUserConnected);
        function newUserConnected(user) {
        	$scope.users = JSON.parse(user.body);
        };
        
        ngstomp.subscribe('/webchat/recive',  messageFromServer);
        function messageFromServer(message) {
        	$scope.messages.push(JSON.parse(message.body));
        };
        
	    $scope.sendMessage = function() {
	        ngstomp.send('/app/webchat/send/message', $scope.message);
	        if($scope.messages.length > 15){
	        	$scope.messages.splice(0, $scope.messages.length-15);
	        }
	        $scope.message = "";
	    };
        
        $window.onbeforeunload = function (evt) {
        	Auth.logout(false);
        }
        
	    $scope.$on('$destroy', function() {
	    	Auth.logout(false);
	    });
	    
    	$scope.$watch('message', function (newValue, oldValue) {
			if (newValue) {
				if (newValue.length > 90) {
				  $scope.message = oldValue;
				}
				$scope.commentLength = 90 - newValue.length;
			}
		});
    })
    .filter("htmlConvert", ['$sce', function($sce) {
	  return function(htmlCode){
	    return $sce.trustAsHtml(htmlCode);
	  }
	}]);
