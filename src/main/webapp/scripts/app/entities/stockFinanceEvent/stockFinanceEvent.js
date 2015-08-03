'use strict';

angular.module('gpwradarApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('stockFinanceEvent', {
                parent: 'entity',
                url: '/stockFinanceEvents',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'gpwradarApp.stockFinanceEvent.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/stockFinanceEvent/stockFinanceEvents.html',
                        controller: 'StockFinanceEventController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stockFinanceEvent');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('stockFinanceEvent.detail', {
                parent: 'entity',
                url: '/stockFinanceEvent/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'gpwradarApp.stockFinanceEvent.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/stockFinanceEvent/stockFinanceEvent-detail.html',
                        controller: 'StockFinanceEventDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stockFinanceEvent');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'StockFinanceEvent', function($stateParams, StockFinanceEvent) {
                        return StockFinanceEvent.get({id : $stateParams.id});
                    }]
                }
            })
            .state('stockFinanceEvent.new', {
                parent: 'stockFinanceEvent',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/stockFinanceEvent/stockFinanceEvent-dialog.html',
                        controller: 'StockFinanceEventDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {date: null, message: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('stockFinanceEvent', null, { reload: true });
                    }, function() {
                        $state.go('stockFinanceEvent');
                    })
                }]
            })
            .state('stockFinanceEvent.edit', {
                parent: 'stockFinanceEvent',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/stockFinanceEvent/stockFinanceEvent-dialog.html',
                        controller: 'StockFinanceEventDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['StockFinanceEvent', function(StockFinanceEvent) {
                                return StockFinanceEvent.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('stockFinanceEvent', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
