app
    .controller('PermissionsController', function ($scope, RestService, $cookieStore, $mdDialog, $location) {
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

            RestService.permissions.load(authToken)
                .then(function (response) {
                    $scope.data = {
                        permissions: response.data.sort(compare)
                    };
                })
                .catch(function (err) {
                    console.log('error : ', err);
                });
        };

        function compare(a, b) {
            if (a.title < b.title)
                return -1;
            if (a.title > b.title)
                return 1;
            return 0;
        }

        $scope.edit = function (permission) {
            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'templates/permissions/edit.html',
                parent: angular.element(document.body),
                locals: {
                    permission: permission
                },
                //targetEvent: ev,
                clickOutsideToClose: true,
            }).then(function (answer) {
                $scope.load();
            }, function () {

            });
        };

        function DialogController($scope, $mdDialog, permission) {
            $scope.newPermission = permission ? false : true;
            $scope.permission = permission ? angular.copy(permission) : {};

            $scope.save = function () {
                let authToken = $cookieStore.get('authToken');
                RestService.permissions.save(authToken, $scope.permission.title, $scope.permission.description, $scope.permission.identifier)
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
