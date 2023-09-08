(function() {
    'use strict';

function SigningAccountService($resource) {
        return $resource('bankingapp/api/v1/account/:extraPath/:uname', { extraPath: '@extraPath', uname: '@uname' });
    }
    angular.module('bankingapp-ui').factory('SigningAccountService', ['$resource', SigningAccountService]);

function SigningSharedService() {
        var uname = '';
        var password = '';

        return this;
    }
    angular.module('bankingapp-ui').factory('SigningSharedService', [SigningSharedService]);


    function SigningController($http, $location, SigningAccountService, SigningSharedService) {
        var self = this;
        self.signingAccountService = SigningAccountService;

        self.signingSharedService = SigningSharedService;
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
                if (error.status === 403) {
                    self.login();
                }
            });
        }

        self.loginSuccess = function(response) {
            if (self.user.uname == "admin") {
                $location.path('/accounts');
            } else {
                self.signingAccountService.get({ extraPath: 'findByUname', uname: self.user.uname }).$promise.then(function(response) {
                    self.userAccountId = response.content.id;
                    $location.path('/userDetails/' + self.userAccountId);
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

    angular.module('bankingapp-ui').controller('SigningController', ['$http', '$location', 'SigningAccountService', 'SigningSharedService', SigningController]);

}());