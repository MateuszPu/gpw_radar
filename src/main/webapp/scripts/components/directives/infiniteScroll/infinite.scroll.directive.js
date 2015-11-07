angular.module('gpwRadarApp').directive('upwardsScoll', function ($timeout) {
    return {
        link: function (scope, elem, attr, ctrl) {
            var raw = elem[0];
 
            elem.bind('scroll', function() {
                if(raw.scrollTop <= 0) {
                    var sh = raw.scrollHeight;
                    scope.$apply(attr.upwardsScoll);
 
                    $timeout(function() {
                        elem.animate({
                            scrollTop: raw.scrollHeight - sh
                        }, 500);
                    }, 100);
                }
            });
 
            //scroll to bottom
            $timeout(function() {
                scope.$apply(function () {
                    elem.scrollTop( raw.scrollHeight );
                });
            }, 0);
        }
    }
});