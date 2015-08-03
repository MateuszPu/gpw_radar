'use strict';

angular.module('gpwradarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('stockDetails', {
                parent: 'entity',
                url: '/stockDetailss',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'gpwradarApp.stockDetails.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/stockDetails/stockDetailss.html',
                        controller: 'StockDetailsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stockDetails');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('stockDetails.detail', {
                parent: 'entity',
                url: '/stockDetails/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'gpwradarApp.stockDetails.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/stockDetails/stockDetails-detail.html',
                        controller: 'StockDetailsDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stockDetails');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'StockDetails', function($stateParams, StockDetails) {
                        return StockDetails.get({id : $stateParams.id});
                    }]
                }
            })
            .state('stockDetails.new', {
                parent: 'stockDetails',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/stockDetails/stockDetails-dialog.html',
                        controller: 'StockDetailsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {date: null, openPrice: null, maxPrice: null, minPrice: null, closePrice: null, volume: null, averageVolume10Days: null, averageVolume30Days: null, volumeRatio10: null, volumeRatio30: null, percentReturn: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('stockDetails', null, { reload: true });
                    }, function() {
                        $state.go('stockDetails');
                    })
                }]
            })
            .state('stockDetails.edit', {
                parent: 'stockDetails',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/stockDetails/stockDetails-dialog.html',
                        controller: 'StockDetailsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['StockDetails', function(StockDetails) {
                                return StockDetails.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('stockDetails', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
