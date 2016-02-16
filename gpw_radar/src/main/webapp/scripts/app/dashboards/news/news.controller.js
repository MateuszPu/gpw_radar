angular.module('gpwRadarApp')
    .controller('NewsMessageController', function ($scope, NewsMessage) {

        $scope.page = 0;
        $scope.size = 5;

    	$scope.espiNewsMessages = NewsMessage.getLatestMessagesByType({type: 'ESPI', page: $scope.page, size: $scope.size = 5});
    	$scope.ebiNewsMessages = NewsMessage.getLatestMessagesByType({type: 'EBI', page: $scope.page, size: $scope.size = 5});
    	$scope.resultsNewsMessages = NewsMessage.getLatestMessagesByType({type: 'RESULTS', page: $scope.page, size: $scope.size = 5});
    	$scope.papNewsMessages = NewsMessage.getLatestMessagesByType({type: 'PAP', page: $scope.page, size: $scope.size = 5});
    	$scope.challengeNewsMessages = NewsMessage.getLatestMessagesByType({type: 'CHALLENGE', page: $scope.page, size: $scope.size = 5});
    	$scope.recommendationsNewsMessages = NewsMessage.getLatestMessagesByType({type: 'RECOMMENDATIONS', page: $scope.page, size: $scope.size = 5});

    })
	.filter("htmlConvert", ['$sce', function($sce) {
		return function(htmlCode) {
		  return $sce.trustAsHtml(htmlCode);
		}
	}]);
