angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('news-details', {
                abstract: true,
                parent: 'site',
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('news');
                        return $translate.refresh();
                    }]
                }
            });
    });
