angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('news-challenge', {
                parent: 'news-details',
                url: '/news/challenge',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'news.type.challenge'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/news/challenge/challenge.html',
                        controller: 'ChallengeController'
                    }
                }
            });
    });
