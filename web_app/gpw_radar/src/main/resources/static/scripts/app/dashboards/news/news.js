angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
        .state('news', {
            parent: 'dashboard',
            url: '/news',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'global.leftsidemenu.dashboards.news'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/dashboards/news/news.html',
                    controller: 'NewsMessageController'
                }
            },
            resolve: {
                mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                    $translatePartialLoader.addPart('news');
                    return $translate.refresh();
                }]
            }
        });
    });
