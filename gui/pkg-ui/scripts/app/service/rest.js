app.service('RestService', ['$http', '$cookieStore', '$state', function ($http, $cookieStore, $state) {
    let self = this;
    this.ingoing = 0;

    self.init = function (rootAddress) {
        baseURL = rootAddress;
    };

    function onerror(response) {
        if (response.status === 403) {
            $state.go("login");
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

    function get(url, params, headers) {
        params = params || {};
        let authToken = $cookieStore.get('authToken');
        if (authToken) headers = headers || {"x-auth-token": authToken};
        params.random = new Date().getTime();

        let req = {
            method: 'GET',
            url: url,
            headers: headers || {},
            params: params
        };

        self.ingoing++;
        loading.show();
        return $http(req).then(onsuccess, onerror);
    }

    function post(url, data, headers) {
        let authToken = $cookieStore.get('authToken');
        if (authToken) headers = headers || {"x-auth-token": authToken};
        let req = {
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

    // general


    this.getPrefixes = function () {
        let url = mappingsURL + '/mapping/rest/v1/prefixes';
        return get(url);
    };

    // Login

    this.login = function (username, password) {
        let url = baseURL + '/services/rs/v1/users/login';
        let headers = {'Authorization': 'Basic ' + btoa(username + ':' + password)};
        return get(url, {}, headers);
    };

    //

    this.users = {
        load: function (authToken, name, username, pageIndex, pageSize) {
            let url = baseURL + '/services/rs/v1/users/search';
            let headers = {"x-auth-token": authToken};
            let params = {
                name: name,
                username: username,
                page: pageIndex,
                pageSize: pageSize
            };
            return get(url, params, headers);
        },
        save: function (authToken, name, username, permissions, password, identifier) {
            let url = baseURL + '/services/rs/v1/users/edit';
            let headers = {"x-auth-token": authToken};
            let params = {
                name: name,
                username: username,
                permissions: permissions,
                password: password,
                identifier: identifier
            };
            return post(url, params, headers);
        }
    };

    this.profile = {
        load: function (authToken) {
            let url = baseURL + '/services/rs/v1/profile/edit';
            let headers = {"x-auth-token": authToken};
            let params = {};
            return post(url, params, headers);
        },
        updateName: function (authToken, name) {
            let url = baseURL + '/services/rs/v1/profile/edit';
            let headers = {"x-auth-token": authToken};
            let params = {
                name: name
            };
            return post(url, params, headers);
        },
        updatePassword: function (authToken, currentPassword, newPassword) {
            let url = baseURL + '/services/rs/v1/profile/edit';
            let headers = {"x-auth-token": authToken};
            let params = {
                currentPassword: currentPassword,
                newPassword: newPassword
            };
            return post(url, params, headers);
        }
    };

    this.permissions = {
        load: function (authToken) {
            let url = baseURL + '/services/rs/v1/forwards/permission/list';
            let headers = {"x-auth-token": authToken};
            let params = {};
            return get(url, params, headers);
        },
        save: function (authToken, title, description, identifier) {
            let url = baseURL + '/services/rs/v1/forwards/permission/edit';
            let headers = {"x-auth-token": authToken};
            let params = {
                title: title,
                description: description,
                identifier: identifier
            };
            return post(url, params, headers);
        }
    };

    this.forwards = {
        load: function (authToken) {
            let url = baseURL + '/services/rs/v1/forwards/list';
            let headers = {"x-auth-token": authToken};
            let params = {};
            return get(url, params, headers);
        },
        save: function (authToken, source, destination, permissions, urns, identifier) {
            let url = baseURL + '/services/rs/v1/forwards/forward';
            let headers = {"x-auth-token": authToken};
            let params = {
                source: source,
                destination: destination,
                permissions: permissions,
                urns: urns.map((u) => {
                    u.permissions = u.permissions.map(p => p.title);
                    return u;
                }),
                identifier: identifier
            };
            return post(url, params, headers);
        }
    };

    this.ontology = {
        classTree: function (lang, root, depth, label) {
            let url = ontologyURL + '/ontology/rest/v1/classTree';
            // let headers = {"x-auth-token": authToken};
            let params = {
                labelLanguage: lang,
                root: root,
                depth: depth,
                label: label === undefined ? true : label
            };
            return get(url, params);
        },
        queryClasses: function (keyword) {
            let url = ontologyURL + '/ontology/rest/v1/classes';
            let params = {
                page: 0,
                pageSize: 1000,
                keyword: keyword
            };
            return get(url, params);
        },
        getClass: function (classUrl) {
            let url = ontologyURL + '/ontology/rest/v1/classData';
            let params = {
                classUrl: classUrl
            };
            return get(url, params);
        },
        saveClass: function (clazz) {
            let url = ontologyURL + '/ontology/rest/v1/saveClass';
            let params = clazz;
            return post(url, params);
        },

        suggestProperties: function (keyword) {
            let url = ontologyURL + '/ontology/rest/v1/ontologyPredicates';
            let params = {
                page: 0,
                pageSize: 1000,
                keyword: keyword || undefined,
                like: true
            };
            return get(url, params);
        },
        queryProperties: function (keyword, page, pageSize) {
            let url = ontologyURL + '/ontology/rest/v1/properties';
            let params = {
                page: page || 0,
                pageSize: pageSize || 20,
                keyword: keyword || undefined
            };
            return get(url, params);
        },
        getProperty: function (propertyUrl) {
            let url = ontologyURL + '/ontology/rest/v1/propertyData';
            let params = {
                propertyData: propertyUrl
            };
            return get(url, params);
        },
        saveProperty: function (property) {
            let url = ontologyURL + '/ontology/rest/v1/saveProperty';
            let params = property;
            return post(url, params);
        },

        removeClass: function (classUrl) {
            let url = ontologyURL + '/ontology/rest/v1/removeClass?classUrl={0}'.format(classUrl);
            let params = {};
            return post(url, params);
        },
        removeProperty: function (propertyUrl) {
            let url = ontologyURL + '/ontology/rest/v1/removePropertyCompletely?propertyUrl={0}'.format(propertyUrl);
            let params = {};
            return post(url, params);
        },
        removePropertyFromClass: function (classUrl, propertyUrl) {
            let url = ontologyURL + '/ontology/rest/v1/removePropertyFromClass?classUrl={0}&propertyUrl={1}'.format(classUrl, propertyUrl);
            let params = {};
            return post(url, params);
        }

    };

    this.mappings = {
        searchTemplate: function (query) {
            let url = mappingsURL + '/mapping/rest/v2/search';
            let params = {
                page: query.page,
                pageSize: query.pageSize,
                templateName: query.templateName || undefined,
                templateNameLike: query.templateName ? query.templateNameLike : undefined,
                className: query.className || undefined,
                classNameLike: query.className ? query.classNameLike : undefined,
                propertyName: query.propertyName || undefined,
                propertyNameLike: query.propertyName ? query.propertyNameLike : undefined,
                predicateName: query.predicateName || undefined,
                predicateNameLike: query.predicateName ? query.predicateNameLike : undefined,
                approved: query.approved
            };
            return get(url, params);
        },
        searchProperty: function (query) {
            let url = mappingsURL + '/mapping/rest/v2/searchProperty';
            let params = {
                page: query.page,
                pageSize: query.pageSize,
                templateName: query.templateName || undefined,
                templateNameLike: query.templateName ? query.templateNameLike : undefined,
                className: query.className || undefined,
                classNameLike: query.className ? query.classNameLike : undefined,
                propertyName: query.propertyName || undefined,
                propertyNameLike: query.propertyName ? query.propertyNameLike : undefined,
                predicateName: query.predicateName || undefined,
                predicateNameLike: query.predicateName ? query.predicateNameLike : undefined,
                allNull: query.allNull,
                oneNull: query.oneNull,
                approved: query.approved
            };
            return get(url, params);
        },
        saveTemplate: function (object) {
            let url = mappingsURL + '/mapping/rest/v2/insert';
            let data = object;
            return post(url, data);
        },
        suggestPredicates: function (keyword) {
            let url = mappingsURL + '/mapping/rest/v2/predicateProposal';
            let params = {keyword: keyword};

            return get(url, params)
                .then(function (response) {
                    return response.data || [];
                });
        },

        suggestUnits: function (keyword, pageIndex, pageSize) {
            let url = mappingsURL + '/mapping/rest/v2/dataTypes';
            let params = {
                page: pageIndex || 0,
                pageSize: pageSize || 200,
                keyword: keyword
            };

            return get(url, params)
                .then(function (response) {
                    return response.data.data || [];
                });
        },

        suggestTransforms: function (keyword) {
            let url = mappingsURL + '/mapping/rest/v2/transforms';
            let params = {};//{keyword: keyword};

            return get(url, params)
                .then(function (response) {
                    return response.data || [];
                });
        }

    };

    this.search = {
        searchFeedback: function (query) {
            let url = feedbackURL + '/rest/v1/feedback/auth/search';
            let params = {
                page: query.page,
                pageSize: query.pageSize,
                textKeyword: query.textKeyword || undefined,
                queryKeyword: query.queryKeyword || undefined,
                minSendDate: query.minSendDate ? (new Date(query.minSendDate)).getTime() : undefined,
                maxSendDate: query.maxSendDate ? (new Date(query.maxSendDate)).getTime() : undefined,
                approved: query.approved,
                done: query.done
            };
            return get(url, params);
        },
        edit: function (object) {
            let url = feedbackURL + '/rest/v1/feedback/auth/edit';
            let data = object;
            return post(url, data);
        }
    };

    this.reports = {
        bySubject: function (authToken, query) {
            let url = reportsURL + '/services/rs/v1/reports/count/subject';
            //let headers = {"x-auth-token": authToken};
            // todo: username, password must be removed from the following line.
            let headers = {'Authorization': 'Basic ' + btoa('superuser' + ':' + 'superuser')};
            let params = {
                username: query.username || undefined,
                hasVote: query.hasVote || undefined,
                vote: query.vote || undefined,
                page: query.pageIndex || 0,
                pageSize: query.pageSize || 20
            };
            return get(url, params, headers);
        },

        byUser: function (authToken, query) {
            let url = reportsURL + '/services/rs/v1/reports/count/user';
            // todo: username, password must be removed from the following line.
            // let headers = {"x-auth-token": authToken};
            let headers = {'Authorization': 'Basic ' + btoa('superuser' + ':' + 'superuser')};
            let params = {
                username: query.username || undefined,
                hasVote: query.hasVote || undefined,
                vote: query.vote || undefined,
                page: query.pageIndex || 0,
                pageSize: query.pageSize || 20
            };
            return get(url, params, headers);
        },

        byUserVotes: function (authToken, query) {
            let url = reportsURL + '/services/rs/v1/reports/count/users_votes';
            // let headers = {"x-auth-token": authToken};
            // todo: username, password must be removed from the following line.
            let headers = {'Authorization': 'Basic ' + btoa('superuser' + ':' + 'superuser')};
            let params = {
                username: query.username || undefined,
                hasVote: query.hasVote || undefined,
                vote: query.vote || undefined,
                page: query.pageIndex || 0,
                pageSize: query.pageSize || 20
            };
            return get(url, params, headers);
        },

        byTriples: function (authToken, query) {
            let url = reportsURL + '/services/rs/v1/reports/triples';
            // let headers = {"x-auth-token": authToken};
            // todo: username, password must be removed from the following line.
            let headers = {'Authorization': 'Basic ' + btoa('superuser' + ':' + 'superuser')};
            let params = {
                subject: query.subject || undefined,
                username: query.username || undefined,
                hasVote: query.hasVote || undefined,
                vote: query.vote || undefined,
                page: query.pageIndex || 0,
                pageSize: query.pageSize || 20
            };
            return get(url, params, headers);
        }

    };

    this.run = {
        getRunning: function () {
            let url = runURL + '/rs/v1/run/all/running';
            let headers = {};
            let params = {};
            return get(url, params, headers);
        }
    };

}]);

let loading = {
    show: function () {
        $('#loading').remove();
        $('body').append('<div id="loading" class="loading"><span>در حال تبادل اطلاعات ...</span></div>');
        $('#loading').fadeIn();
    },
    hide: function () {
        $('#loading').fadeOut();
    }
};
