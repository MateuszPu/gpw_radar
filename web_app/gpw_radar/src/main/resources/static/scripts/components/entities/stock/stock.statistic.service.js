angular.module('gpwRadarApp').factory('StockStatistic', function($http) {
    var statistics = {
        getStocksUpCount: function() {
            var promise =  $http.get('api/statistic/stocks/up').then(function(response) {
                return response.data;
            });
            return promise;
        },

        getStocksDownCount: function() {
            var promise = $http.get('api/statistic/stocks/down').then(function(response) {
                return response.data;
            });
            return promise;
        },

        getStocksNoChangeCount: function() {
            var promise = $http.get('api/statistic/stocks/no/change').then(function(response) {
                return response.data;
            });
            return promise;
        },

        getMostFollowedStocks: function() {
            var promise = $http.get('api/statistic/five/most/followed/stocks').then(function (response) {
                return response.data;
            });
            return promise;
        },

        getFiveLatestNewsMessages: function() {
            var promise = $http.get('api/news/message/top/five/latest').then(function (response) {
                return response.data;
            });
            return promise;
        }
    };
    return statistics;
});
