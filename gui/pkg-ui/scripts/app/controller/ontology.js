app
    .controller('OntologyController', function ($scope, RestService, $state, $cookieStore, $mdDialog, $location) {

    })

    .controller('OntologyTreeController', function ($scope, RestService, $state, ivhTreeviewMgr) {
        $scope.lang = 'fa';
        // $scope.lang = 'en';
        $scope.view = 'SIMPLE';
        // $scope.view = 'GRAPHICAL';

        $scope.expandAll = function () {
            ivhTreeviewMgr.expandRecursive($scope.items, $scope.items);
        };

        $scope.collapseAll = function () {
            ivhTreeviewMgr.collapseRecursive($scope.items, $scope.items);
        };

        // $scope.expandTo = function (level) {
        //     ivhTreeviewMgr.collapseRecursive($scope.items, $scope.items);
        // };

        $scope.switchView = function () {
            $scope.view = ($scope.view === 'SIMPLE') ? 'GRAPHICAL' : 'SIMPLE';

            $scope.load();
        };

        $scope.switchLanguage = function (lang) {
            $scope.lang = lang;
            $scope.items = undefined;
            $scope.load();
        };

        $scope.load = function () {

            // RestService.ontology.classTree($scope.lang, undefined, 2)
            RestService.ontology.classTree($scope.lang)
                .then(function (response) {
                    let items = response.data;
                    $scope.items = items;

                    if ($scope.view === 'GRAPHICAL')
                        setTimeout(function () {
                            renderTree(angular.copy($scope.items), ($scope.lang === 'fa'), $state);
                        }, 100);
                });
        };

        $scope.load();
    })

    .controller('OntologyClassController', function ($scope, RestService, $state, $stateParams, $rootScope, $mdDialog) {

        let classUrl = decodeURIComponent($stateParams.classUrl);

        $scope.load = function () {
            $rootScope.title = classUrl ? 'ویرایش کلاس' : 'ایجاد کلاس جدید';
            if (classUrl) {

                RestService.ontology.getClass(classUrl)
                    .then(function (response) {
                        let clazz = response.data;
                        $scope.clazz = response.data;
                    });
            }
            else {
                //console.log('add new class');
                $scope.clazz = {
                    "disjointWith": [],
                    "equivalentClasses": [],
                    "properties": [],
                };
                $scope.addNew = true;
            }
        };

        $scope.prevClass = function () {
            $state.go('ontology.class', {classUrl: $scope.clazz.previous});
        };

        $scope.nextClass = function () {
            $state.go('ontology.class', {classUrl: $scope.clazz.next});
        };

        $scope.saveClass = function (ev) {
            //console.log($scope.clazz);
            RestService.ontology.saveClass($scope.clazz)
                .then(function (status) {
                    if (status) {
                        $state.go('ontology.class', {classUrl: $scope.clazz.url});
                    }
                    else {
                        $mdDialog.show(
                            $mdDialog.alert()
                                .parent(angular.element(document.querySelector('body')))
                                .clickOutsideToClose(true)
                                .title('خطا')
                                .textContent('خطایی رخ داده است!')
                                .ariaLabel('ERROR')
                                .ok('خب')
                                .targetEvent(ev)
                        );
                    }
                });
        };

        $scope.cancel = function () {
            if ($scope.addNew)
                $state.go('ontology.tree');
            else
                $state.go('ontology.class', {classUrl: $scope.clazz.url});
        };

        $scope.editProperty = function (property, ev) {
            console.log('editProperty');
            $mdDialog.show({
                controller: EditPropertyDialogController,
                templateUrl: './templates/ontology/property-edit.html',
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose: true,
                locals: {
                    property: property,
                    mode: 'edit-dialog'
                }
            }).then(function (data) {
            }, function () {
            });

        };

        $scope.newProperty = function (ev) {
            console.log('newProperty');
            $mdDialog.show({
                controller: NewPropertyDialogController,
                templateUrl: './templates/ontology/property-selector.html',
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose: true,
                locals: {
                    clazz: $scope.clazz,
                    mode: 'new-dialog'
                }
            }).then(function (data) {
                //console.log(data.property);
                $scope.clazz.properties.push(data.property);
                //console.log(data.property);
            }, function () {
                $scope.status = 'You cancelled the dialog.';
            });

        };

        $scope.queryClasses = function (query) {
            //console.log('queryClasses : ', query);
            return RestService.ontology.queryClasses(query)
                .then(function (response) {
                    return response.data.data;
                });
        };

        $scope.detachProperty = function (property, ev) {

            var className = $scope.clazz.name;
            var propertyName = property.name;

            var confirm = $mdDialog.confirm({
                // onComplete: function afterShowAnimation() {
                //     var $dialog = angular.element(document.querySelector('md-dialog'));
                //     var $actionsSection = $dialog.find('md-dialog-actions');
                //     var $cancelButton = $actionsSection.children()[0];
                //     var $confirmButton = $actionsSection.children()[1];
                //     angular.element($confirmButton).addClass('md-raised md-warn');
                //     angular.element($cancelButton).addClass('md-raised');
                // }
            })
                .title('آیا واقعا می‌خواهید خصیصه {0} را از کلاس {1} جدا کنید؟'.format(propertyName, className))
                .textContent('این عمل قابل بازگشت نمی‌باشد!')
                .ariaLabel('Lucky day')
                .targetEvent(ev)
                .ok('جدا کن')
                .cancel('انصراف');

            $mdDialog.show(confirm).then(function () {
                console.log('detach property {0} from class {1}!'.format(propertyName, className));

                RestService.ontology.removePropertyFromClass($scope.clazz.url, property.url)
                    .then(function (status) {
                        if (status) {
                            // $scope.load();
                            let pos = $scope.clazz.properties.indexOf(property);
                            $scope.clazz.properties.splice(pos, 1);
                        }
                        else {
                            alert('خطایی رخ داده است!');
                        }
                    });

            }, function () {

            });

        };

        $scope.removeClass = function (ev) {

            var className = $scope.clazz.name;

            var confirm = $mdDialog.confirm({
                // onComplete: function afterShowAnimation() {
                //     var $dialog = angular.element(document.querySelector('md-dialog'));
                //     var $actionsSection = $dialog.find('md-dialog-actions');
                //     var $cancelButton = $actionsSection.children()[0];
                //     var $confirmButton = $actionsSection.children()[1];
                //     angular.element($confirmButton).addClass('md-raised md-warn');
                //     angular.element($cancelButton).addClass('md-raised');
                // }
            })
                .title('آیا واقعا می‌خواهید کلاس {0} را حذف کنید؟'.format(className))
                .textContent('این عمل قابل بازگشت نمی‌باشد!')
                .ariaLabel('Lucky day')
                .targetEvent(ev)
                .ok('حذف')
                .cancel('انصراف');

            $mdDialog.show(confirm).then(function () {
                console.log('remove class {0}!'.format(className));
                RestService.ontology.removeClass($scope.clazz.url)
                    .then(function (status) {
                        if (status) {
                            $state.go('ontology.tree');
                        }
                        else {
                            alert('خطایی رخ داده است!');
                        }
                    });
            }, function () {

            });
        };

        $scope.nameChanged = function (clazz) {
            clazz.url = 'http://fkg.iust.ac.ir/ontology/' + (clazz.name || '');
            clazz.wasDerivedFrom = clazz.url;
        };

        $scope.urlChanged = function (clazz) {
            clazz.wasDerivedFrom = clazz.url;
        };


        $scope.load();


        function NewPropertyDialogController($scope, $mdDialog, clazz, mode) {

            $scope.data = {
                property: {
                    domains: [clazz.url],
                    types: ['rdf:Property']
                },
                mode: mode
            };

            $scope.closeDialog = function () {
                $mdDialog.cancel();
            };

            $scope.cancel = function () {
                $mdDialog.cancel();
            };

            $scope.suggestProperties = function (query) {
                return RestService.ontology.suggestProperties(query)
                    .then(function (response) {
                        return response.data;
                    });
            };

            $scope.selectProperty = function (propertyUrl) {
                RestService.ontology.getProperty(propertyUrl)
                    .then(function (data) {
                        $mdDialog.hide({property: data.data});
                    });
            };

            $scope.saveProperty = function (property) {
                console.log('saveProperty 1');
                //$mdDialog.hide({property: property});

                RestService.ontology.saveProperty($scope.data.property)
                    .then(function (status) {
                        if (status)
                            $mdDialog.hide({property: property});
                        else
                            $mdDialog.show(
                                $mdDialog.alert()
                                    .parent(angular.element(document.querySelector('body')))
                                    .clickOutsideToClose(true)
                                    .title('خطا')
                                    .textContent('خطایی رخ داده است!')
                                    .ariaLabel('ERROR')
                                    .ok('خب')
                                    .targetEvent(ev)
                            );
                    });
            };

            $scope.nameChanged = function (p) {
                p.url = 'http://fkg.iust.ac.ir/ontology/' + p.name;
                p.wasDerivedFrom = p.url;
            };

            $scope.urlChanged = function (p) {
                p.wasDerivedFrom = p.url;
            };
        }

        function EditPropertyDialogController($scope, $mdDialog, property, mode) {

            $scope.data = {
                property: property,
                mode: mode
            };

            $scope.closeDialog = function () {
                $mdDialog.cancel();
            };

            $scope.cancel = function () {
                $mdDialog.cancel();
            };

            $scope.saveProperty = function (property, ev) {
                console.log('saveProperty 2');
                RestService.ontology.saveProperty($scope.data.property)
                    .then(function (status) {
                        if (status)
                            $mdDialog.hide({});
                        else
                            $mdDialog.show(
                                $mdDialog.alert()
                                    .parent(angular.element(document.querySelector('body')))
                                    .clickOutsideToClose(true)
                                    .title('خطا')
                                    .textContent('خطایی رخ داده است!')
                                    .ariaLabel('ERROR')
                                    .ok('خب')
                                    .targetEvent(ev)
                            );
                    });
            };


            $scope.nameChanged = function (p) {
                p.url = 'http://fkg.iust.ac.ir/ontology/' + p.name;
            };

            $scope.urlChanged = function (p) {

            };
        }

    })

    .controller('OntologyPropertyController', function ($scope, RestService, $state, $stateParams, $mdDialog) {

        let propertyUrl = decodeURIComponent($stateParams.propertyUrl);
        let mode = ($state.current.url.indexOf('/property-edit/') !== -1) ? 'edit' : 'view';
        // console.log('mode : ', mode);

        $scope.load = function () {
            RestService.ontology.getProperty(propertyUrl)
                .then(function (response) {
                    let _property = response.data;

                    let domains = _property.domains;
                    let domain = domains[0];

                    if (!domain) {
                        $scope.data = {
                            property: _property,
                            mode: mode
                        };
                    }
                    else {
                        RestService.ontology.getClass(domain)
                            .then(function (res) {
                                let _clazz = res.data;

                                let index = _.findIndex(_clazz.properties, {url: _property.url});
                                let previous = (_clazz.properties[index - 1] || {}).url;
                                let next = (_clazz.properties[index + 1] || {}).url;

                                $scope.data = {
                                    property: _property,
                                    mode: mode,
                                    next: next,
                                    previous: previous
                                };
                            });
                    }

                });
        };

        $scope.prevProperty = function () {
            $state.go('ontology.property', {propertyUrl: $scope.data.previous});
        };

        $scope.nextProperty = function () {
            $state.go('ontology.property', {propertyUrl: $scope.data.next});
        };


        $scope.saveProperty = function (property, ev) {
            RestService.ontology.saveProperty($scope.data.property)
                .then(function (status) {
                    if (status)
                        $state.go('ontology.property', {propertyUrl: $scope.data.property.url});
                    else
                        $mdDialog.show(
                            $mdDialog.alert()
                                .parent(angular.element(document.querySelector('body')))
                                .clickOutsideToClose(true)
                                .title('خطا')
                                .textContent('خطایی رخ داده است!')
                                .ariaLabel('ERROR')
                                .ok('خب')
                                .targetEvent(ev)
                        );
                });
        };

        $scope.cancel = function (ev) {
            $state.go('ontology.property', {propertyUrl: $scope.data.property.url});
        };

        $scope.removeProperty = function (ev) {

            var propertyName = $scope.data.property.name;

            var confirm = $mdDialog.confirm({
                // onComplete: function afterShowAnimation() {
                //     var $dialog = angular.element(document.querySelector('md-dialog'));
                //     var $actionsSection = $dialog.find('md-dialog-actions');
                //     var $cancelButton = $actionsSection.children()[0];
                //     var $confirmButton = $actionsSection.children()[1];
                //     angular.element($confirmButton).addClass('md-raised md-warn');
                //     angular.element($cancelButton).addClass('md-raised');
                // }
            })
                .title('آیا واقعا می‌خواهید خصیصه {0} را حذف کنید؟'.format(propertyName))
                .textContent('این عمل قابل بازگشت نمی‌باشد!')
                .ariaLabel('Lucky day')
                .targetEvent(ev)
                .ok('حذف')
                .cancel('انصراف');

            $mdDialog.show(confirm).then(function () {
                console.log('remove property {0} : {1}'.format(propertyName, $scope.data.property.url));
                RestService.ontology.removeProperty($scope.data.property.url)
                    .then(function (status) {
                        if (!status) alert('خطایی رخ داده است!');
                    });
            }, function () {

            });
        };

        $scope.load();
    });
