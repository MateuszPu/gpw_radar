'use strict';

angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('chat', {
                parent: 'site',
                url: '/chat',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'global.menu.chat'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/chat/chat.html',
                        controller: 'ChatController'
                    }
                },
                onEnter: function($rootScope, Websocket){
                    if($rootScope.isWebsocket) {
                        Websocket.subscribeUsersOnChat();
                        Websocket.subscribeChatMessages();
                        Websocket.userLoginToChat();
                    }
                },
                onExit: function(Websocket) {
                    Websocket.userLeaveChat();
                    Websocket.unsubscribeChat();
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('chat');
                        return $translate.refresh();
                    }]
                }
            });
    });
