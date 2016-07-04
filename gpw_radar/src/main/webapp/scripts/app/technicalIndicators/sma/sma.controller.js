angular.module('gpwRadarApp')
    .controller('SmaController', function ($scope, SmaIndicator) {
        $scope.sliderCrossover = {
            fasterSma: 15,
            slowerSma: 30,
            options: {
                floor: 5,
                ceil: 90,
                step: 1
            }
        };
        $scope.sliderPriceCrossSma = {
            sma: 15,
            options: {
                floor: 5,
                ceil: 90,
                step: 1
            }
        };
        $scope.crossoverType = "FROM_BELOW";
        $scope.smaCrossPriceType = "FROM_BELOW";
        $scope.itemsByPage = 5;

        $scope.findPriceCrossSma = function() {
            SmaIndicator.getPriceCrossSma({direction: $scope.smaCrossPriceType, sma: $scope.sliderPriceCrossSma.sma}, function(result) {
                $scope.priceCrossSmaSafeCopy = result;
                $scope.priceCrossSma = [].concat($scope.priceCrossSmaSafeCopy);
            });
        };

        $scope.findCrossover = function () {
            SmaIndicator.getSmaCrossover({direction: $scope.crossoverType, fasterSma: $scope.sliderCrossover.fasterSma, slowerSma: $scope.sliderCrossover.slowerSma}, function(result) {
                $scope.smaCrossoverSafeCopy = result;
                $scope.smaCrossover = [].concat($scope.smaCrossoverSafeCopy);
            });
        };
    });
