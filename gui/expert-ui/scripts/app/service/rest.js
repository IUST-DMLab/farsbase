app.service('RestService', ['$http', function ($http) {
    var baseURl = 'http://dmls.iust.ac.ir:8092';

    var self = this;
    this.ingoing = 0;

    self.init = function (rootAddress) {
        baseURl = rootAddress;
    };

    function onerror(response) {
        loading.hide();
        self.ingoing--;
        console.log('onerror : ', response);
        return response;
    }

    function onsuccess(response) {
        loading.hide();
        self.ingoing--;
        // console.log('onsuccess : ', response);
        return response;
    }

    function get(url, params, headers) {
        params = params || {};
        params.random = new Date().getTime();

        var req = {
            method: 'GET',
            url: url,
            headers: headers,
            params: params
        };

        self.ingoing++;
        loading.show();
        return $http(req).then(onsuccess, onerror);
    }

    function post(url, data, headers) {
        var req = {
            method: 'POST',
            url: url,
            headers: headers,
            data: data
        };

        self.ingoing++;
        loading.show();

        return $http(req).then(onsuccess, onerror);
    }

    /**/

    this.getPrefixes = function () {
        return get('http://dmls.iust.ac.ir:8090/mapping/rest/v1/prefixes');
    };

    this.login = function (username, password) {
        var url = baseURl + '/services/rs/v1/experts/login';
        var headers = {'Authorization': 'Basic ' + btoa(username + ':' + password)};
        return get(url, {}, headers);
    };

    this.getSubjects = function (authToken) {
        var url = baseURl + '/services/rs/v1/experts/subjects/current';
        var headers = {"x-auth-token": authToken};

        return get(url, {}, headers);
    };

    this.getTriples = function (authToken, subjectId, pageIndex, pageSize) {

        var url = baseURl + '/services/rs/v1/experts/triples/current';
        var headers = {"x-auth-token": authToken};
        var params = {
            subject: subjectId,
            page: pageIndex,
            pageSize: pageSize
        };

        return get(url, params, headers);
    };

    this.vote = function (authToken, identifier, vote) {
        var url = baseURl + '/services/rs/v1/experts/vote';
        var headers = {"x-auth-token": authToken};
        var params = {
            identifier: identifier,
            vote: vote
        };

        return get(url, params, headers);
    };

    this.batchVote = function (authToken, items) {
        let url = baseURl + '/services/rs/v1/experts/vote/batch';
        let params = items;
        let headers = {"x-auth-token": authToken};
        return post(url, params, headers);
    };

    this.requestMore = function (authToken, sourceModule, subject, exact) {
        let url = baseURl + '/services/rs/v1/experts/triples/new/subject';
        let headers = {"x-auth-token": authToken};
        let params = {
            sourceModule: sourceModule,
            subjectQuery: !exact ? subject : undefined,
            subjectMatch: exact ? subject : undefined,
            size: sourceModule.indexOf('wiki') !== -1 ? 100000 : undefined
        };

        return get(url, params, headers);
    };

    this.getLabel = function (subject) {

        var url = 'http://dmls.iust.ac.ir:8091/rs/v1/triples/search';
        var params = {
            predicate: 'http://www.w3.org/2000/01/rdf-schema#label',
            useRegexForPredicate: false,
            subject: subject,
            useRegexForSubject: false
        };

        return get(url, params);
    };
}]);

var loading = {
    show: function () {
        $('#loading').remove();
        $('body').append('<div id="loading" class="loading"><span>در حال تبادل اطلاعات ...</span></div>');
        $('#loading').fadeIn();
    },
    hide: function () {
        $('#loading').fadeOut();
    }
};
