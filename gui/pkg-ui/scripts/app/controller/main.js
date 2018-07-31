app
    .controller('MainController', function ($scope, RestService, $cookieStore, $state, $mdDialog) {

        // console.log('MainController');

        let authToken = $cookieStore.get('authToken');
        if (!authToken) {
            $state.go('login');
            return;
        }

        $scope.load = function () {
            let authToken = $cookieStore.get('authToken');
            $scope.username = $cookieStore.get('username');
            $scope.roles = $cookieStore.get('roles');
        };

        $scope.logout = function () {
            $cookieStore.put('authToken', undefined);
            $cookieStore.put('roles', undefined);
            $cookieStore.put('username', undefined);
            $scope.authenticated = false;

            $state.go("login");
        };

        // **

        $scope.load();

    });
