'use strict';

angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('administrationPanel', {
                parent: 'site',
                url: '/user/administration/panel',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'panel.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/administrationPanel/administrationPanel.html',
                        controller: 'AdministrationPanelController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('administrationPanel');
                        return $translate.refresh();
                    }]
                }
            })
    });
