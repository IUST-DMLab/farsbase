app.service('RestService', ['$http', function ($http) {
    //var baseURl = 'http://farsbase.net:8099/proxy/raw';
    //var testUser = null;
    // var baseURl = 'http://dmls.iust.ac.ir:8100';
    var mapperUrl = 'http://farsbase.net:8099/proxy/mapper/';
    var baseURl = 'http://localhost:8100';
    var testUser = "test";

    var self = this;
    this.ingoing = 0;

    self.getTestUser = function () {
        return testUser;
    };

    self.init = function (rootAddress) {
        baseURl = rootAddress;
    };

    function onerror(response) {

        if (response.status === 403) {
            window.location.href = window.location.href + "../pkg";
            return;
        }

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
            if (testUser !== null) headers["x-auth-username"] = testUser;
            else {
                headers["Access-Control-Request-Headers"] = 'x-auth-token';
                headers["x-auth-token"] = localStorage.getItem('authToken');
            }
        }

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

    function post(url, data, headers, auth) {

        headers = headers || {};
        if (auth === undefined || auth === null || auth) {
            if (testUser !== null) headers["x-auth-username"] = testUser;
            else {
                headers["Access-Control-Request-Headers"] = 'x-auth-token';
                headers["x-auth-token"] = localStorage.getItem('authToken');
            }
        }

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

    this.getTriples = function (page, pageSize, predicate, like, minOccurrence, approved, assignee) {

        var url = baseURl + '/rest/v1/raw/search';
        var params = {
            page: page,
            pageSize: pageSize
        };

        if (!OUC.isEmpty(predicate)) params.predicate = predicate;
        if (!OUC.isEmpty(like)) params.like = like;
        if (!OUC.isEmpty(minOccurrence)) params.minOccurrence = minOccurrence;
        if (!OUC.isEmpty(approved)) params.approved = approved;
        if (!OUC.isEmpty(assignee)) params.assigneeUsername = assignee;

        return get(url, params);
    };

    this.goPredicates = function (page, pageSize, predicate) {

        var url = baseURl + '/rest/v1/raw/predicates';
        var params = {
            page: page,
            pageSize: pageSize
        };

        if (!OUC.isEmpty(predicate)) params.predicate = predicate;

        return get(url, params);
    };

    this.assigneeCount = function (predicate) {
        var url = baseURl + '/rest/v1/raw/assigneeCount';
        var params = {
            predicate: predicate
        };
        return get(url, params);
    };

    this.approve = function (id, approved) {
        var url = baseURl + '/rest/v1/raw/approve';
        var params = {
            id: id,
            approved: approved
        };
        return get(url, params);
    };

    this.rules = function () {
        var url = baseURl + '/rest/v1/raw/rules';
        return get(url, {page: 0, pageSize: 10000});
    };

    this.editRule = function (data) {
        var url = baseURl + '/rest/v1/raw/editRule';
        if (OUC.isEmpty(data.approved)) data.approved = false;
        var params = {
            rule: data.rule,
            predicate: data.predicate,
            approved: data.approved
        };
        if (!OUC.isEmpty(data.id)) params.id = data.id;
        return get(url, params);
    };

    this.removeRule = function (id) {
        var url = baseURl + '/rest/v1/raw/removeRule';
        var params = {
            id: id
        };
        return get(url, params);
    };

    this.ruleTest = function (data) {
        var url = baseURl + '/rest/v1/raw/ruleTest';
        return post(url, data);
    };

    this.listUsers = function () {
        var url = baseURl + '/rest/v1/raw/listUsers';
        return get(url, {});
    };

    this.assign = function (assignee, predicate, count) {
        var url = baseURl + '/rest/v1/raw/assign';
        var params = {
            username: assignee,
            count: count
        };
        if (!OUC.isEmpty(predicate)) params.predicate = predicate;
        return get(url, params);
    };

    this.goPatterns = function (page, pageSize) {
        var url = baseURl + '/rest/v1/raw/searchPattern';
        var params = {
            page: page,
            pageSize: pageSize
        };
        return get(url, params);
    };

    this.dependencyParse = function (text) {
        var url = baseURl + '/rest/v1/raw/dependencyParseGet';
        var params = {
            text: text
        };
        return get(url, params);
    };

    this.savePattern = function (data) {
        var url = baseURl + '/rest/v1/raw/savePattern';
        return post(url, data);
    };

    this.predictByPattern = function (text) {
        var url = baseURl + '/rest/v1/raw/predictByPatternPost';
        return post(url, {text: text});
    };

    this.fkgfy = function (text) {
        var url = baseURl + '/rest/v1/raw/FKGfy';
        return post(url, {text: text});
    };

    this.extractTriples = function (text) {
        var url = baseURl + '/rest/v1/raw/extractTriples';
        return post(url, {text: text});
    };

    this.getEntities = function (entities) {
        var url = mapperUrl + '/entity/rest/v2/getEntities';
        return post(url, {entities: entities});
    };

    this.getRepositoryLs = function (path) {
        var url = baseURl + '/rest/v1/raw/repository/ls';
        var params = {};
        if (path !== undefined) params.path = path;
        return get(url, params);
    };

    this.getRepositoryGet = function (path) {
        var url = baseURl + '/rest/v1/raw/repository/get';
        var params = {
            path: path
        };
        return get(url, params);
    };

    this.getRepositoryMark = function (path) {
        var url = baseURl + '/rest/v1/raw/repository/mark';
        var params = {
            path: path
        };
        return get(url, params);
    };

    this.searchArticles = function (page, pageSize, path, title, minPercentOfRelations, approved) {
        var url = baseURl + '/rest/v1/raw/repository/searchArticles';
        var params = {
            page: page,
            pageSize: pageSize
        };
        if (!OUC.isEmpty(path)) params.path = path;
        if (!OUC.isEmpty(title)) params.title = title;
        if (!OUC.isEmpty(minPercentOfRelations)) params.minPercentOfRelations = minPercentOfRelations;
        if (!OUC.isEmpty(approved)) params.approved = approved;
        return get(url, params);
    };

    this.saveArticle = function (article) {
        var url = baseURl + '/rest/v1/raw/repository/saveArticle';
        return post(url, article);
    };

    this.selectForDependencyRelation = function (article) {
        var url = baseURl + '/rest/v1/raw/repository/selectForDependencyRelation';
        return post(url, article);
    };

    this.selectForOccurrence = function (article) {
        var url = baseURl + '/rest/v1/raw/repository/selectForOccurrence';
        return post(url, article);
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
