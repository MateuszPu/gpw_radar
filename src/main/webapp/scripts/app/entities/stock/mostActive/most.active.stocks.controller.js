angular.module('gpwRadarApp')
    .controller('MostActiveStocksController', function ($scope, Websocket, StockFiveMinutesDetails) {

        $scope.events = StockFiveMinutesDetails.getTodaysDetails();

        Websocket.receiveWebsocketStatus().then(null, null, function(data){
            Websocket.subscribeMostActiveStocks();
        });

        Websocket.reciveMostActiveStocks().then(null, null, function(response) {
            $scope.events.push(JSON.parse(response.body));
        });
    })

