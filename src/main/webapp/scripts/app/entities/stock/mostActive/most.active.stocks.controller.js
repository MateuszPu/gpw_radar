angular.module('gpwRadarApp')
    .controller('MostActiveStocksController', function ($scope, Websocket, StockFiveMinutesDetails) {

        $scope.events = StockFiveMinutesDetails.getTodaysDetails();

        console.log($scope.events);
        Websocket.subscribeMostActiveStocks();

        Websocket.reciveMostActiveStocks().then(null, null, function(response) {
            $scope.events.push(JSON.parse(response.body));
        });
    })

