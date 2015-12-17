angular.module('gpwRadarApp')
    .controller('MostActiveStocksController', function ($scope, Websocket, StockFiveMinutesDetails) {

        $scope.events = StockFiveMinutesDetails.getTodaysDetails();
        Websocket.subscribeMostActiveStocks();

        Websocket.reciveMostActiveStocks().then(null, null, function(response) {
            $scope.events.push(JSON.parse(response.body));
            console.log($scope.events);
            console.log(JSON.parse(response.body));
            console.log($scope.events);
        });
    })

