app
    .controller('ReportController', function ($scope, RestService, $state, $cookieStore, $mdPanel, $location) {
        $scope.getSelectedTabIndex = () => {
            return $state.current.data.index;
        };
    })

    .controller('ReportSubjectsController', function ($scope, RestService, $state, $cookieStore, $mdPanel, $location) {
        $scope.query = {
            username: undefined,
            hasVote: undefined,
            vote: undefined,
            pageIndex: 0,
            pageSize: 20
        };

        $scope.paging = {
            pageIndex: 0,
            current: 1
        };

        $scope.onPageChanged = function () {
            if (!$scope.paging) return;
            let pageIndex = $scope.paging.current - 1;
            $scope.load(pageIndex);
        };

        $scope.load = function (pageIndex) {
            let authToken = $cookieStore.get('authToken');
            $scope.query.pageIndex = pageIndex || 0;

            RestService.reports.bySubject(authToken, $scope.query)
                .then((response) => {
                    $scope.items = response.data.data;
                    $scope.loaded = true;
                    $scope.err = undefined;
                    $scope.paging = {
                        pageIndex: response.data.page,
                        current: response.data.page + 1,
                        pageCount: response.data.pageCount,
                        pageSize: response.data.pageSize,
                        totalSize: response.data.totalSize
                    }
                })
                .catch(function (err) {
                    $scope.items = undefined;
                    $scope.loaded = false;
                    $scope.err = err;
                });
        };

        // $scope.showTriplesBySubject = function (item) {
        //     console.log(item.id);
        //     $state.go('reports.triples', {subject: item.id});
        // };


        $scope.showItem = function (item, ev) {

            let position = $mdPanel.newPanelPosition()
                .absolute()
                .center();

            $mdPanel.open({
                attachTo: angular.element(document.body),
                controller: DialogController,
                disableParentScroll: false,
                templateUrl: './templates/reports/triples-panel.html',
                hasBackdrop: true,
                panelClass: 'dialog-panel-big',
                position: position,
                trapFocus: true,
                zIndex: 50,
                targetEvent: ev,
                clickOutsideToClose: true,
                escapeToClose: true,
                focusOnOpen: true,
                locals: {
                    item: item
                }
            })
                .then(function (p) {
                    _dialogPanels['report-panel'] = p;
                });


        };

        function DialogController($scope, $mdPanel, item) {

            let authToken = $cookieStore.get('authToken');
            $scope.query = {
                subject : item.id,
                username: undefined,
                hasVote: undefined,
                vote: undefined,
                pageIndex: 0,
                pageSize: 2000
            };
            RestService.reports.byTriples(authToken, $scope.query)
                .then((response) => {
                    $scope.selectedItem = item;
                    $scope.items = response.data.data;
                    $scope.loaded = true;
                    $scope.err = undefined;
                    $scope.paging = {
                        pageIndex: response.data.page,
                        current: response.data.page + 1,
                        pageCount: response.data.pageCount,
                        pageSize: response.data.pageSize,
                        totalSize: response.data.totalSize
                    }
                })
                .catch(function (err) {
                    $scope.items = undefined;
                    $scope.loaded = false;
                    $scope.err = err;
                });

            $scope.close = function () {
                closeDialogPanel('report-panel');
            };

        }

    })

    .controller('ReportSummariesController', function ($scope, RestService, $state, $cookieStore, $mdPanel, $location) {
        $scope.query = {
            username: undefined,
            hasVote: undefined,
            vote: undefined,
            pageIndex: 0,
            pageSize: 20
        };

        $scope.paging = {
            pageIndex: 0,
            current: 1
        };

        $scope.onPageChanged = function () {
            if (!$scope.paging) return;
            let pageIndex = $scope.paging.current - 1;
            $scope.load(pageIndex);
        };

        $scope.load = function (pageIndex) {
            let authToken = $cookieStore.get('authToken');
            $scope.query.pageIndex = pageIndex || 0;

            RestService.reports.byUser(authToken, $scope.query)
                .then((response) => {
                    $scope.items = response.data.data;
                    $scope.loaded = true;
                    $scope.err = undefined;
                    $scope.paging = {
                        pageIndex: response.data.page,
                        current: response.data.page + 1,
                        pageCount: response.data.pageCount,
                        pageSize: response.data.pageSize,
                        totalSize: response.data.totalSize
                    }
                })
                .catch(function (err) {
                    $scope.items = undefined;
                    $scope.loaded = false;
                    $scope.err = err;
                });
        };

        $scope.showItem = function (item, ev) {

            let position = $mdPanel.newPanelPosition()
                .absolute()
                .center();

            $mdPanel.open({
                attachTo: angular.element(document.body),
                controller: DialogController,
                disableParentScroll: false,
                templateUrl: './templates/reports/votes-panel.html',
                hasBackdrop: true,
                panelClass: 'dialog-panel-big',
                position: position,
                trapFocus: true,
                zIndex: 50,
                targetEvent: ev,
                clickOutsideToClose: true,
                escapeToClose: true,
                focusOnOpen: true,
                locals: {
                    item: item
                }
            })
                .then(function (p) {
                    _dialogPanels['report-panel'] = p;
                });


        };

        function DialogController($scope, $mdPanel, item) {

            let authToken = $cookieStore.get('authToken');
            $scope.query = {
                username: item.user.username,
                hasVote: undefined,
                vote: undefined,
                pageIndex: 0,
                pageSize: 2000
            };
            RestService.reports.byUserVotes(authToken, $scope.query)
                .then((response) => {
                    $scope.selectedItem = item;
                    $scope.items = response.data.data;
                    $scope.loaded = true;
                    $scope.err = undefined;
                    $scope.paging = {
                        pageIndex: response.data.page,
                        current: response.data.page + 1,
                        pageCount: response.data.pageCount,
                        pageSize: response.data.pageSize,
                        totalSize: response.data.totalSize
                    }
                })
                .catch(function (err) {
                    $scope.items = undefined;
                    $scope.loaded = false;
                    $scope.err = err;
                });

            $scope.close = function () {
                closeDialogPanel('report-panel');
            };

        }
    })

    .controller('ReportVotesController', function ($scope, RestService, $state, $cookieStore, $mdPanel, $location) {
        $scope.query = {
            username: undefined,
            hasVote: undefined,
            vote: undefined,
            pageIndex: 0,
            pageSize: 20
        };

        $scope.paging = {
            pageIndex: 0,
            current: 1
        };

        $scope.onPageChanged = function () {
            if (!$scope.paging) return;
            let pageIndex = $scope.paging.current - 1;
            $scope.load(pageIndex);
        };

        $scope.load = function (pageIndex) {
            let authToken = $cookieStore.get('authToken');
            $scope.query.pageIndex = pageIndex || 0;

            RestService.reports.byUserVotes(authToken, $scope.query)
                .then((response) => {
                    $scope.items = response.data.data;
                    $scope.loaded = true;
                    $scope.err = undefined;
                    $scope.paging = {
                        pageIndex: response.data.page,
                        current: response.data.page + 1,
                        pageCount: response.data.pageCount,
                        pageSize: response.data.pageSize,
                        totalSize: response.data.totalSize
                    }
                })
                .catch(function (err) {
                    $scope.items = undefined;
                    $scope.loaded = false;
                    $scope.err = err;
                });
        };

        $scope.showTriplesByVote = function (item) {
            // console.log(item);
            $state.go('reports.triples', {username: item.user.username, vote: item.vote});
        };
    })

    .controller('ReportTriplesController', function ($scope, RestService, $state, $cookieStore, $mdPanel, $stateParams) {
        console.log($stateParams);
        $scope.query = {
            username: $stateParams.username || undefined,
            subject: $stateParams.subject || undefined,
            hasVote: undefined,
            vote: $stateParams.vote,
            pageIndex: 0,
            pageSize: 20
        };
        console.log($scope.query);
        $scope.paging = {
            pageIndex: 0,
            current: 1
        };

        $scope.onPageChanged = function () {
            if (!$scope.paging) return;
            let pageIndex = $scope.paging.current - 1;
            $scope.load(pageIndex);
        };

        $scope.load = function (pageIndex) {
            let authToken = $cookieStore.get('authToken');
            $scope.query.pageIndex = pageIndex || 0;

            RestService.reports.byTriples(authToken, $scope.query)
                .then((response) => {
                    $scope.items = response.data.data;
                    $scope.loaded = true;
                    $scope.err = undefined;
                    $scope.paging = {
                        pageIndex: response.data.page,
                        current: response.data.page + 1,
                        pageCount: response.data.pageCount,
                        pageSize: response.data.pageSize,
                        totalSize: response.data.totalSize
                    }
                })
                .catch(function (err) {
                    $scope.items = undefined;
                    $scope.loaded = false;
                    $scope.err = err;
                });
        };
    });
