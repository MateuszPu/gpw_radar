angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('news-espi', {
                parent: 'news-details',
                url: '/news/espi',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'news.type.espi'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/news/espi/espi.html',
                        controller: 'EspiController'
                    }
                }
            });
    });
