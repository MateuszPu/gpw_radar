angular.module('gpwRadarApp')
    .factory('StockFinanceEvent', function ($resource) {
        return $resource('api/stockFinanceEvents/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    var DateFrom = data.date.split("-");
                    data.Date = new Date(new Date(DateFrom[0], DateFrom[1] - 1, DateFrom[2]));
                    return data;
                }
            },
            'update': { method:'PUT' },
            'getFollowedStockFinanceEvents' : { method:'GET', url:'api/users/stocks/followed/finance/event', isArray: true},
            'getAllStockFinanceEvents' : { method:'GET', url:'api/all/finance/event', isArray: true}
        });
    });
