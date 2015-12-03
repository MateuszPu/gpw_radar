angular.module('gpwRadarApp')
    .controller('MostActiveStocksController', function ($scope, Websocket) {

        $scope.events = [];

        Websocket.subscribeMostActiveStocks();

        Websocket.reciveMostActiveStocks().then(null, null, function(message) {
            $scope.events.push(JSON.parse(message.body));
            console.log($scope.events);
        });
    })

