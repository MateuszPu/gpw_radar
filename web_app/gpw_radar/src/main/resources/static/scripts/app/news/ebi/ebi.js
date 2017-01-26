angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('news-ebi', {
                parent: 'news-details',
                url: '/news/ebi',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'news.type.ebi'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/news/ebi/ebi.html',
                        controller: 'EbiController'
                    }
                }
            });
    });
