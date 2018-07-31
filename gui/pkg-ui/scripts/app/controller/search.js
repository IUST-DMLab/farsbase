app
    .controller('SearchController', function ($scope, RestService, $state) {
    })

    .controller('SearchFeedbackController', function ($scope, RestService, $state, $stateParams, $rootScope, $mdPanel) {

        $scope.query = {
            page: 0,
            pageSize: 20,
            textKeyword: undefined,
            queryKeyword: undefined,
            minSendDate: undefined,
            maxSendDate: undefined,
            approved: '',
            done: ''
        };

        $scope.paging = {
            pageIndex: 0,
            current: 1
        };

        $scope.onPageChanged = function () {
            if (!$scope.paging) return;
            $scope.query.page = $scope.paging.current - 1;
            $scope.load();
        };

        $scope.load = function () {
            //console.log(new Date($scope.query.minSendDate));

            RestService.search.searchFeedback($scope.query)
                .then((response) => {
                    $scope.items = response.data.content;
                    $scope.loaded = true;
                    $scope.err = undefined;
                    $scope.paging = {
                        pageIndex: response.data.number,
                        current: response.data.number + 1,
                        pageCount: response.data.totalPages,
                        pageSize: response.data.size,
                        totalSize: response.data.totalElements
                    }
                })
                .catch(function (err) {
                    $scope.items = undefined;
                    $scope.loaded = false;
                    $scope.err = err;
                });
        };

        $scope.showItem = function (item, ev) {
            // console.log('showItem', item);

            let position = $mdPanel.newPanelPosition()
                .absolute()
                .center();

            $mdPanel.open({
                attachTo: angular.element(document.body),
                controller: DialogController,
                disableParentScroll: false,
                templateUrl: './templates/search/feedback-item.html',
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
                    _dialogPanels['feedback-panel'] = p;
                });
        };

        // $scope.load();

        function DialogController($scope, $mdPanel, item) {

            $scope.item = angular.copy(item);
            //console.log($scope.item);

            $scope.save = (row, index) => {

                let obj = {
                    "approved": $scope.item.approved,
                    "done": $scope.item.done,
                    "note": $scope.item.note,
                    "uid": $scope.item.id
                };
                //console.log(obj);

                RestService.search.edit(obj)
                    .then(function () {
                        item.approved = obj.approved;
                        item.done = obj.done;
                        item.note = obj.note;
                        closeDialogPanel('feedback-panel');
                    });
            };

            $scope.cancel = function (row, index) {
                closeDialogPanel('feedback-panel');
            };

            $scope.close = function () {
                closeDialogPanel('feedback-panel');
            };

        }
    });
