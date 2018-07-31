var app = angular.module('kgui', ['ui.bootstrap', 'ngTagsInput', 'autoCompleteModule', 'bw.paging']);

app.filter("mapPrefix", function (RestService) {
    var prefixes = null, // DATA RECEIVED ASYNCHRONOUSLY AND CACHED HERE
        serviceInvoked = false;

    function realFilter(text) { // REAL FILTER LOGIC
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
                RestService.getPrefixes().success(function (result) {
                    prefixes = result;
                });
            }
            return "-"; // PLACEHOLDER WHILE LOADING, COULD BE EMPTY
        }
        else
            return realFilter(value);
    }

    return filterStub;
});

app.filter('index', function () {
    return function (array, index) {
        if (!index)
            index = 'index';
        for (var i = 0; i < array.length; ++i) {
            array[i][index] = i;
        }
        return array;
    };
});

app.filter('persianNumbers', function () {
    return function (str) {
        return str ? str.toString().numbersToPersian() : undefined;
    };
});


var OUC = {
    isEmpty: function (obj) {
        return obj == undefined || obj == null;
    }
};