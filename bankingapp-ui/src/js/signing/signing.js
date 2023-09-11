(function() {
    'use strict';


function SigningUserService($resource) {
        return $resource('/bankingapp/api/v1/users/:extraPath', { extraPath: '@extraPath', uname: '@uname' });
    }
    angular.module('bankingapp-ui').factory('SigningUserService', ['$resource', SigningUserService]);



    function SigningController($http, $location, SigningUserService) {
        var self = this;
        self.signingUserService = SigningUserService;

        self.user = {};
        self.userAccountId;


        self.login = function () {
            document.getElementById("loginInvalidError").innerHTML = "";

            $http.post('bankingapp/login', 'username=' + self.user.uname + '&password=' + self.user.password, {
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                }
            })
            .then(self.loginSuccess)
            .catch(function (error) {
                if (error.status === -1) {
                    document.getElementById("loginInvalidError").innerHTML = "Username or Password Invalid!";
                }
                else if (error.status === 403) {
                    self.login();
                }
                else {
                $location.path('/notFound');
                                }
            });
        }

        self.loginSuccess = function(response) {
            if (self.user.uname == "admin") {
                $location.path('/accounts');
            } else {
                self.signingUserService.get({ extraPath: 'findByUname', uname: self.user.uname }).$promise.then(function(response) {
                    self.userAccountId = response.content.account.id;
                    $location.path('/userDetails/' + self.userAccountId);
                })
                catch(function (error) {
                                if (error.status === 302) {
                                    document.getElementById("loginInvalidError").innerHTML = "Username or Password Invalid!";
                                }
                                else if (error.status === 403) {
                                $location.path('/forbidden');
                                }
                                else {
                                $location.path('/notFound');
                                                }
                            });
            }
        };

        /*self.loginFailure = function(response) {
            console.log('failure login', response);
            self.user.error = response.data.message;
            document.getElementById('response-error').innerHTML = response.data.message;
        }*/

        self.logout = function() {
            $http.post('bankingapp/logout').then(self.logoutSuccess)
            .catch(function(error){
                if(error.status === 403){
                    self.logout();              //calling 2 times
                }
                else{
                                $location.path('/notFound');
                }
            })
        }

        self.logoutSuccess = function(response) {
            $location.path('/login');
        }

        /*self.logoutFailure = function(response) {
            console.log('logout failure');
            self.user.error = response.data.message;
        }*/

    }

    angular.module('bankingapp-ui').controller('SigningController', ['$http', '$location', 'SigningUserService', SigningController]);

}());