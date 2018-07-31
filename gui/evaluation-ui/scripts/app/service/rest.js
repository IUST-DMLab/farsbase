app.service('RestService', ['$http', function ($http) {
    // var baseURl = 'http://194.225.227.161:8096';
    let baseURl = 'http://dmls.iust.ac.ir:8099/proxy/evaluation';

    let self = this;
    this.ingoing = 0;

    self.init = function (rootAddress) {
        baseURl = rootAddress;
    };

    function onerror(response) {
        loading.hide();
        self.ingoing--;
        console.log('error : ', response);
        return response;
    }

    function onsuccess(response) {
        loading.hide();
        self.ingoing--;
        return response;
    }

    function get(url, params, headers, auth) {
        params = params || {};
        params.random = new Date().getTime();

        headers = headers || {};
        if (auth === undefined || auth === null || auth) {
            headers["Access-Control-Request-Headers"] = 'x-auth-token';
            headers["x-auth-token"] = localStorage.getItem('authToken');
        }

        let req = {
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

        headers = headers || {};
        headers["Access-Control-Request-Headers"] = 'x-auth-token';
        headers["x-auth-token"] = localStorage.getItem('authToken');

        var req = {
            method: 'POST',
            url: url,
            headers: headers,
            data: data
        };

        self.ingoing++;
        loading.show();

        return $http(req).then(onsuccess, onerror);
        //return $http.post(url, data, headers).then(onsuccess, onerror);
    }

    //

    this.next = function (personId, user) {
        var url = baseURl + '/rest/v1/evaluation/next';
        var params = {"personId": personId, user: user || ''};
        return get(url, params, {});
    };

    this.submit = function (userResponse) {
        let url = baseURl + '/rest/v1/evaluation/submit';
        let params = userResponse;
        return post(url, params, {});
    };

    this.search = function (keyword) {
        let url = 'http://194.225.227.161:8093' + '/rest/v1/searcher/search';
        let params = {keyword: keyword};
        return get(url, params, {}, false);
    };

    // eval

    this.eval = function (k) {
        var url = baseURl + '/rest/v1/evaluation/admin/eval-p-at-k';
        var params = {"k": k};
        return get(url, params, {});
    };

    // queries

    this.loadQueries = function () {
        let url = baseURl + '/rest/v1/evaluation/admin/allqueries';
        let params = {};
        return get(url, params, {});
    };

    this.addQuery = function (query) {
        let url = baseURl + '/rest/v1/evaluation/admin/addquery';
        let params = {"q": query};
        return post(url, params, {});
    };

    this.removeQuery = function (query) {
        let url = baseURl + '/rest/v1/evaluation/admin/deletequery';
        let data = {"q": query};
        return post(url, data, {});
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
