angular.module('gpwRadarApp')
	.directive('iboxTools', function($timeout) {
		return {
	        restrict: 'AE',
	        scope: {
                hideTools: '=?'
            },
	        templateUrl: 'scripts/components/directives/iboxTools/ibox-tools.html',
	        controller: function ($scope, $element) {
	            // Function for collapse ibox
	            $scope.showhide = function () {
	                var ibox = $element.closest('div.ibox');
	                var icon = $element.find('i:first');
	                var content = ibox.find('div.ibox-content');
	                content.slideToggle(200);
	                // Toggle icon from up to down
	                icon.toggleClass('fa-chevron-up').toggleClass('fa-chevron-down');
	                ibox.toggleClass('').toggleClass('border-bottom');
	                $timeout(function () {
	                    ibox.resize();
	                    ibox.find('[id^=map-]').resize();
	                }, 50);
	            },
	                // Function for close ibox
                $scope.closebox = function () {
                    var ibox = $element.closest('div.ibox');
                    ibox.remove();
                },
                $scope.hideTools = angular.isDefined($scope.hideTools) ? $scope.hideTools : true;
                $scope.checkShowHide = function() {
                    console.log($scope.hideTools);
                    if($scope.hideTools == true) {
                        $scope.showhide();
                    }
                },
                $scope.checkShowHide();
            }
	    };
	});
