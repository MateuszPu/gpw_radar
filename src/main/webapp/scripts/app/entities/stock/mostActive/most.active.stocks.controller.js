angular.module('gpwRadarApp')
    .controller('MostActiveStocksController', function ($scope, Websocket) {

        $scope.messages = [];

        Websocket.subscribeMostActiveStocks();

        Websocket.reciveMostActiveStocks().then(null, null, function(message) {
            $scope.messages.push(JSON.parse(message.body));
        });
    })

