app
    .controller('RunningController', function ($scope, RestService, $state) {


        RestService.run.getRunning()
            .then(function(data){
                $scope.data = {
                    items : data.data
                }
            })
            .catch(function(){

            });


        // $scope.data = {
        //     items : [
        //         {
        //             "title": "raw",
        //             "commands": [
        //                 {
        //                     "command": "java",
        //                     "arguments": [
        //                         "-jar",
        //                         "mapper.jar",
        //                         "raw",
        //                         "knowledgeStore",
        //                         "D:\\KnowledgeGraph\\updater_link\\raw"
        //                     ],
        //                     "environment": {},
        //                     "continueOnFail": false,
        //                     "workingDirectory": "C:\\Workspace\\knowledge_graph\\knowledge-base-mapping\\runner\\target",
        //                     "result": null
        //                 }
        //             ],
        //             "creationEpoch": 1504917722223,
        //             "remindedTryCount": 4,
        //             "validUntilEpoch": 1505004122223,
        //             "startEpoch": 1504917722235,
        //             "progress": 50,
        //             "endEpoch": null,
        //             "state": "Running",
        //             "definition": {
        //                 "title": "raw",
        //                 "commands": [
        //                     {
        //                         "command": "java",
        //                         "arguments": [
        //                             "-jar",
        //                             "mapper.jar",
        //                             "raw",
        //                             "knowledgeStore",
        //                             "D:\\KnowledgeGraph\\updater_link\\raw"
        //                         ],
        //                         "environment": {},
        //                         "continueOnFail": false,
        //                         "workingDirectory": "C:\\Workspace\\knowledge_graph\\knowledge-base-mapping\\runner\\target",
        //                         "result": null
        //                     }
        //                 ],
        //                 "creationEpoch": 0,
        //                 "maxTryCount": 5,
        //                 "maxTryDuration": 86400000,
        //                 "identifier": "59b3375df84e923e3c3befef"
        //             },
        //             "identifier": "59b338daf84e923e3c3beff2"
        //         },
        //         {
        //             "title": "raw",
        //             "commands": [
        //                 {
        //                     "command": "java",
        //                     "arguments": [
        //                         "-jar",
        //                         "mapper.jar",
        //                         "raw",
        //                         "knowledgeStore",
        //                         "D:\\KnowledgeGraph\\updater_link\\raw"
        //                     ],
        //                     "environment": {},
        //                     "continueOnFail": false,
        //                     "workingDirectory": "C:\\Workspace\\knowledge_graph\\knowledge-base-mapping\\runner\\target",
        //                     "result": null
        //                 }
        //             ],
        //             "creationEpoch": 1504917704943,
        //             "remindedTryCount": 4,
        //             "validUntilEpoch": 1505004104943,
        //             "startEpoch": 1504917704949,
        //             "progress": 25,
        //             "endEpoch": null,
        //             "state": "Running",
        //             "definition": {
        //                 "title": "raw",
        //                 "commands": [
        //                     {
        //                         "command": "java",
        //                         "arguments": [
        //                             "-jar",
        //                             "mapper.jar",
        //                             "raw",
        //                             "knowledgeStore",
        //                             "D:\\KnowledgeGraph\\updater_link\\raw"
        //                         ],
        //                         "environment": {},
        //                         "continueOnFail": false,
        //                         "workingDirectory": "C:\\Workspace\\knowledge_graph\\knowledge-base-mapping\\runner\\target",
        //                         "result": null
        //                     }
        //                 ],
        //                 "creationEpoch": 0,
        //                 "maxTryCount": 5,
        //                 "maxTryDuration": 86400000,
        //                 "identifier": "59b3375df84e923e3c3befef"
        //             },
        //             "identifier": "59b338c8f84e923e3c3beff1"
        //         }
        //     ]
        // };

    });
