app
    .controller('MappingsController', function ($scope, RestService, $state) {
        $scope.getSelectedTabIndex = function () {
            return $state.current.data.index;
        };


        $scope.getFKGOpropertyUrl = function (p) {
            return "http://fkg.iust.ac.ir/ontology/{0}".format(p.replace('fkgo:', ''));
        };

    })

    .controller('MappingsTemplateController', function ($scope, RestService, $state, $stateParams, $rootScope, $mdPanel, $mdDialog) {

        $scope.query = {
            page: 0,
            pageSize: 20,
            templateName: undefined,
            templateNameLike: undefined,
            className: undefined,
            classNameLike: undefined,
            //propertyName: 'مبدا', //undefined,
            propertyName: undefined,
            propertyNameLike: undefined,
            predicateName: undefined,
            predicateNameLike: undefined,
            approved: ''
        };

        $scope.paging = {
            pageIndex: 0,
            current: 1
        };

        $scope.onPageChanged = function () {
            if (!$scope.paging) return;
            $scope.query.page = $scope.paging.current - 1;
            $scope.load();
        };

        $scope.showTemplate = function (ev, templates, templateIndex) {

            let position = $mdPanel.newPanelPosition()
                .absolute()
                .center();

            $mdPanel.open({
                attachTo: angular.element(document.body),
                controller: SelectedTemplateDialogController,
                disableParentScroll: false,
                templateUrl: './templates/mappings/template-item.html',
                hasBackdrop: true,
                panelClass: 'dialog-panel-big',
                position: position,
                trapFocus: true,
                zIndex: 50,
                targetEvent: ev,
                clickOutsideToClose: true,
                escapeToClose: true,
                focusOnOpen: true,
                locals: {
                    templates: templates,
                    templateIndex: templateIndex,
                    onRefresh: $scope.load
                }
            })
                .then(function (p) {
                    _dialogPanels['main-panel'] = p;
                });

        };

        $scope.load = function () {
            RestService.mappings.searchTemplate($scope.query)
                .then((response) => {
                    $scope.templates = response.data.data;
                    $scope.loaded = true;
                    $scope.err = undefined;
                    $scope.paging = {
                        pageIndex: response.data.page,
                        current: response.data.page + 1,
                        pageCount: response.data.pageCount,
                        pageSize: response.data.pageSize,
                        totalSize: response.data.totalSize
                    }
                })
                .catch(function (err) {
                    $scope.templates = undefined;
                    $scope.loaded = false;
                    $scope.err = err;
                });
        };

        // $scope.load();

        function SelectedTemplateDialogController($scope, $mdPanel, templates, templateIndex, onRefresh) {

            function generateRule(items) {
                return items.map((r) => {
                    return {
                        constant: r.constant,
                        predicate: r.predicate,
                        transform: r.transform ? (r.transform.transform ? r.transform.transform : r.transform) : undefined,
                        type: r.type,
                        unit: r.unit
                    }
                });
            }

            function prepareTemplate(t) {
                for (let p of t.properties) {
                    p.list = [];
                    for (let r of p.rules) {
                        p.list.push(_.assign({valid: true}, r));
                    }
                    for (let r of p.recommendations) {
                        p.list.push(_.assign({valid: false}, r));
                    }
                }
                return t;
            }

            $scope.selectedTemplate = prepareTemplate(templates[templateIndex]);

            $scope.saveTemplate = (templateToBeSaved, refresh) => {
                RestService.mappings.saveTemplate(templateToBeSaved)
                    .then(function () {
                        if ($scope.selectedTemplate.identifier === templateToBeSaved.identifier)
                            $scope.selectedTemplate = prepareTemplate(templateToBeSaved);

                        if (refresh && onRefresh) onRefresh();
                    })
                    .catch(function () {
                        alert('خطایی رخ داده است!');
                    });
            };

            $scope.cancel = function (row, index) {
                $scope.selectedItemPropertyRulesAndRecommendations[index] = $scope.backup;
                $scope.backup = undefined;
            };

            $scope.close = function () {
                closeDialogPanel('main-panel');
            };

            $scope.filterProperty = function (ev, property) {

                $mdPanel.open({
                    attachTo: angular.element(document.body),
                    controller: FilterDialogController,
                    disableParentScroll: false,
                    templateUrl: './templates/mappings/property-filter.html',
                    hasBackdrop: true,
                    panelClass: 'dialog-panel-small',
                    position: $mdPanel.newPanelPosition().absolute().center(),
                    trapFocus: true,
                    zIndex: 51,
                    targetEvent: ev,
                    clickOutsideToClose: true,
                    escapeToClose: true,
                    focusOnOpen: true,
                    locals: {
                        property: property,
                        onSave: function (data) {

                            let selectedTemplates = data.selectedTemplates;
                            let predicate = data.predicate;
                            let unit = data.unit;
                            let transform = data.transform;

                            for (let i = 0; i < selectedTemplates.length; i++) {
                                //console.log(selectedTemplates[i]);
                                let selectedTemplate = angular.copy(selectedTemplates[i]);
                                let selectedProperty = selectedTemplate.properties.filter((p) => {
                                    return p.property === property.property;
                                })[0];

                                let rule = {
                                    predicate: predicate,
                                    unit: unit,
                                    transform: transform ? transform.transform : undefined
                                };
                                if (selectedProperty.rules.length)
                                    selectedProperty.rules[0] = rule;
                                else
                                    selectedProperty.rules = [rule];

                                //console.log(selectedTemplate);
                                $scope.saveTemplate(selectedTemplate, i === selectedTemplates.length - 1);
                            }

                        }
                    }
                })
                    .then(function (p) {
                        _dialogPanels['filter-panel'] = p;
                    });

            };

            function FilterDialogController($scope, property, onSave) {

                let query = {
                    propertyName: property.property,
                    propertyNameLike: false
                };

                $scope.filteredTemplates = [];
                $scope.selected = [];

                $scope.load = function () {
                    RestService.mappings.searchTemplate(query)
                        .then((response) => {
                            $scope.property = property;
                            //$scope.predicates = property.list.projection('predicate');

                            let filteredTemplates = angular.copy(response.data.data);
                            for (let i = 0; i < filteredTemplates.length; i++) {
                                filteredTemplates[i] = prepareTemplate(filteredTemplates[i]);
                            }

                            $scope.filteredTemplates = filteredTemplates;
                            $scope.loaded = true;
                            $scope.err = undefined;
                        })
                        .catch(function (err) {
                            $scope.filteredTemplates = undefined;
                            $scope.loaded = false;
                            $scope.err = err;
                        });
                };

                $scope.suggestPredicates = function (query) {
                    return RestService.mappings.suggestPredicates(query);
                };
                $scope.suggestUnits = function (query) {
                    return RestService.mappings.suggestUnits(query);
                };
                $scope.suggestTransforms = function (query) {
                    return RestService.mappings.suggestTransforms(query)
                        .then(function (data) {
                            if (query)
                                return data.filter(x => (x.transform.indexOf(query) > -1) || (x.label.indexOf(query) > -1));
                            else return data;
                        });
                };

                $scope.toggle = function (item, list) {
                    var idx = list.indexOf(item);
                    if (idx > -1) {
                        list.splice(idx, 1);
                    }
                    else {
                        list.push(item);
                    }
                };

                $scope.exists = function (item, list) {
                    return list.indexOf(item) > -1;
                };

                $scope.isIndeterminate = function () {
                    return ($scope.selected.length !== 0 &&
                    $scope.selected.length !== $scope.filteredTemplates.length);
                };

                $scope.isChecked = function () {
                    return $scope.selected.length === $scope.filteredTemplates.length;
                };

                $scope.toggleAll = function () {
                    if ($scope.selected.length === $scope.filteredTemplates.length) {
                        $scope.selected = [];
                    } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
                        $scope.selected = $scope.filteredTemplates.slice(0);
                    }
                };

                $scope.save = function () {
                    //console.log($scope.selected, $scope.selectedPredicate, $scope.selectedUnit, $scope.selectedTransform);

                    if (!$scope.selected.length) {
                        alert('هیچ سطری انتخاب نشده است!!!');
                        return;
                    }

                    if (!$scope.selectedPredicate && !$scope.selectedUnit && !$scope.selectedTransform) {
                        var c = confirm('هیچ مقداری وارد نشده است!!! آیا واقعا می‌خواهید نگاشت را انجام دهید؟');
                        if (!c)
                            return;
                    }

                    let data = {
                        selectedTemplates: $scope.selected,
                        predicate: $scope.selectedPredicate,
                        unit: $scope.selectedUnit,
                        transform: $scope.selectedTransform
                    };
                    closeDialogPanel('filter-panel', onSave, data);
                };

                $scope.close = function () {
                    closeDialogPanel('filter-panel');
                };


                $scope.load();
            }


            $scope.removeRule = function (ev, property, rule) {

                let confirm = $mdDialog.confirm({
                    // onComplete: function afterShowAnimation() {
                    //     var $dialog = angular.element(document.querySelector('md-dialog'));
                    //     var $actionsSection = $dialog.find('md-dialog-actions');
                    //     var $cancelButton = $actionsSection.children()[0];
                    //     var $confirmButton = $actionsSection.children()[1];
                    //     angular.element($confirmButton).addClass('md-raised md-warn');
                    //     angular.element($cancelButton).addClass('md-raised');
                    // }
                })
                    .title('آیا واقعا می‌خواهید خصیصه انتخاب شده را حذف کنید؟')
                    .textContent('این عمل قابل بازگشت نمی‌باشد!')
                    .ariaLabel('Lucky day')
                    .targetEvent(ev)
                    .ok('حذف کن')
                    .cancel('انصراف');

                $mdDialog.show(confirm)
                    .then(function () {

                        let copy = angular.copy($scope.selectedTemplate);
                        let rIndex = property.list.indexOf(rule);
                        let pIndex = $scope.selectedTemplate.properties.indexOf(property);
                        copy.properties[pIndex].list.splice(rIndex, 1);

                        copy.properties[pIndex].rules = generateRule(copy.properties[pIndex].list.filter(r => r.valid));
                        copy.properties[pIndex].recommendations = generateRule(copy.properties[pIndex].list.filter(r => !r.valid));

                        // $scope.selectedTemplate = copy;  // on debug only
                        $scope.saveTemplate(copy);

                    }, function () {

                    });
            };

            $scope.addNewRule = function (ev, property) {
                console.log(property);

                $scope.editRule(ev, property);
                // constant, predicate, transform, type, unit
            };

            $scope.approveRule = function (ev, property, rule) {

                let copy = angular.copy($scope.selectedTemplate);
                let rIndex = property.list.indexOf(rule);
                let pIndex = $scope.selectedTemplate.properties.indexOf(property);
                copy.properties[pIndex].list[rIndex].valid = true;

                copy.properties[pIndex].rules = generateRule(copy.properties[pIndex].list.filter(r => r.valid));
                copy.properties[pIndex].recommendations = generateRule(copy.properties[pIndex].list.filter(r => !r.valid));

                // $scope.selectedTemplate = copy;  // on debug only
                $scope.saveTemplate(copy);

            };


            $scope.editRule = function (ev, property, rule) {

                $mdPanel.open({
                    attachTo: angular.element(document.body),
                    controller: EditRuleDialogController,
                    disableParentScroll: false,
                    templateUrl: './templates/mappings/template-item-rule-edit.html',
                    hasBackdrop: true,
                    panelClass: 'dialog-panel-thin',
                    position: $mdPanel.newPanelPosition().absolute().center(),
                    trapFocus: true,
                    zIndex: 52,
                    targetEvent: ev,
                    clickOutsideToClose: true,
                    escapeToClose: true,
                    focusOnOpen: true,
                    locals: {
                        title: !rule ? 'نگاشت جدید' : 'ویرایش نگاشت',
                        model: {
                            property: property,
                            rule: angular.copy(rule)
                        },
                        onSave: function (data) {

                            let copy = angular.copy($scope.selectedTemplate);
                            let rIndex = property.list.indexOf(rule);
                            let pIndex = $scope.selectedTemplate.properties.indexOf(property);

                            if (rule)   // edit
                                copy.properties[pIndex].list[rIndex] = angular.copy(data);
                            else        // add
                                copy.properties[pIndex].list.push(data);

                            copy.properties[pIndex].rules = generateRule(copy.properties[pIndex].list.filter(r => r.valid));
                            copy.properties[pIndex].recommendations = generateRule(copy.properties[pIndex].list.filter(r => !r.valid));

                            // $scope.selectedTemplate = copy;  // on debug only
                            $scope.saveTemplate(copy);
                        }
                    }
                })
                    .then(function (p) {
                        _dialogPanels['edit-property-panel'] = p;
                    });
            };

            function EditRuleDialogController($scope, title, model, onSave) {

                $scope.action = {
                    title: title
                };

                $scope.model = model;

                // $scope.suggestPredicates = function (query) {
                //     console.log('edit-constant suggestPredicates');
                //     return RestService.mappings.suggestPredicates(query);
                // };

                $scope.suggestPredicates = function (query) {
                    //console.log('dialog-controller suggestPredicates');
                    // return RestService.mappings.suggestPredicates(query)
                    //     .then((res) => {
                    //         return res.data;
                    //     });
                    return RestService.mappings.suggestPredicates(query);
                };
                $scope.suggestUnits = function (query) {
                    return RestService.mappings.suggestUnits(query);
                };
                $scope.suggestTransforms = function (query) {
                    return RestService.mappings.suggestTransforms(query)
                        .then(function (data) {
                            if (query)
                                return data.filter(x => (x.transform.indexOf(query) > -1) || (x.label.indexOf(query) > -1));
                            else return data;
                        });
                };

                $scope.save = function () {
                    closeDialogPanel('edit-property-panel', onSave, model.rule);
                };

                $scope.close = function () {
                    closeDialogPanel('edit-property-panel');
                };
            }


            $scope.removeConstant = function (ev, rule) {

                let confirm = $mdDialog.confirm({
                    // onComplete: function afterShowAnimation() {
                    //     var $dialog = angular.element(document.querySelector('md-dialog'));
                    //     var $actionsSection = $dialog.find('md-dialog-actions');
                    //     var $cancelButton = $actionsSection.children()[0];
                    //     var $confirmButton = $actionsSection.children()[1];
                    //     angular.element($confirmButton).addClass('md-raised md-warn');
                    //     angular.element($cancelButton).addClass('md-raised');
                    // }
                })
                    .title('آیا واقعا می‌خواهید ثابت انتخاب شده را حذف کنید؟')
                    .textContent('این عمل قابل بازگشت نمی‌باشد!')
                    .ariaLabel('Lucky day')
                    .targetEvent(ev)
                    .ok('حذف کن')
                    .cancel('انصراف');

                $mdDialog.show(confirm).then(function () {

                    let index = $scope.selectedTemplate.rules.indexOf(rule);
                    let copy = angular.copy($scope.selectedTemplate);
                    copy.rules.splice(index, 1);
                    $scope.saveTemplate(copy);

                }, function () {
                });

            };

            $scope.editConstant = function (ev, rule) {

                let index = $scope.selectedTemplate.rules.indexOf(rule);

                $mdPanel.open({
                    attachTo: angular.element(document.body),
                    controller: EditConstantDialogController,
                    disableParentScroll: false,
                    templateUrl: './templates/mappings/template-constant-edit.html',
                    hasBackdrop: true,
                    panelClass: 'dialog-panel-thin',
                    position: $mdPanel.newPanelPosition().absolute().center(),
                    trapFocus: true,
                    zIndex: 52,
                    targetEvent: ev,
                    clickOutsideToClose: true,
                    escapeToClose: true,
                    focusOnOpen: true,
                    locals: {
                        title: !rule ? 'افزودن ثابت جدید' : 'ویرایش ثابت',
                        model: !rule ? {} : angular.copy($scope.selectedTemplate.rules[index]), // todo : angular.copy(rule)
                        onSave: function (data) {

                            let copy = angular.copy($scope.selectedTemplate);
                            if (rule)   // edit
                                copy.rules[index] = angular.copy(data.model);
                            else        // add
                                copy.rules.push(data.model);

                            $scope.saveTemplate(copy);
                        }
                    }
                })
                    .then(function (p) {
                        _dialogPanels['edit-constant-panel'] = p;
                    });
            };

            function EditConstantDialogController($scope, title, model, onSave) {
                $scope.action = {
                    title: title
                };

                $scope.model = model;

                $scope.suggestPredicates = function (query) {
                    //console.log('edit-constant suggestPredicates');
                    return RestService.mappings.suggestPredicates(query);
                };
                $scope.suggestClasses = function (query) {
                    //console.log('edit-constant suggestClasses');
                    return RestService.ontology.queryClasses(query)
                        .then(function (response) {
                            return response.data.data;
                        });
                };

                $scope.save = function () {
                    model.predicate = model.predicate || $scope.searchPredicate;

                    closeDialogPanel('edit-constant-panel', onSave, {model: model, action: 'add'});
                };

                $scope.close = function () {
                    closeDialogPanel('edit-constant-panel');
                };
            }


            $scope.getFKGOpropertyUrl = function (p) {
                return "http://fkg.iust.ac.ir/ontology/{0}".format(p.replace('fkgo:', ''));
            };
        }
    })

    .controller('MappingsPropertyController', function ($scope, RestService) {

        $scope.query = {
            page: 0,
            pageSize: 20,
            templateName: undefined,
            templateNameLike: undefined,
            className: undefined,
            classNameLike: undefined,
            propertyName: undefined,
            propertyNameLike: undefined,
            predicateName: undefined,
            predicateNameLike: undefined,
            allNull: '',
            oneNull: '',
            approved: ''
        };

        $scope.paging = {
            pageIndex: 0,
            current: 1
        };

        $scope.onPageChanged = function () {
            if (!$scope.paging) return;
            $scope.query.page = $scope.paging.current - 1;
            $scope.load();
        };

        $scope.load = function () {
            RestService.mappings.searchProperty($scope.query)
                .then((response) => {
                    $scope.items = response.data.data;
                    $scope.loaded = true;
                    $scope.err = undefined;
                    $scope.paging = {
                        pageIndex: response.data.page,
                        current: response.data.page + 1,
                        pageCount: response.data.pageCount,
                        pageSize: response.data.pageSize,
                        totalSize: response.data.rowCount
                    }
                })
                .catch(function (err) {
                    $scope.items = undefined;
                    $scope.loaded = false;
                    $scope.err = err;
                });
        };

        $scope.diffArrays = function (a, b) {
            return _.differenceWith(a, b, _.isEqual);
        };

        // $scope.getFKGOpropertyUrl = function(p){
        //     return "http://fkg.iust.ac.ir/ontology/{0}".format(p.replace('fkgo:', ''));
        // };

        // $scope.load();
    });

