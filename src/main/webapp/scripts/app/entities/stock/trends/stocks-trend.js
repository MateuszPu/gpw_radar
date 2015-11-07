'use strict';

angular.module('gpwRadarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('stocksTrendUp', {
                parent: 'entity',
                url: '/stocks/trend/up',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'gpwanalysisApp.stock.trend.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/stock/trends/stocks-trend-up.html',
                        controller: 'StockTrendsUpController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stock');
                        return $translate.refresh();
                    }]
                }
            })
            .state('stocksTrendDown', {
                parent: 'entity',
                url: '/stocks/trend/down',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'gpwanalysisApp.stock.trend.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/stock/trends/stocks-trend-down.html',
                        controller: 'StockTrendsDownController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stock');
                        return $translate.refresh();
                    }]
                }
            })
    });
