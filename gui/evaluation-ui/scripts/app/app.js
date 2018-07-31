var app = angular.module('evaluationApp', ['ngRoute', 'ngMaterial','ngAnimate','ngAria','ngMessages', 'ngCookies', 'ngMdIcons']);

app.filter("mapPrefix", function (RestService) {
    var prefixes = null, // DATA RECEIVED ASYNCHRONOUSLY AND CACHED HERE
        serviceInvoked = false;

    function realFilter(text) { // REAL FILTER LOGIC
        if(!text) return text;
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

app.filter('triple', function(){
    return function(subject){
        return 'http://194.225.227.161/mapping/html/triple.html?subject='+subject;
    };
});

function getParameterByName(name, url) {
    if (!url) {
        url = window.location.href;
    }
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

var OUC = {
    isEmpty: function (obj) {
        return obj == undefined || obj == null;
    }
};
