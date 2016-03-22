angular.module('gpwRadarApp')
    .controller('MostActiveStocksController', function ($scope, Websocket, StockFiveMinutesDetails) {

        $scope.events = [];
        $scope.miliSecondsInMinutes = 60000;

        StockFiveMinutesDetails.getTodaysDetails({time: new Date().getTime()}, function (result) {
            $scope.events.push(result);
        });

        $scope.loadMore = function () {
            $scope.lastTime = $scope.events[$scope.events.length - 1].time.split(":");
            $scope.fiveMinutesEarlier = new Date();
            $scope.fiveMinutesEarlier.setHours($scope.lastTime[0]);
            $scope.fiveMinutesEarlier.setMinutes($scope.lastTime[1]);
            $scope.fiveMinutesEarlier.setMilliseconds(0);
            $scope.fiveMinutesEarlier.setSeconds(0);
            $scope.time = $scope.fiveMinutesEarlier.getTime() - 5 * $scope.miliSecondsInMinutes;

            StockFiveMinutesDetails.getTodaysDetails({time: $scope.time}, function (result) {
                $scope.events.push(result);
            });
        };

        Websocket.receiveWebsocketStatus().then(null, null, function (data) {
            Websocket.subscribeMostActiveStocks();
        });

        Websocket.reciveMostActiveStocks().then(null, null, function (response) {
            $scope.events.push(JSON.parse(response.body));
        });
    });

