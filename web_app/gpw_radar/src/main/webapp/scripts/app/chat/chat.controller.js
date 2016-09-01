angular.module('gpwRadarApp')
    .controller('ChatController', function ($scope, $window, $http, ChatMessage, Websocket) {

    	$scope.showSystemMessage = true;

    	$scope.showMessage = function(login) {
    		if ($scope.showSystemMessage) {
    			return true;
    		}
    		else {
    			return login != 'system';
    		}
    	}

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

        Websocket.receiveWebsocketStatus().then(null, null, function(data){
            Websocket.subscribeUsersOnChat();
            Websocket.subscribeChatMessages();
            Websocket.userLoginToChat();
        });

        Websocket.receiveUsersOnChat().then(null, null, function(user) {
        	$scope.users = JSON.parse(user.body);
        });

        Websocket.reciveChatMessages().then(null, null, function(message) {
        	$scope.messages.push(JSON.parse(message.body));
        });

	    $scope.sendMessage = function() {
	        Websocket.sendMessageToChat($scope.message);
	        if($scope.messages.length > 15){
	        	$scope.messages.splice(0, $scope.messages.length-15);
	        }
	        $scope.message = "";
	    };

        $window.onbeforeunload = function (evt) {
        	Websocket.userLeaveChat();
        	Websocket.unsubscribeChat();
        };

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
