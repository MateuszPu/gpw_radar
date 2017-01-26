angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('news-pap', {
                parent: 'news-details',
                url: '/news/pap',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'news.type.pap'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/news/pap/pap.html',
                        controller: 'PapController'
                    }
                }
            });
    });
