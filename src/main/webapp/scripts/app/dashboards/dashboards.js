angular.module('gpwradarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('dashboard', {
                abstract: true,
                parent: 'site'
            });
    });