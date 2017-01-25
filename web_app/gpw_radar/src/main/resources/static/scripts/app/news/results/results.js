angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('news-results', {
                parent: 'news-details',
                url: '/news/results',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'news.type.results'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/news/results/results.html',
                        controller: 'ResultsController'
                    }
                }
            });
    });
