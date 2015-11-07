'use strict';

angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('chat', {
                parent: 'site',
                url: '/chat',
                data: {
                	roles: ['ROLE_USER'],
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/chat/chat.html',
                        controller: 'ChatController'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('chat');
                        return $translate.refresh();
                    }]
                }
            });
    });
