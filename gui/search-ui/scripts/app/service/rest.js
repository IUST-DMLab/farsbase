app.service('RestService', ['$http', function ($http) {
    var self = this;
    this.ingoing = 0;

    self.init = function (rootAddress) {
        baseURl = rootAddress;
    };

    function handelError(error) {
        self.ingoing--;
        console.log(error);
    }

    function handelSuccess(/*data, status, headers, config*/) {
        self.ingoing--;
    }

    function http(req) {
        if (OUC.isEmpty(req.params)) req.params = {};
        req.params.random = new Date().getTime();
        self.ingoing++;

        $('#loading').remove();
        $('body').append('<div id="loading" class="loading"><span>loading...</span></div>');

        $('#loading').fadeIn();
        return $http(req).error(function () {
            handelError();
            $('#loading').fadeOut();
        }).success(function () {
            handelSuccess();
            $('#loading').fadeOut();
        });
    }

    function post(url, data) {
        self.ingoing++;
        return $http.post(url, data).error(handelError).success(handelSuccess);
    }


    /**/

    this.translate = function (keyword) {
        var req = {
            method: 'GET',
            url: mappingUrl + '/translator/rest/v1/node/' + keyword
        };
        //console.log(req);
        return http(req);
    };

    this.getEntityData = function (url) {
        var req = {
            method: 'GET',
            url: mappingUrl + '/entity/rest/v1/getEntityData?url=' + url
        };
        //console.log(req);
        return http(req);
    };

    this.getEntityData2 = function (url) {
        var req = {
            method: 'GET',
            url: mappingUrl + '/entity/rest/v2/getEntityData?url=' + url
        };
        //console.log(req);
        return http(req);
    };

    this.sendFeedback = function (data) {
        var url = feedbackUrl + '/rest/v1/feedback/public/send';
        return post(url, data);
    };

    /**/

    this.searcher = function (keyword) {
        var req = {
            method: 'GET',
            url: baseURl + '/rest/v1/searcher/search',
            params: {
                keyword: keyword
            }
        };
        return http(req);
    };


}]);