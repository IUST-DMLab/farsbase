app
    .controller('UserController', function ($scope, RestService, $cookieStore, $mdDialog, $location) {

        $scope.load = function () {
            let authToken = $cookieStore.get('authToken');

            RestService.profile.load(authToken)
                .then(function (response) {
                    $scope.data = response.data;
                })
                .catch(function (err) {
                    console.log('error : ', err);
                });
        };

        $scope.save = function () {
            let authToken = $cookieStore.get('authToken');

            RestService.profile.updateName(authToken, $scope.data.name)
                .then(function (response) {
                    $scope.data = response.data;
                })
                .catch(function (err) {
                    console.log('error : ', err);
                });
        };

        $scope.updatePassword = function () {
            let authToken = $cookieStore.get('authToken');

            RestService.profile.updatePassword(authToken, $scope.data.currentPassword, $scope.data.newPassword)
                .then(function (response) {
                    if (typeof response.data === 'string')
                        $scope.error = response.data;
                    else
                        $scope.error = '';

                    if(response.status === 200)
                        $scope.error = 'تغییر کلمه عبور با موفقیت انجام شد';
                })
                .catch(function (err) {
                    console.log('error : ', err);
                });
        };

        //
        $scope.load();
    });
