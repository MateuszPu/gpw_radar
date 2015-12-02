angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
    $stateProvider
        .state('mostActiveStocks', {
            parent: 'entity',
            url: '/most/active/stocks',
            data: {
                roles: ['ROLE_USER'],
                pageTitle: 'global.leftsidemenu.activeStocks.title'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/entities/stock/mostActive/most-active-stocks.html',
                    controller: 'MostActiveStocksController'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('stock');
                    return $translate.refresh();
                }]
            }
        });
    });
