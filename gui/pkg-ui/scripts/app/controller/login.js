app
    .controller('LoginController', function ($scope, $location, RestService, $cookieStore, $state, $filter, $mdDialog) {

        //var at = $cookieStore.get('authToken');
        let at = undefined;
        $scope.roles = $cookieStore.get('roles');
        if (at) {
            $scope.authenticated = true;
            $scope.authToken = at;
        }
        else {
            $scope.authenticated = false;
        }

        $scope.login = function (username, password) {
            $scope.authify();

            RestService.login(username, password).then(SUCCESS, ERROR);

            function SUCCESS(response) {
                let token = response.headers('x-auth-token');
                if (token) {
                    $scope.authify(token, response.data, username);

                    $cookieStore.put('authToken', $scope.auth.token);
                    $cookieStore.put('roles', $scope.auth.roles);
                    $cookieStore.put('username', $scope.auth.username);

                    localStorage.setItem('authToken', $scope.auth.token);
                    localStorage.setItem('roles', $scope.auth.roles);
                    localStorage.setItem('username', $scope.auth.username);

                    $state.go("home.dashboard");
                }
                else {
                    $scope.error = {error: 'شما اجازه دسترسی به کنترل پنل را ندارید', status: ''};
                    // console.log($scope.error);
                }
            }

            function ERROR(error, status) {
                $scope.authify();
                $scope.error = {error: error, status: status};
                console.log(error);
            }
        };

        $scope.authify = function (token, roles, username) {
            $scope.auth = {
                token: token,
                roles: roles,
                username: username
            };
            $scope.authenticated = token ? true : false;
            $scope.error = token ? undefined : 'error';
        };

    });
