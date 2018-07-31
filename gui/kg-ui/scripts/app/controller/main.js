app
    .controller('MainController', function ($scope, RestService, $cookieStore, $mdDialog) {

        var at = undefined;
        $scope.roles = $cookieStore.get('roles');
        if (at) {
            $scope.authenticated = true;
            $scope.authToken = at;
        }
        else {
            $scope.authenticated = false;
        }


    });
