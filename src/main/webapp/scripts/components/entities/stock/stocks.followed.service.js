angular.module('gpwRadarApp').factory('StocksFollowed', function($http) {
	var stocksFollowedByUser = {
			 getStocksFollowed: function() {
				 var promise =  $http.get('api/users/stocks/followed').then(function(response) {
					 return response.data;
		    		});
				 return promise;
			 },
			
			 followStock: function(id) {
		    	$http.get('api/stock/follow/'+id).success(function(response) {
		    		return response;
		    	});
			 },
			 
			 stopFollowStock: function(id) {
	    		$http.get('api/stock/stop/follow/'+id).success(function(response) {
	    			return response;
	    		});
			 }
			  
		  };
		  return stocksFollowedByUser;
	});

angular.module('gpwRadarApp').filter('getById', function() {
	return function(input, id) {
		var i=0, len=input.length;
			for (; i<len; i++) {
				if (+input[i].id == +id) {
					return input[i];
				}
			}	
		return null;
	}
});