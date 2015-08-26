angular.module('gpwradarApp')
    .controller('AdministrationPanelController', function ($scope, $http, $timeout, $filter, StocksFollowed, ApplicationStatus, StockFinanceEvent) {
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
    			$scope.topDateOfStock = new Date(response.date);
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
                    $scope.stepOfUpdate = result.step;
                    console.log($scope.stepOfUpdate);

                    if(!result.updating) {
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
        $scope.showFinanceEventCalendar = function(){
        	$scope.getStockFinanceEvents();
        };
        $scope.financeEventsLoaded = false;
        $scope.clickedDate = false;

        $scope.getStockFinanceEvents = function(){
        	StockFinanceEvent.getStockFinanceEvents(function(response){
				$scope.finance = response;
				$scope.financeEventsLoaded = true;
        	});

        }

        $scope.getDayClass = function(date, mode) {
        	if($scope.financeEventsLoaded === true){
		        if (mode === 'day') {
		          var dayToCheck = new Date(date).setHours(0,0,0,0);

		          for (var i=0;i<$scope.finance.length;i++){
		            var currentDay = new Date($scope.finance[i].date).setHours(0,0,0,0);

		            if (dayToCheck === currentDay) {
		              return 'full';
		            }
		          }
		        }
	        return '';
        	}
        };

        $scope.clickOnDate = function(calendarDay){
        	$scope.financeEventFounded = false;
        	if($scope.financeEventsLoaded === true && !(typeof calendarDay === 'undefined')){
        		$scope.clickedDate = true;
		    	$scope.financeEvents = [];
		    	for (var i=0;i<$scope.finance.length;i++){
		    		var financeEventDate = new Date($scope.finance[i].date);
	    			financeEventDate.setHours(0,0,0,0)

			    		if(calendarDay.getTime() === financeEventDate.getTime()){
			    			var ticker = $scope.finance[i].stock.ticker;
			    			var financeEvent = $scope.finance[i].message;

			    			$scope.merged = [];
			    			$scope.merged.ticker = ticker.toUpperCase();
			    			$scope.merged.event = financeEvent;

			    			$scope.financeEvents.push($scope.merged);
			    			$scope.financeEventFounded = true;
				    	}
		    		}
		    	}
		    }
    	});
