'use strict';

angular.module('gpwradarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('stock', {
                parent: 'entity',
                url: '/stocks',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'gpwradarApp.stock.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/stock/stocks.html',
                        controller: 'StockController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stock');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('stock.detail', {
                parent: 'entity',
                url: '/stock/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'gpwradarApp.stock.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/stock/stock-detail.html',
                        controller: 'StockDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stock');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Stock', function($stateParams, Stock) {
                        return Stock.get({id : $stateParams.id});
                    }]
                }
            })
            .state('stock.new', {
                parent: 'stock',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/stock/stock-dialog.html',
                        controller: 'StockDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {y: null, stockName: null, stockShortName: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('stock', null, { reload: true });
                    }, function() {
                        $state.go('stock');
                    })
                }]
            })
            .state('stock.edit', {
                parent: 'stock',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/stock/stock-dialog.html',
                        controller: 'StockDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Stock', function(Stock) {
                                return Stock.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('stock', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
