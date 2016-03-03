angular.module('gpwRadarApp')
.controller('AdministrationPanelController', function ($scope, $http, $timeout, $filter,
    		StocksFollowed, ApplicationStatus, StockFinanceEvent, DateUtils) {
        //scope for app status
    	$scope.isApplicationUpdating = false;
        $scope.today = new Date();
        $scope.days = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];
        $scope.stepOfUpdate = 0;
        $scope.topDateOfStock;
        $scope.differenceBetweenDaysAndDB;

        //scope for main page
        $scope.stocksFollowedByUserList = [];

    	$scope.followStock = function(id){
    		StocksFollowed.followStock(id);
    		$scope.showDetailsOfStocksFollowed
    	};

    	$scope.stopFollowStock = function(id){
    		StocksFollowed.stopFollowStock(id);
    		$scope.showDetailsOfStocksFollowed();
    	};

    	$scope.isFollowed = function(id){
    		var found = $filter('getById')($scope.stocksFollowedByUser, id);
    		return found;
    	};

    	//application status part
    	$scope.showApplicationStatus = function(){
    		$scope.getStatusOfUpdating();
    		$scope.getStatusOfStocksDates();
    	};

    	$scope.getStatusOfStocksDates = function(){
    		ApplicationStatus.getStatusOfStocks(function(response){
    			$scope.topDateOfStock = DateUtils.convertLocaleDateFromServer(response);
    			$scope.differenceBetweenDaysAndDB =  Math.floor(($scope.today- $scope.topDateOfStock) / (1000 * 3600 * 24));
    		});
    	};

    	$scope.getStatusOfUpdating = function(){
    		ApplicationStatus.isUpdating(function(response){
    			$scope.isApplicationUpdating = response.updating;
    			$scope.stepOfUpdate = response.step;

                if($scope.isApplicationUpdating){
                    $scope.pollingApplicationStatusData();
                }
    		});
    	};

        $scope.pollingApplicationStatusData = function(){
            (function tick() {
                $scope.data = ApplicationStatus.isUpdating(function(response){
                	$scope.timeInMiliSeconds = 1000;
                    $scope.promiseTimeout = $timeout(tick, $scope.timeInMiliSeconds);
                    $scope.stepOfUpdate = response.step;

                    if(!response.updating) {
                        $timeout.cancel($scope.promiseTimeout);
                        $scope.isApplicationUpdating = false;
                    }
                });
            })();
        }

        //followed stocks part
        $scope.showDetailsOfStocksFollowed = function(){
        	$scope.getAllStocksFollowed();
        };

    	$scope.getAllStocksFollowed = function(){
	    	StocksFollowed.getStocksFollowed().then(function(response) {
	    		$scope.smartTableSafeCopy = response;
    			$scope.stocksFollowedByUserList = [].concat($scope.smartTableSafeCopy);
	    	});
        };

        //calendar part
        $scope.financeEvents = [];
        $scope.financeEventsLoaded = false;
        $scope.clickedDate = false;

        $scope.getAllStockFinanceEvents = function() {
        	$scope.clearData();
        	StockFinanceEvent.getAllStockFinanceEvents(function(response){
                $scope.prepareEvents(response);
    		});
        };

        $scope.getFollowedStockFinanceEvents = function() {
        	$scope.clearData();
        	StockFinanceEvent.getFollowedStockFinanceEvents(function(response){
                $scope.prepareEvents(response);
        	});
        };

        $scope.prepareEvents = function (stockFinanceEvents) {
            for(var i = 0; i < stockFinanceEvents.length; i++){
                $scope.event = {};
                $scope.event.start = stockFinanceEvents[i].start;
                $scope.event.title = stockFinanceEvents[i].title;
                $scope.event.message = stockFinanceEvents[i].message;
                $scope.financeEvents.push($scope.event);
            }
            $scope.financeEventsLoaded = true;
        };

        $scope.clearData = function() {
        	$scope.financeEvents.length = 0;
        	$scope.financeEventsLoaded = false;
        	$scope.clickedDate = false;
        };

        $scope.eventSources = [$scope.financeEvents];

        $scope.clickEvent = function(date, jsEvent, view){
        	$scope.clickedDate = true;
        	$scope.event.message = date.message;
        	$scope.event.date = date.start._i;
        	$scope.event.ticker = date.title;
        };

        $scope.uiConfig = {
      	      calendar:{
      	        height: 450,
      	        editable: false,
      	        eventColor: '#FF0000',
      	        eventBorderColor:'#000000',
      	        header:{
      	          left: '',
      	          center: 'title',
      	          right: 'today prev,next'
      	        },
      	        eventClick: $scope.clickEvent,
      	      }
      };
});
