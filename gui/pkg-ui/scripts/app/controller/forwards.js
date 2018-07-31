app
    .controller('ForwardsController', function ($scope, RestService, $cookieStore, $mdDialog, $location) {
        $scope.selected = [];
        $scope.limitOptions = [5, 10, 15];

        $scope.options = {
            rowSelection: true,
            multiSelect: true,
            autoSelect: true,
            decapitate: false,
            largeEditDialog: false,
            boundaryLinks: false,
            limitSelect: true,
            pageSelect: true
        };

        $scope.query = {
            name: '',
            username: '',
            order: 'name',
            limit: 5,
            page: 1
        };

        $scope.load = function () {
            let authToken = $cookieStore.get('authToken');
            $scope.username = $cookieStore.get('username');

            RestService.forwards.load(authToken)
                .then(function (response) {
                    $scope.data = {
                        forwards: response.data.sort(compare)
                    };
                })
                .catch(function (err) {
                    console.log('error : ', err);
                });
        };

        function compare(a, b) {
            if (a.source < b.source)
                return -1;
            if (a.source > b.source)
                return 1;
            return 0;
        }

        $scope.edit = function (forward) {
            let authToken = $cookieStore.get('authToken');
            RestService.permissions.load(authToken)
                .then(function (response) {
                    let permissions = response.data;

                    $mdDialog.show({
                        controller: DialogController,
                        templateUrl: 'templates/forwards/edit.html',
                        parent: angular.element(document.body),
                        locals: {
                            forward: forward,
                            permissions: permissions
                        },
                        //targetEvent: ev,
                        clickOutsideToClose: true,
                    })
                        .then(function (answer) {
                            $scope.load();
                        }, function () {

                        });
                });
        };

        function DialogController($scope, $mdDialog, forward, permissions) {
            $scope.newForward = forward ? false : true;
            $scope.forward = forward ? angular.copy(forward) : {permissions: []};
            permissions.map(p => p.selected = $scope.forward.permissions.map(fp => fp.identifier).indexOf(p.identifier) !== -1);
            $scope.permissions = permissions;

            $scope.addUrn = function () {
                console.log('addUrn');
                let urns = $scope.forward.urns ? $scope.forward.urns : [];
                urns.push({
                    "urn": "",
                    "type": "",
                    "method": "",
                    "permissions": []
                });
                $scope.forward.urns = urns;
            };

            $scope.removeUrn = function (u) {
                $scope.forward.urns.erase(u);
            };

            $scope.save = function () {
                let authToken = $cookieStore.get('authToken');
                let selectedPermissions = $scope.permissions.filter(p => p.selected).map(p => p.title);
                RestService.forwards.save(authToken, $scope.forward.source, $scope.forward.destination, selectedPermissions, $scope.forward.urns, $scope.forward.identifier)
                    .then(function (response) {
                        $mdDialog.hide('save');
                    })
                    .catch(function (err) {
                        alert(err);
                        console.log('error : ', err);
                    });
            };

            $scope.cancel = function () {
                $mdDialog.hide('cancel');
            };
        }

        //
        $scope.load();
    });
