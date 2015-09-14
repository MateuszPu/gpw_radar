'use strict';

angular.module('gpwradarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('configurator', {
                parent: 'admin',
                url: '/configurator',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'configurator.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/appConfigurator/configurator.html',
                        controller: 'ConfiguratorController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('configurator');
                        return $translate.refresh();
                    }]
                }
            });
    });
