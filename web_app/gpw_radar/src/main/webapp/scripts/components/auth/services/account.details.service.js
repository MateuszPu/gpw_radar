angular.module('gpwRadarApp')
    .factory('AccountDetails', function() {
        var acc = null;
        return {
            getAccountDetails: function () {
                return acc;
            },
            setAccountDetails: function (acc2) {
                this.acc = acc2;
            }
        };
    });
