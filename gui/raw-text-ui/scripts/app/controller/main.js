app.controller('MainController', function ($scope, $http, $window, RestService,
                                           $cookieStore, $mdSidenav, $timeout,
                                           $filter, $mdDialog, $mdToast, $localStorage) {

    $scope.username = localStorage.getItem('username');
    if (!$scope.username) {
        $scope.username = RestService.getTestUser();
        if (!$scope.username) return;
    }

    $scope.colors = {
        null: 'indigo',
        true: 'green',
        false: 'red'
    };

    $scope.params = {
        predicate: null,
        minOccurrence: null,
        approved: null,
        selectedTab: 0,
        assignee: $scope.username,
        assignAssignee: $scope.username
    };

    $scope.cardSearch = {
        page: 1
    };

    $scope.predicateSearch = {
        page: 1,
        predicate: null
    };

    $scope.patternSearch = {
        page: 1
    };

    $scope.go = function (page) {
        $scope.cardSearch.page = page - 1;
        $timeout(function () {
            RestService.getTriples(page - 1, 20, $scope.params.predicate, !$scope.params.exact,
                $scope.params.minOccurrence, $scope.params.approved, $scope.params.assignee)
                .then(function (response) {
                    $scope.data = response.data.page;
                    $scope.numberOfApproved = response.data.numberOfApproved;
                    $scope.numberOfRejected = response.data.numberOfRejected;
                    $scope.data.pageNo = $scope.data.number + 1;
                    for (var i = 0; i < $scope.data.content.length; i++)
                        $scope.data.content[i].toShow =
                            $scope.data.content[i].generalizedSentence
                                .replace('$SUBJ', '<span class="subject">' + $scope.data.content[i].subject + ' :S</span>')
                                .replace('$OBJ', '<span class="object">' + $scope.data.content[i].object + ' :O</span>');
                    console.log($scope.data)
                });
        }, 200);
    };

    $scope.goPredicates = function (page) {
        $scope.predicateSearch.page = page - 1;
        RestService.goPredicates(page - 1, 50, $scope.predicateSearch.predicate)
            .then(function (response) {
                $scope.predicates = response.data;
                $scope.predicates.pageNo = $scope.predicates.number + 1;
            });
    };

    $scope.assigneeCount = function (index) {
        RestService.assigneeCount($scope.predicates.content[index].predicate)
            .then(function (response) {
                $scope.predicates.content[index].assignees = response.data;
            });
    };

    $scope.getUsers = function () {
        RestService.listUsers()
            .then(function (response) {
                $scope.users = response.data;
                console.log($scope.data)
            });
    };

    $scope.assign = function (switchSearch, assignee, predicate, count) {
        if (!assignee) assignee == $scope.username;
        RestService.assign(assignee, predicate, count)
            .then(function (response) {
                var message = "";
                if (response.data > 0) {
                    $scope.go($scope.cardSearch.page + 1);
                    message = "تعداد " + response.data + " کارت جدید به کاربر اختصاص پیدا کرد.";

                    if (switchSearch) {
                        $scope.params.st = 0;
                        $scope.params.predicate = predicate;
                        $scope.params.assignee = assignee;
                        $scope.params.exact = true;
                        $scope.go(1);
                    }
                } else {
                    message = "هیچ کارتی به کاربر اختصاص پیدا نکرد.";
                }
                $mdToast.show(
                    $mdToast.simple()
                        .textContent(message)
                        .position("top")
                        .hideDelay(3000)
                );
            });
    };

    $scope.vote = function (x, approved) {
        RestService.approve(x.id, approved)
            .then(function (response) {
                $scope.go($scope.cardSearch.page + 1);
            });
    };

    $scope.rulesOptions = {
        selected: null,
        text: 'هادی ساعی در شهرری متولد شد.'
    };

    $scope.getRules = function () {
        RestService.rules()
            .then(function (response) {
                $scope.rules = response.data.content
            });
    };

    $scope.newRule = function () {
        $scope.rulesOptions.selected = {
            id: null,
            rule: '',
            approved: false
        }
    };

    $scope.editRule = function () {
        if (!OUC.isEmpty($scope.rulesOptions.selected))
            RestService.editRule($scope.rulesOptions.selected)
                .then(function (response) {
                    if ($scope.rulesOptions.selected.id === null)
                        $scope.getRules();
                    $scope.rulesOptions.selected.id = response.data.id;
                });
    };

    $scope.removeRule = function () {
        if (!OUC.isEmpty($scope.rulesOptions.selected))
            RestService.removeRule($scope.rulesOptions.selected.id)
                .then(function (response) {
                    $scope.getRules();
                });
    };

    $scope.ruleTest = function () {
        var data = {
            "predicates": [],
            "rules": [],
            "text": $scope.rulesOptions.text
        };
        for (var i = 0; i < $scope.rules.length; i++)
            if ($scope.rules[i].approved) data.rules.push({
                rule: $scope.rules[i].rule,
                predicate: $scope.rules[i].predicate
            });
        RestService.ruleTest(data)
            .then(function (response) {
                $scope.resultRules = JSON.stringify(response.data, null, 2);
            });
    };

    $scope.goPatterns = function (page, callback) {
        $scope.patternSearch.page = page - 1;
        RestService.goPatterns(page - 1, 20)
            .then(function (response) {
                $scope.patterns = response.data;
                $scope.patterns.pageNo = $scope.patterns.number + 1;
                if (callback !== undefined) callback();
            });
    };

    $scope.editPattern = function (index, data) {
        $scope.currentPattern = {
            index: index,
            data: data
        };
        $scope.params.st = 1;
        RestService.dependencyParse(data.samples[0])
            .then(function (response) {
                $scope.currentPattern.dependencyTree = response.data;
                var conll = "";
                var s = "";
                var sentence = response.data;
                for (var j = 0; j < sentence.length; j++) {
                    var word = sentence[j];
                    conll += (word.position + "\t" + word.word + "\t" + word.lemma + "\t" + word.pos
                        + "\t" + word.pos + "\t" + word.features + "\t" + (word.head === -1 ? '0' : word.head)
                        + "\t" + word.relation + "\t-\t-\n");
                    s += (word.word + " ");
                }
                $scope.currentPattern.dependencyTreeConll = conll;
                var svg = document.getElementById('sample-dep-tree');
                angular.forEach($scope.currentPattern.data.relations, function (relation) {
                    relation.ssw = {};
                    angular.forEach(relation.subject, function (i) {
                        relation.ssw[i] = true;
                    });
                    relation.psw = {};
                    angular.forEach(relation.predicate, function (i) {
                        relation.psw[i] = true;
                    });
                    relation.osw = {};
                    angular.forEach(relation.object, function (i) {
                        relation.osw[i] = true;
                    });
                });
                $scope.rebuildTuples();
                window.drawTree(svg, conll);
            });
    };

    $scope.nextPattern = function () {
        if ($scope.currentPattern.index === 19) {
            if ($scope.patterns.pageNo < $scope.patterns.totalPages) {
                $scope.goPatterns($scope.patterns.pageNo + 1, function () {
                    $scope.editPattern(0, $scope.patterns.content[0]);
                });
            }
        } else $scope.editPattern($scope.currentPattern.index + 1,
            $scope.patterns.content[$scope.currentPattern.index + 1]);
    };

    $scope.previousPattern = function () {
        if ($scope.currentPattern.index === 0) {
            if ($scope.patterns.pageNo > 1) {
                $scope.goPatterns($scope.patterns.pageNo - 1, function () {
                    $scope.editPattern(19, $scope.patterns.content[19]);
                });
            }
        } else $scope.editPattern($scope.currentPattern.index - 1,
            $scope.patterns.content[$scope.currentPattern.index - 1]);
    };

    $scope.switchSubject = function (relation) {
        relation.subject = [];
        for (var key in relation.ssw) if (relation.ssw[key]) relation.subject.push(parseInt(key));
        $scope.rebuildTuples();
    };

    $scope.switchPredicate = function (relation) {
        relation.predicate = [];
        for (var key in relation.psw) if (relation.psw[key]) relation.predicate.push(parseInt(key));
        $scope.rebuildTuples();
    };

    $scope.switchObject = function (relation) {
        relation.object = [];
        for (var key in relation.osw) if (relation.osw[key]) relation.object.push(parseInt(key));
        $scope.rebuildTuples();
    };

    $scope.rebuildTuples = function () {
        $scope.currentPattern.tuples = [];
        angular.forEach($scope.currentPattern.data.relations, function (relation) {
            if (relation.subject && relation.subject.length > 0 &&
                (relation.manualPredicate || (relation.predicate && relation.predicate.length > 0)) &&
                relation.object && relation.object.length > 0) {
                var tree = $scope.currentPattern.dependencyTree;
                var found = false;
                if (relation.mandatoryWord) {
                    angular.forEach(tree, function (p) {
                        if (p.word === relation.mandatoryWord) found = true;
                    });
                }
                if (relation.mandatoryWord && !found) return;
                var tuple = {
                    subject: '',
                    predicate: '',
                    object: ''
                };
                angular.forEach(relation.subject, function (v) {
                    tuple.subject += (tree[v].word + ' ');
                });
                if (relation.manualPredicate != null)
                    tuple.predicate = relation.manualPredicate;
                else
                    angular.forEach(relation.predicate, function (v) {
                        tuple.predicate += (tree[v].word + ' ');
                    });
                angular.forEach(relation.object, function (v) {
                    tuple.object += (tree[v].word + ' ');
                });
                $scope.currentPattern.tuples.push(tuple);
            }
        });
    };

    $scope.addRelation = function () {
        if ($scope.currentPattern) {
            $scope.currentPattern.data.relations.push({
                subject: [],
                predicate: [],
                object: [],
                accuracy: 0.0
            });
            $scope.params.relationIndex = $scope.currentPattern.data.relations.length - 1;
            toast('یک رابطه به الگو اضافه شد.');
        }
    };

    $scope.removeRelation = function () {
        if ($scope.currentPattern
            && $scope.currentPattern.data.relations.length > $scope.params.relationIndex) {
            $scope.currentPattern.data.relations.splice($scope.params.relationIndex, 1);
            // $scope.params.relationIndex = 0;
            $scope.rebuildTuples();
            toast('رابطه از الگو حذف شد.');
        }
    };

    $scope.removeAllRelations = function () {
        if ($scope.currentPattern && $scope.currentPattern.data.relations.length > 0) {
            $scope.currentPattern.data.relations = [];
            // $scope.params.relationIndex = 0;
            $scope.rebuildTuples();
            toast('همه روابط از الگو حذف شد.');
        }
    };

    $scope.savePattern = function () {
        RestService.savePattern($scope.currentPattern.data)
            .then(function (response) {
                if (response.data) toast('الگو ذخیره شد.');
                console.log(response);
            });
    };

    $scope.predictByPattern = function (text) {
        RestService.predictByPattern(text)
            .then(function (response) {
                $scope.predicted = response.data;
            });
    };

    $scope.fkgfy = function (text) {
        RestService.fkgfy(text)
            .then(function (response) {
                $scope.fkgfied = response.data;
                if (!self.params) self.params = {};
                $scope.params.fkgfyTab = 0;
            });
    };

    $scope.extractTriples = function (text) {
        RestService.extractTriples(text)
            .then(function (response) {
                $scope.extractedTriples = response.data;
                $scope.noExtracted = $scope.extractedTriples.size === 0;
                if (!self.params) self.params = {};
                $scope.params.fkgfyTab = 1;
            });
    };

    $scope.fkgfyDetailed = function (text) {
        RestService.fkgfy(text)
            .then(function (response) {
                $scope.fkgfied = response.data;
                var entities = [];
                for (var i = 0; i < $scope.fkgfied.length; i++) {
                    var sentence = $scope.fkgfied[i];
                    for (var j = 0; j < sentence.length; j++) {
                        if (!sentence[j].resource) continue;
                        var iri = sentence[j].resource.iri;
                        if (entities.indexOf(iri) === -1) entities.push(iri);
                    }
                }
                RestService.getEntities(entities).then(function (entitiesResponse) {
                    for (var i = 0; i < response.data.length; i++) {
                        var sentence = $scope.fkgfied[i];
                        for (var j = 0; j < sentence.length; j++) {
                            if (!sentence[j].resource) continue;
                            var iri = sentence[j].resource.iri;
                            var index = entities.indexOf(iri);
                            if (index !== -1) $scope.fkgfied[i][j].entityData = entitiesResponse.data[index];
                        }
                    }
                    if (!self.params) self.params = {};
                    $scope.params.fkgfyTab = 0;
                });
            });
    };

    $scope.repository = {
        path: []
    };

    var getAsString = function (path) {
        var builder = '';
        angular.forEach(path, function (part) {
            builder += (part + '/');
        });
        return builder;
    };

    $scope.getRepositoryLs = function () {
        RestService.getRepositoryLs(getAsString($scope.repository.path))
            .then(function (response) {
                $scope.repository.ls = response.data;
                console.log(response);
            });
    };

    $scope.gotoPath = function (name) {
        $scope.repository.path.push(name);
        $scope.getRepositoryLs();
    };

    $scope.gotoUp = function () {
        $scope.repository.path.pop();
        $scope.getRepositoryLs();
    };

    $scope.openDocument = function (index) {
        $scope.lastSelectedDocumentIndex = index;
        var name = $scope.repository.ls[index].name;
        RestService.getRepositoryGet(getAsString($scope.repository.path) + name)
            .then(function (response) {
                $scope.repository.tab = 1;
                $scope.repository.docName = name;
                $scope.repository.document = response.data;
                console.log(response.data);
            });
    };

    $scope.nextDocument = function () {
        if ($scope.lastSelectedDocumentIndex < $scope.repository.ls.length - 1)
            $scope.openDocument($scope.lastSelectedDocumentIndex + 1);
    };

    $scope.previousDocument = function () {
        if ($scope.lastSelectedDocumentIndex > 1)
            $scope.openDocument($scope.lastSelectedDocumentIndex - 1);
    };

    $scope.markDocument = function () {
        RestService.getRepositoryMark(getAsString($scope.repository.path) + $scope.repository.docName)
            .then(function (response) {
                console.log(response.data);
                if (response.data) toast('سند اضافه شد.');
                else toast('سند اضافه نشد. آیا قبلا این سند را اضافه کرده بودید؟');
            });
    };

    $scope.searchArticles = function (page) {
        if (page === undefined) page = 1;
        RestService.searchArticles(page - 1, 10,
            $scope.repository.pathSearch, $scope.repository.title,
            $scope.repository.minPercentOfRelations, $scope.repository.approved)
            .then(function (response) {
                console.log(response.data);
                $scope.repository.articles = response.data;
                $scope.repository.articles.pageNo = $scope.repository.articles.number + 1;
            });
    };

    $scope.$watch('repository.tab', function (current, old) {
        if (2 === current) $scope.searchArticles();
    });

    $scope.editArticle = function (index) {
        $scope.repository.selectedArticleIndex = index;
        $scope.repository.selectedArticle = $scope.repository.articles.content[index];
        $scope.repository.tab = 3;
    };

    $scope.setNumberOfRelations = function (index, number) {
        var sum = 0;
        $scope.repository.selectedArticle.sentences[index].numberOfRelations = number;
        angular.forEach($scope.repository.selectedArticle.sentences, function (sentence) {
            sum += sentence.numberOfRelations;
        });
        $scope.repository.selectedArticle.numberOfRelations = sum;
        $scope.repository.selectedArticle.percentOfRelations = $scope.repository.selectedArticle.numberOfRelations /
            $scope.repository.selectedArticle.numberOfSentences;
    };

    $scope.setRelations = function (index) {
        $mdDialog.show({
            controller: DialogController,
            templateUrl: 'html/editRelation.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            locals: {
                sentence: $scope.repository.selectedArticle.sentences[index]
            }
        }).then(function (answer) {
            console.log(answer);
        }, function () {
            console.log('canceled');
        });
    };

    $scope.saveArticle = function () {
        RestService.saveArticle($scope.repository.selectedArticle)
            .then(function (response) {
                if (response.data) toast('مقاله ذخیره شد.');
                else toast('ذخیره نشد.');
            });
    };

    function DialogController($scope, $mdDialog, sentence) {
        $scope.sentence = sentence;
        $scope.relation = {
            subject: [],
            predicate: [],
            object: []
        };

        angular.forEach($scope.sentence.tokens, function (token) {
            $scope.relation.subject.push(false);
            $scope.relation.predicate.push(false);
            $scope.relation.object.push(false);
        });

        var buildSelection = function () {
            var selection = {
                tokens: $scope.sentence.tokens,
                manualPredicate: $scope.manualPredicate,
                subject: [],
                predicate: [],
                object: []
            };
            var i;
            for (i = 0; i < $scope.relation.subject.length; i++) {
                if ($scope.relation.subject[i])
                    selection.subject.push(i);
            }
            for (i = 0; i < $scope.relation.predicate.length; i++) {
                if ($scope.relation.predicate[i])
                    selection.predicate.push(i);
            }
            for (i = 0; i < $scope.relation.object.length; i++) {
                if ($scope.relation.object[i])
                    selection.object.push(i);
            }
            return selection;
        };

        $scope.hide = function () {
            $mdDialog.hide();
        };

        $scope.addAsCard = function () {
            RestService.selectForOccurrence(buildSelection())
                .then(function (response) {
                    sentence.numberOfRelations++;
                    toast('به سه‌تایی‌های طلایی افزوده شد.');
                });
        };

        $scope.addAsDepRel = function () {
            RestService.selectForDependencyRelation(buildSelection())
                .then(function (response) {
                    sentence.numberOfRelations++;
                    toast('به الگوهای وابستگی افزوده شد.');
                });
        };
    }

    $scope.hoverDiv = function (word) {
        $scope.selectedWord = word;
    };


    var toast = function (message) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(message)
                .position("top")
                .hideDelay(3000)
        );
    };

    $scope.switch = function (tab) {
        if (tab === null) {
            $window.location = "../pkg";
            return;
        }
        $scope.storage.selectedTab = tab;
        $scope.tab = tab;
        if (tab === 'triples') {
            $scope.getUsers();
            $scope.go(1);
        }
        else if (tab === 'rules') $scope.getRules();
        else if (tab === 'repository') $scope.getRepositoryLs();
        else $scope.goPatterns(1);
    };


    $scope.storage = $localStorage.$default({
        selectedTab: 'triples',
        showPos: false,
        showAmbiguities: false
    });

    // $scope.switch('patterns');
    $scope.switch($scope.storage.selectedTab);
    // $scope.switch('rules');
});
