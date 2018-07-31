app.service('RestService', ['$http', function ($http) {
    var self = this;
    this.ingoing = 0;

    self.init = function (rootAddress) {
        baseURL = rootAddress;
    };

    function handelError(error) {
        self.ingoing--;
        loading.hide();
        console.log(error);
    }

    function handelSuccess(/*data, status, headers, config*/) {
        self.ingoing--;
        loading.hide();
    }

    function http(req) {
        if (OUC.isEmpty(req.params)) req.params = {};
        req.params.random = new Date().getTime();
        self.ingoing++;

        loading.show();
        return $http(req).error(handelError).success(handelSuccess);
    }

    function post(url, data) {
        self.ingoing++;
        loading.show();
        return $http.post(url, data).error(handelError).success(handelSuccess);
    }

    /**************************************************/

    this.addMultipleTagToDisk = function (data) {
        return post(baseURL + 'rest/tags/addMultiple', data);
    };

    this.translationRoot = function () {
        var req = {
            method: 'GET',
            url: baseURL + 'translator/rest/v1/root',
            params: {}
        };
        return http(req);
    };

    this.translationSearch = function (page, pageSize, name, like, approved, hasFarsi) {
        var req = {
            method: 'GET',
            url: baseURL + 'translator/rest/v1/search',
            params: {
                page: page,
                pageSize: pageSize
            }
        };
        if (name != null) req.params.name = name;
        if (like != null) req.params.like = like;
        if (approved != null && approved) req.params.approved = approved;
        if (hasFarsi != null && hasFarsi) req.params.hasFarsi = hasFarsi;
        return http(req);
    };

    this.translationTranslate = function (name, faLabel, faOtherLabels, note, approved) {
        var req = {
            method: 'GET',
            url: baseURL + 'translator/rest/v1/translate',
            params: {
                name: name,
                faLabel: OUC.isEmpty(faLabel) ? "" : faLabel,
                faOtherLabels: OUC.isEmpty(faOtherLabels) ? "" : faOtherLabels,
                note: OUC.isEmpty(note) ? "" : note,
                approved: OUC.isEmpty(approved) ? false : approved
            }
        };
        return http(req);
    };

    this.translate = function (keyword) {
        var req = {
            method: 'GET',
            url: baseURL + 'translator/rest/v1/node/' + keyword
        };
        return http(req);
    };

    this.getPrefixes = function () {
        var req = {
            method: 'GET',
            url: baseURL + 'mapping/rest/v1/prefixes'
        };

        return http(req);
    };

    /* Template Mapping */

    this.ontologyClassSearch = function (page, pageSize, keyword) {
        var req = {
            method: 'GET',
            url: baseURL + 'templateMapping/rest/v1/searchOntologyClass',
            params: {
                page: page,
                pageSize: pageSize,
                keyword: keyword || undefined
            }
        };
        return http(req).then(function (res) {
            return res.data || [];
        });
    };

    this.ontologyBySubject = function (subject) {
        var req = {
            method: 'GET',
            url: xxxURL + 'rs/v2/ontology/search',
            params: {
                subject: subject || ''
            }
        };
        return http(req);
    };

    this.templateMappingSearch = function (page, pageSize, templateName, className, like, approved) {
        var req = {
            method: 'GET',
            url: baseURL + 'templateMapping/rest/v1/search',
            params: {
                page: page,
                pageSize: pageSize
            }
        };
        if (templateName) req.params.templateName = templateName;
        if (className) req.params.className = className;
        if (like != null) req.params.like = like;
        if (approved != null && approved != undefined) req.params.approved = approved;
        return http(req);
    };

    this.templateMappingSave = function (item) {
        var req = {
            method: 'GET',
            url: baseURL + 'templateMapping/rest/v1/editByGet',
            params: {
                id: item.id,
                approved: OUC.isEmpty(item.approved) ? false : item.approved,
                ontologyClass: OUC.isEmpty(item.ontologyClass) ? "" : item.ontologyClass,
                templateName: OUC.isEmpty(item.templateName) ? "" : item.templateName,
                language: OUC.isEmpty(item.language) ? "" : item.language
            }
        };
        return http(req);
    };


    /* Property Mapping */

    this.ontologyPropertyNameSearch = function (page, pageSize, keyword) {
        var req = {
            method: 'GET',
            url: baseURL + 'mapping/rest/v1/searchOntologyPropertyName',
            params: {
                page: page,
                pageSize: pageSize,
                keyword: keyword || undefined
            }
        };
        return http(req).then(function (res) {
            return res.data || [];
        });
    };

    this.propertyMappingSearch = function (page, pageSize, templateName, className, templateProperty, ontologyProperty, like, approved, status) {

        var req = {
            method: 'GET',
            url: baseURL + 'mapping/rest/v1/search',
            params: {
                page: page,
                pageSize: pageSize
            }
        };
        if (templateName) req.params.templateName = templateName;
        if (className) req.params.className = className;
        if (templateProperty) req.params.templateProperty = templateProperty;
        if (ontologyProperty) req.params.ontologyProperty = ontologyProperty;

        if (approved != null && approved != undefined) req.params.approved = approved;
        if (status) req.params.status = status;

        if (like != null) req.params.like = like;

        return http(req);
    };

    this.propertyMappingSave = function (item) {
        var req = {
            method: 'GET',
            url: baseURL + 'mapping/rest/v1/editByGet',
            params: {
                id: item.id,
                approved: OUC.isEmpty(item.approved) ? false : item.approved,
                status: OUC.isEmpty(item.status) ? "" : item.status,
                language: OUC.isEmpty(item.language) ? "" : item.language,
                className: OUC.isEmpty(item.ontologyClass) ? "" : item.ontologyClass,
                ontologyProperty: OUC.isEmpty(item.ontologyProperty) ? "" : item.ontologyProperty,
                templateName: OUC.isEmpty(item.templateName) ? "" : item.templateName,
                templateProperty: OUC.isEmpty(item.templateProperty) ? "" : item.templateProperty
            }
        };
        return http(req);
    };

    /* Triples */

    this.triplesSearch = function (page, pageSize, context, subject, predicate, object) {
        var req = {
            method: 'GET',
            url: xxxURL + 'rs/v1/triples/search?predicate=http://www.w3.org/2000/01/rdf-schema%23label',
            params: {
                page: page,
                pageSize: pageSize
            }
        };

        if (object) req.params.object = object;

        return http(req);
    };

    this.tripleBySubject = function (subject) {
        var req = {
            method: 'GET',
            url: xxxURL + 'rs/v1/triples/search',
            params: {
                subject: subject
            }
        };

        return http(req);
    };

    this.tripleBySubject2 = function (subject) {
        var req = {
            method: 'GET',
            url: xxxURL + 'rs/v2/subjects/get',
            params: {
                subject: subject
            }
        };

        return http(req);
    };

    //  http://dmls.iust.ac.ir:8090/virtuoso/rest/v1/getTriplesOfSubject?subjectUrl=http://fkg.iust.ac.ir/ontology/Capital
    this.triplesOfSubject = function (subject) {
        var req = {
            method: 'GET',
            url: baseURL + 'virtuoso/rest/v1/getTriplesOfSubject',
            params: {
                subjectUrl: subject
            }
        };

        return http(req);
    };

    // mappings

    this.getMappings = function (page, pageSize) {
        var url = xxxURL + 'rs/v1/mappings/all?page=' + page + '&pageSize=' + pageSize;
        return post(url, {});
    };

    this.saveMappings = function (object) {
        var url = xxxURL + 'rs/v1/mappings/template/insert';
        var data = object;
        return post(url, data);
    };

    this.predicatesSearch = function (keyword) {
        var req = {
            method: 'GET',
            url: xxxURL + 'rs/v1/mappings/experts/predicates',
            params: {
                keyword: keyword
            }
        };

        return http(req).then(function (response) {
            return response.data || [];
        });
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
