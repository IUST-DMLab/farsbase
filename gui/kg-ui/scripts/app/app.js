let app = angular.module('KnowledgeGraphApp', ['ngRoute', 'ngMaterial', 'md.data.table', 'ngAnimate', 'ngAria', 'ngMessages', 'ngCookies', 'ngMdIcons']);

app.config(function ($routeProvider, $locationProvider, $httpProvider) {
    $routeProvider
        .when('/login', {
            templateUrl: 'templates/login.html',
            controller: 'LoginController'
        })
        .when('/users', {
            templateUrl: 'templates/users/list.html',
            controller: 'UsersController'
        })
        .when('/register', {
            templateUrl: '/templates/register.html',
            controller: 'RegisterController'
        })
        .when('/forgotPassword', {
            templateUrl: '/templates/forgotpassword.html',
            controller: 'forgotController'
        })
        .when('/home', {
            templateUrl: 'views/home.html',
            controller: 'homeController'
        })
        .otherwise({
            redirectTo: '/login'
        });
//    $locationProvider.html5Mode(true); //Remove the '#' from URL.

    $httpProvider.interceptors.push('loginInterceptor');
});

app.filter("mapPrefix", function (RestService) {
    var prefixes = null, // DATA RECEIVED ASYNCHRONOUSLY AND CACHED HERE
        serviceInvoked = false;

    function realFilter(text) { // REAL FILTER LOGIC
        if (!text) return text;
        for (var link in prefixes) {
            var p = prefixes[link];
            if (text.indexOf(link) !== -1)
                return p + ':' + text.replace(link, '');
        }
        return text;
    }

    filterStub.$stateful = true;
    function filterStub(value) { // FILTER WRAPPER TO COPE WITH ASYNCHRONICITY
        if (prefixes === null) {
            if (!serviceInvoked) {
                serviceInvoked = true;
                // CALL THE SERVICE THAT FETCHES THE DATA HERE
                RestService.getPrefixes().then(function (result) {
                    prefixes = result.data;
                });
            }
            return ""; // PLACEHOLDER WHILE LOADING, COULD BE EMPTY
        }
        else
            return realFilter(value);
    }

    return filterStub;
});

app.filter('triple', function () {
    return function (subject) {
        return 'http://194.225.227.161/mapping/html/triple.html?subject=' + subject;
    };
});

app.factory('loginInterceptor', function ($q, $location) {
    return {
        'response': function (response) {
            if (typeof response.data === 'string' && response.data.indexOf('action="/login"') > -1) {
                console.log("LOGIN!!");
                //console.log(response);
                $location.path( "/login" );
                return $q.reject(response);
            }
            else {
                return response;
            }
        }
    }

});

