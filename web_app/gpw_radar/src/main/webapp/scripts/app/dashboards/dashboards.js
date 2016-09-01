angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('dashboard', {
                abstract: true,
                parent: 'site'
            });
    });