'use strict';

angular.module('gpwRadarApp')
    .controller('CorrelationController', function ($scope, $sce, $timeout, Stock, Correlation, CorrelationStep) {
        $scope.periods = ['10', '20', '30', '60', '90'];
        $scope.selected = {};
        $scope.selected.ticker = '';
        $scope.selected.period = '';

        $scope.tickers = Stock.getAllStockTickers();
        $scope.correlationTypes = Correlation.getCorrelationTypes();
        
        $scope.computeCorrelation = function (){
            $scope.isComputing = true;
            $scope.pollingData();
            Correlation.computeCorrelation({ticker: $scope.selected.ticker, period: $scope.selected.period, correlation_type: $scope.selected.type}, function(result) {
                $scope.smartTableSafeCopy = result;
                $scope.correlationList = [].concat($scope.smartTableSafeCopy);
                $scope.isComputing = false;
            });
        };

        $scope.pollingData = function(){
            (function tick() {
            	CorrelationStep.getStep().then(function(result) {
                    $scope.promiseTimeout = $timeout(tick, 1000);
                    $scope.stepOfCorrelation = result;

                    if(!$scope.isComputing) {
                        $timeout.cancel($scope.promiseTimeout);
                    }
                });
            })();
        }

        $scope.trustAsHtml = function(value) {
            return $sce.trustAsHtml(value);
        };

    });
