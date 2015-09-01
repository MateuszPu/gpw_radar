'use strict';

angular.module('gpwradarApp')
    .controller('CorrelationController', function ($scope, $sce, $timeout, Stock, Correlation, CorrelationStep) {
        $scope.periods = ['10', '20', '30', '60', '90'];
        $scope.selected = {};
        $scope.selected.ticker = '';
        $scope.selected.period = '';

//        $scope.getAllTickers = function() {
//            Stock.getAllStockTickers(function(result) {
//                $scope.tickers = result;
//            })
//        };
        
        $scope.tickers = Stock.getAllStockTickers();
//        $scope.getAllTickers();
        
        $scope.correlationTypes = Correlation.getCorrelationTypes();
        
        console.log($scope.correlationTypes);

        $scope.computeCorrelation = function (){
            $scope.isComputing = true;
            $scope.pollingData();
            Correlation.computeCorrelation({ticker: $scope.selected.ticker, period: $scope.selected.period, correlation_type: $scope.selected.type}, function(result) {
                $scope.smartTableSafeCopy = result;
                $scope.correlationList = [].concat($scope.smartTableSafeCopy);
                $scope.isComputing = false;
                //$scope.selected.ticker = '';
                //$scope.selected.period = '';
            });
        };

        $scope.pollingData = function(){
            (function tick() {
            	CorrelationStep.getStep().then(function(result) {
                    console.log(result);
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
