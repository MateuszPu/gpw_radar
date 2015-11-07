'use strict';

angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('correlation', {
                parent: 'entity',
                url: '/correlation',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'gpwanalysisApp.correlation.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/stock/correlation/correlation.html',
                        controller: 'CorrelationController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('correlation');
                        return $translate.refresh();
                    }]
                }
            })
    });
