angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('news-recommendations', {
                parent: 'news-details',
                url: '/news/recommendations',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'news.type.recommendations'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/news/recommendations/recommendations.html',
                        controller: 'RecommendationsController'
                    }
                }
            });
    });
