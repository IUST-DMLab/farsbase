app
    .controller('MainController', function ($scope, $http, RestService, $cookieStore, $mdSidenav, $filter, $mdDialog) {

        // var at = undefined;
        var at = $cookieStore.get('authToken');
        if (at) {
            $scope.authenticated = true;
            $scope.authToken = at;
        }
        else {
            $scope.authenticated = false;
        }

        $scope.data = {subjects: {}, triples: {}};
        $scope.search = {};
        $scope.modules = ['raw_dependency_pattern', 'wiki', 'web_table_extractor', 'wiki_table_extractor', 'text'];
        $scope.PREFIX = 'http://194.225.227.161/mapping/html/triple.html?subject=';

        $scope.login = function (username, password) {
            $scope.authToken = '';
            $scope.roles = [];
            $scope.username = '';

            RestService.login(username, password).then(SUCCESS, ERROR);

            function SUCCESS(response) {
                $scope.authToken = response.headers('x-auth-token');
                if ($scope.authToken) {
                    $cookieStore.put('authToken', $scope.authToken);
                    $cookieStore.put('roles', response.data);
                    $cookieStore.put('username', username);
                    $scope.roles = response.data;
                    $scope.authenticated = true;
                    $scope.isVipUser = (response.data.indexOf("ROLE_VIPEXPERT") !== -1);
                    $scope.error = undefined;

                    $scope.reload();
                }
            }

            function ERROR(error, status) {
                $scope.authToken = '';
                $scope.authenticated = false;
                $scope.error = {error: error, status: status};
            }
        };

        $scope.logout = function () {
            $cookieStore.put('authToken', '');
            $cookieStore.put('roles', '');
            $cookieStore.put('username', '');
            $scope.authToken = undefined;
            $scope.authenticated = false;
            $scope.roles = [];
            $scope.isVipUser = undefined;
            $scope.data = {subjects: {}, triples: {}};
            $scope.search = {};
        };

        $scope.reload = function () {
            $scope.roles = $cookieStore.get('roles');
            $scope.username = $cookieStore.get('username');

            $('html,body').animate({scrollTop: 0}, 400);
            if ($scope.authenticated)
                RestService.getSubjects($scope.authToken)
                    .then(function (response) {
                        if (typeof response.data === 'string' && response.data.indexOf('action="/login"') !== -1) {
                            $scope.logout();
                            $scope.reload();
                        }

                        if (!response.data.data.length) {
                            $scope.requestMore('auto');
                            return;
                        }

                        for (let item of response.data.data) {
                            RestService.getLabel(item.id)
                                .then(function (d) {
                                    var titleRow = d.data.data.filter(function (q) {
                                        return q.predicate.endsWith('label');
                                    })[0];

                                    //item.caption = ($filter('mapPrefix')(item.id) || item.id);
                                    item.caption = titleRow ? titleRow.object.value : ($filter('mapPrefix')(item.id) || item.id);
                                });
                        }

                        $scope.data.subjects.list = response.data.data;
                        $scope.loadTriples($scope.data.subjects.list[0]);
                    });
        };

        $scope.loadTriples = function (subject) {
            $scope.data.subjects.selected = subject;
            RestService.getTriples($scope.authToken, $scope.data.subjects.selected.id)
                .then(function (response) {
                    if (!response.data.data.length) {
                        $scope.reload();
                        return;
                    }

                    $scope.data.triples.list = response.data.data.sort(compare);

                    var titleRow = $scope.data.triples.list.filter(function (item) {
                        return item.triple.predicate.endsWith('label');
                    })[0];

                    $scope.data.pageTitle = titleRow ? titleRow.triple.object.value : (($filter('mapPrefix')(subject.id) || subject.id));
                    $scope.data.pageTitleUrl = titleRow ? titleRow.triple.subject : subject.id;
                });
        };

        function compare(a, b) {
            if (a.triple.predicate < b.triple.predicate)
                return -1;
            if (a.triple.predicate > b.triple.predicate)
                return 1;
            return 0;
        }

        $scope.submit = function (item, vip) {
            var vote = item.vote || 'None';
            if ($scope.isVipUser && vip && item.vote !== 'None')
                vote = 'VIP' + item.vote;

            RestService.vote($scope.authToken, item.identifier, vote)
                .then(function (data) {
                    $scope.loadTriples($scope.data.subjects.selected);
                });
        };

        function submitAll(vip) {
            let i = 0;
            let items = {};
            for (let item of $scope.data.triples.list) {
                i++;
                let vote = item.vote || 'None';
                if ($scope.isVipUser && vip && item.vote !== 'None')
                    vote = 'VIP' + item.vote;
                items[item.identifier] = vote;
            }

            RestService.batchVote($scope.authToken, items)
                .then(function (data) {
                    $scope.reload();
                });
        }

        $scope.submitAll = function (vip) {
            // Appending dialog to document.body to cover sidenav in docs app
            let confirm = $mdDialog.confirm()
                .title('آیا همه موارد ارسال شود؟')
                .textContent('تمام سطرهای در حال نمایش به سرور ارسال می‌شود.')
                .ariaLabel('Lucky day')
                .ok('تایید')
                .cancel('انصراف');

            $mdDialog.show(confirm)
                .then(function () {
                    console.log('submit all, ', vip);
                    submitAll(vip);
                }, function () {

                });
        };

        $scope.requestMore = function (auto) {

            let exact = $scope.search.exact ? true : false;
            let source = $scope.search.module;
            let subject = ($scope.search.text || '').replace(/\s/g, '_');
            if(exact) subject = 'http://fkg.iust.ac.ir/resource/' + subject;

            RestService.requestMore($scope.authToken, source, subject, exact)
                .then(function (data) {
                    $scope.reload();
                })
                .catch(function (error) {
                    alert('خطایی رخ داده است!');
                });
        };

        $scope.checkAll = function (vote) {
            if ($scope.data.triples.list && $scope.data.triples.list.length) {
                for (let item of $scope.data.triples.list) {
                    item.vote = vote;
                }
            }
        };

        $scope.toggle = function () {
            $mdSidenav('sidebar').toggle();
        };

        var originatorEv;
        $scope.openUserMenu = function ($mdMenu, ev) {
            originatorEv = ev;
            $mdMenu.open(ev);
        };

        //*****
        $scope.reload();
    });
