app
    .controller('HomeController', function ($scope, $state, RestService, $cookieStore, $mdDialog) {

        $scope.getSelectedTabIndex = function () {
            return $state.current.data.index;
        };

    });
