var app = angular.module('kgui', ['ngRoute', 'ui.bootstrap', 'ngTagsInput', 'autoCompleteModule', 'bw.paging', 'ngMessages']);

app.filter("translate", function (RestService) {
    var dic = {}, // DATA RECEIVED ASYNCHRONOUSLY AND CACHED HERE
        serviceInvoked = false;

    function realFilter(name) { // REAL FILTER LOGIC
        return dic[name];
    }

    filterStub.$stateful = true;
    function filterStub(value) { // FILTER WRAPPER TO COPE WITH ASYNCHRONICITY
        var name = (value || '').split('/').pop();
        if (!dic[name]) {
            //if (!serviceInvoked) {
                serviceInvoked = true;
                // CALL THE SERVICE THAT FETCHES THE DATA HERE
                RestService.translate(name).success(function (result) {
                    dic[name] = result.faLabel || '';
                    serviceInvoked = false;
                });
            //}
            return "-"; // PLACEHOLDER WHILE LOADING, COULD BE EMPTY
        }
        else
            return realFilter(name);
    }

    return filterStub;
});

app.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/:keyword?', {
                controller: 'MainController'
            });
    }]);

app.run(function ($rootScope, $route, $location, $templateCache) {
    //Bind the `$locationChangeSuccess` event on the rootScope, so that we dont need to
    //bind in induvidual controllers.

    $rootScope.$on('$locationChangeSuccess', function () {
        $rootScope.actualLocation = $location.path();
        $rootScope.$$childHead.load();
    });

    $rootScope.$watch(function () {
        return $location.path();
    }, function (newLocation, oldLocation) {
        if ($rootScope.actualLocation === newLocation) {
            console.log('Why did you use history back?');
        }
    });

    $rootScope.$on('$viewContentLoaded', function() {
        $templateCache.removeAll();
    });
});


// app.filter('triple', function(){
//     return function(subject){
//         return 'http://194.225.227.161/mapping/html/triple.html?subject='+subject;
//     };
// });

var OUC = {
    isEmpty: function (obj) {
        return obj == undefined || obj == null;
    }
};