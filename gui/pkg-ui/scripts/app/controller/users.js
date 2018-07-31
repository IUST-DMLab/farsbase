app
    .controller('UsersController', function ($scope, RestService, $cookieStore, $mdDialog, $location) {
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

            RestService.users.load(authToken, $scope.query.name, $scope.query.username)
                .then(function (response) {
                    $scope.data = {
                        users: response.data.data.sort(compare),
                        pageIndex: response.data.page,
                        pageSize: response.data.pageSize,
                        pageCount: response.data.pageCount,
                        totalSize: response.data.totalSize,
                    };
                })
                .catch(function (err) {
                    console.log('error : ', err);
                });
        };

        function compare(a, b) {
            if (a.name < b.name)
                return -1;
            if (a.name > b.name)
                return 1;
            return 0;
        }

        $scope.edit = function (user) {
            let authToken = $cookieStore.get('authToken');
            RestService.permissions.load(authToken)
                .then(function (response) {
                    let permissions = response.data;

                    $mdDialog.show({
                        controller: DialogController,
                        templateUrl: 'templates/users/edit.html',
                        parent: angular.element(document.body),
                        locals: {
                            user: user,
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

        function DialogController($scope, $mdDialog, user, permissions) {
            $scope.newUser = user ? false : true;
            $scope.user = user ? angular.copy(user) : {permissions: []};
            permissions.map(p => p.selected = $scope.user.permissions.map(up => up.identifier).indexOf(p.identifier) !== -1);
            $scope.permissions = permissions;
            console.log(permissions);
            $scope.save = function () {
                let authToken = $cookieStore.get('authToken');
                let selectedPermissions = $scope.permissions.filter(p => p.selected).map(p=>p.title);
                // $scope.user.permissions = permissions;
                console.log(selectedPermissions);
                RestService.users.save(authToken, $scope.user.name, $scope.user.username, selectedPermissions, $scope.user.password, $scope.user.identifier)
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
