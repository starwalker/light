'use strict';

/**
 * @ngdoc function
 * @name lightApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the lightApp
 */
angular.module('lightApp')
    .controller('mainCtrl', ['$scope', function ($scope) {
        console.log('Now we are in mainCtrl');
        /*
        var getRecentPosts = {
            category : 'post',
            name : 'getRecentPosts',
            readOnly: true,
            data : {}
        };
        */
        $scope.awesomeThings = [1,2, 3];

        $scope.forumPosts = [];
        $scope.newsPosts = [];
        $scope.blogPosts = [];
        /*
        $http.post('api/rs', getRecentPosts)
            .success(function(result, status, headers, config) {
                console.log(result);
                $scope.forumPosts = result.forumPosts;
                $scope.newsPosts = result.newsPosts;
                $scope.blogPosts = result.blogPosts;
            });
        */
    }]);
