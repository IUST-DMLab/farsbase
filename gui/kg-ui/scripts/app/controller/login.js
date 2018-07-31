app
    .controller('LoginController', function ($scope, $location, RestService, $cookieStore, $mdSidenav, $filter, $mdDialog) {

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
                    $scope.username = username;

                    $location.path( "/users" );
                }
            }

            function ERROR(error, status) {
                $scope.authToken = '';
                $scope.authenticated = false;
                $scope.error = {error: error, status: status};
            }
        };


    });
