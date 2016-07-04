angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('sma', {
                parent: 'site',
                url: '/technical/indicators/sma',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'sma.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/technicalIndicators/sma/sma.html',
                        controller: 'SmaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sma');
                        $translatePartialLoader.addPart('stock');
                        return $translate.refresh();
                    }]
                }
            });
    });
