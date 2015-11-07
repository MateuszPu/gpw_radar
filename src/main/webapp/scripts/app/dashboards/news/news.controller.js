angular.module('gpwRadarApp')
    .controller('NewsMessageController', function ($scope, NewsMessage) {
    
    	$scope.espiNewsMessages = NewsMessage.getLatestMessagesByType({type: 'ESPI'});
    	$scope.ebiNewsMessages = NewsMessage.getLatestMessagesByType({type: 'EBI'});
    	$scope.resultsNewsMessages = NewsMessage.getLatestMessagesByType({type: 'RESULTS'});
    	$scope.papNewsMessages = NewsMessage.getLatestMessagesByType({type: 'PAP'});
    	$scope.challengeNewsMessages = NewsMessage.getLatestMessagesByType({type: 'CHALLENGE'});
    	$scope.recommendationsNewsMessages = NewsMessage.getLatestMessagesByType({type: 'RECOMMENDATIONS'});
    	
    })
	.filter("htmlConvert", ['$sce', function($sce) {
		return function(htmlCode) {
		  return $sce.trustAsHtml(htmlCode);
		}
	}]);