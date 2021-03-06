/// <reference path="../apimanPlugin.ts"/>
module Apiman {

    export var ConsumerSvcsController = _module.controller("Apiman.ConsumerSvcsController",
        ['$q', '$location', '$scope', 'ApimanSvcs', 'PageLifecycle', 
        ($q, $location, $scope, ApimanSvcs, PageLifecycle) => {
            var params = $location.search();
            if (params.q) {
                $scope.serviceName = params.q;
            }

            $scope.searchSvcs = function(value) {
                $location.search('q', value);
            };
            
            var promise = $q.all({
                services: $q(function(resolve, reject) {
                    if (params.q) {
                        var body:any = {};
                        body.filters = [];
                        body.filters.push( {"name": "name", "value": "%" + params.q + "%", "operator": "like"});
                        var searchStr = JSON.stringify(body);
                        
                        ApimanSvcs.save({ entityType: 'search', secondaryType: 'services' }, searchStr, function(reply) {
                            resolve(reply.beans);
                        }, reject);
                    } else {
                        resolve([]);
                    }
                })
            });

            PageLifecycle.loadPage('ConsumerSvcs', promise, $scope, function() {
                $('#apiman-search').focus();
            });
        }]);

}
