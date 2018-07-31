app.controller('MainController', function ($scope, $location, $routeParams, RestService, $uibModal) {
    $scope.filter = {};
    $scope.go = go;
    $scope.load = load;

    var keyword = getParameterByName('keyword');
    //console.log(keyword);
    if (keyword) {
        // load(keyword);
    }

    function go() {
        var kw = $scope.filter.keyword;
        //console.log('go : ', kw);
        if (kw) {
            $location.url('/?keyword=' + kw);
            load(kw);
        }
    }

    function load(kw) {
        kw = kw || getParameterByName('keyword');
        if(!kw) return;
        RestService.searcher(kw)
            .success(function (results) {
                $scope.results = results;
                $scope.filter.keyword = kw;

                var groups = _.groupBy(results.entities, 'resultType');
                var relationals = _.groupBy(groups['RelationalResult'], 'description');
                var similarities = _.groupBy(groups['Similar'], 'description');
                var entities = groups['Entity'];

                checkMode(relationals);
                checkMode(similarities, 'abstract');

                if (entities && entities[0] && entities[0].link) {
                    RestService.getEntityData2(entities[0].link)
                        .success(function (entity) {
                            entities[0].data = entity;
                            update(relationals, entities, similarities);
                        });
                }
                else {
                    update(relationals, entities, similarities);
                }
            });

        function update(relationals, entities, similarities) {
            $scope.relationalResults = relationals;
            $scope.entities = entities;
            $scope.similarities = similarities;
            //console.log(similarities);
        }

    }


    $scope.loadEntity = function (entity) {
        RestService.getEntityData(entity.link)
            .success(function (data) {
                entity.data = data;
            });
    };

    $scope.openFeedback = function () {
        var parentElem = undefined;
        var modalInstance = $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: './templates/feedback.html',
            controller: 'FeedbackCtrl',
            //controllerAs: '$ctrl',
            //size: size,
            appendTo: parentElem,
            resolve: {
                items: function () {
                    //return items;
                }
            }
        });

        modalInstance.result
            .then(function () {

            }, function () {

            });
    };

    $scope.isEmpty = function(obj){
        return angular.equals(obj, {});
    };

    function checkMode(obj, m) {
        for (let key in obj) {
            if (obj.hasOwnProperty(key)) {
                let res = obj[key];
                let count = _.sum(res.projection('photoUrls').map(x => x.length ? 1 : 0));
                let mode = ((res.length * 0.7 < count) ) ? 'large' : 'abstract';
                res.mode = m|| mode;
                //console.log(res.length, count, (res.length / count), (res.length / count) <= 2, mode);
            }
        }
    }

});

app.controller('FeedbackCtrl', function ($scope, $uibModalInstance, RestService) {

    $scope.fb = {};

    // RestService.

    $scope.ok = function () {

        var data = {
            "name": $scope.fb.name,
            "email": $scope.fb.email,
            "query": $scope.fb.query,
            "text": $scope.fb.text
        };

        RestService.sendFeedback(data)
            .success(function (response) {
                $uibModalInstance.close();
            });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

});

app.controller('ExportController', function ($scope, $location, $routeParams, RestService) {
    $scope.filter = {};
    $scope.data = {};
    $scope.load = load;

    var keyword = getParameterByName('keyword');
    if (keyword) {
        load(keyword);
    }

    function load(kw) {
        kw = kw || getParameterByName('keyword');
        RestService.searcher(kw)
            .success(function (results) {
                $scope.results = results;
                $scope.filter.keyword = kw;

                var groups = _.groupBy(results.entities, 'resultType');

                var _entities = groups['Entity'];

                if (_entities[0] && _entities[0].link) {
                    RestService.getEntityData(_entities[0].link)
                        .success(function (entity) {
                            _entities[0].data = entity;

                            $scope.entity = _entities[0];
                        });
                }
                else {
                    $scope.entity = _entities[0];
                }
            });
    }


    $scope.loadEntity = function (entity) {
        RestService.getEntityData(entity.link)
            .success(function (data) {
                entity.data = data;
            });
    };
});


function fixImage(element, entity) {
    console.log($(element));
    $(element).css({'background-image': 'url(\'' + entity.data.image + '\')'})
}

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

