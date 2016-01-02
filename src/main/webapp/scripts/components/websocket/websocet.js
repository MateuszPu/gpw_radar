'use strict';

angular.module('gpwRadarApp')
    .factory('Websocket', function ($rootScope, $cookies, $http, $q) {
        var stompClient = null;
        var subscriberUsersOnChat = null;
        var subscriberChatMessages = null;
        var subscriberMostActiveStocks = null;
        var subscriberStepOfFillDb = null;
        var subscriberCountUsers = null;
        var listenerWebsocketConnection = $q.defer();
        var listenerCountUser = $q.defer();
        var listenerUsersOnChat = $q.defer();
        var listenerChatMessages = $q.defer();
        var listenerMostActiveStocks = $q.defer();
        var listenerStepOfFillDb = $q.defer();
        var connected = $q.defer();
        var alreadyConnectedOnce = false;

        function websocketConnectionOpen() {
            stompClient.send('/app/websocket/connection/open');
        };

        function subscribeWebsocketConnection() {
            stompClient.subscribe("/websocket/status", function(data) {
                listenerWebsocketConnection.notify(data);
            });
        };

        return {
            connect: function () {
                //building absolute path so that websocket doesnt fail when deploying with a context path
                var loc = window.location;
                var url = '//' + loc.host + loc.pathname + 'socket';
                var socket = new SockJS(url);
                stompClient = Stomp.over(socket);
                stompClient.debug = null;
                var headers = {};
                headers['X-CSRF-TOKEN'] = $cookies[$http.defaults.xsrfCookieName];
                stompClient.connect(headers, function(frame) {
                    connected.resolve("success");
                    websocketConnectionOpen();
                    subscribeWebsocketConnection();
                    if (!alreadyConnectedOnce) {
                        websocketConnectionOpen();
                        subscribeWebsocketConnection();
                        alreadyConnectedOnce = true;
                    }
                });
            },
            userConnectedToApp: function() {
                stompClient.send('/app/webchat/user/app/on');
            },
            subscribeCountUsers: function() {
                subscriberCountUsers =  stompClient.subscribe("/webchat/count", function(data) {
                    listenerCountUser.notify(data);
                });
            },
            subscribeUsersOnChat: function() {
                    subscriberUsersOnChat = stompClient.subscribe("/webchat/user", function (data) {
                        listenerUsersOnChat.notify(data);
                    });
            },
            subscribeChatMessages: function() {
	            subscriberChatMessages = stompClient.subscribe("/webchat/recive", function(data) {
	            	listenerChatMessages.notify(data);
	            });
            },
            subscribeMostActiveStocks: function() {
                subscriberMostActiveStocks = stompClient.subscribe("/most/active/stocks", function(data){
                    listenerMostActiveStocks.notify(data);
                });
            },
            subscribeStepOfFillDb: function() {
                subscriberStepOfFillDb = stompClient.subscribe("/step/of/fill/db", function(data){
                    listenerStepOfFillDb.notify(data);
                });
            },
            unsubscribeChat: function() {
                if (subscriberUsersOnChat != null) {
                	subscriberUsersOnChat.unsubscribe();
                	subscriberChatMessages.unsubscribe();
                }
            },
            unsubscribeMostActiveStocks: function() {
                if (subscriberMostActiveStocks != null) {
                    subscriberMostActiveStocks.unsubscribe();
                }
            },
            receiveWebsocketStatus: function() {
                return listenerWebsocketConnection.promise;
            },
            receiveCountUsers: function() {
                return listenerCountUser.promise;
            },
            receiveUsersOnChat: function() {
                return listenerUsersOnChat.promise;
            },
            reciveChatMessages: function() {
            	return listenerChatMessages.promise;
            },
            reciveMostActiveStocks: function() {
                return listenerMostActiveStocks.promise;
            },
            reciveStepOfFillDb: function() {
                return listenerStepOfFillDb.promise;
            },
            userLoginToChat: function() {
            	stompClient.send('/app/webchat/user/login');
            },
            userLeaveChat: function() {
            	stompClient.send('/app/webchat/user/logout');
            },
            sendMessageToChat: function(message) {
            	stompClient.send('/app/webchat/send/message', {}, JSON.stringify({'message': message}));
            },
            disconnect: function() {
                if (stompClient != null) {
                    stompClient.disconnect();
                    stompClient = null;
                }
            }
        };
    });
