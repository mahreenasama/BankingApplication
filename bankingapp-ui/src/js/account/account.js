(function() {
    'use strict';


    function AccountService($resource) {
        return $resource('bankingapp/api/v1/accounts/:extraPath/:id', { extraPath: '@extraPath', id: '@id' });
    }
    angular.module('bankingapp-ui').factory('AccountService', ['$resource', AccountService]);

    function BalanceService($resource) {
        return $resource('bankingapp/api/v1/balances/:extraPath/:accountId', { extraPath: '@extraPath', accountId: '@accountId' });
    }
    angular.module('bankingapp-ui').factory('BalanceService', ['$resource', BalanceService]);

    function TransactionService($resource) {
        return $resource('bankingapp/api/v1/transactions/:extraPath/:accountId', { extraPath: '@extraPath', accountId: '@accountId' });
    }
    angular.module('bankingapp-ui').factory('TransactionService', ['$resource', TransactionService]);

    function UserService($resource) {
            return $resource('bankingapp/api/v1/user/:extraPath/:accId', { extraPath: '@extraPath', accId: '@accId' });
    }
    angular.module('bankingapp-ui').factory('UserService', ['$resource', UserService]);

    function SharedService() {
        var account = {};
        var accountBalance = {};
        var loggedInUserAccountId;

        return this;
    }
    angular.module('bankingapp-ui').factory('SharedService', [SharedService]);


    function AccountController($location, $routeParams, AccountService, BalanceService, TransactionService, UserService, SharedService) {
        var self = this;

        self.service = AccountService;
        self.balanceService = BalanceService;
        self.transactionService = TransactionService;
        self.userService = UserService;
        self.sharedService = SharedService;

        self.accounts = [];          //array of accounts
        self.balances = [];          //array of balances
        self.accountsBalances = [];  //combine array of accounts and balances
        self.balanceHistory = [];    //balance history of a particular account
        self.name = '';              //name to search for account
        self.showBalanceHistory = false;
        self.accountTransactions = []; //array of transactions
        self.showTransactions = false;
        self.accountItem = {};
        self.balanceItem = {};
        self.userItem = {};
        self.sharedService.loggedInUserAccountId = $routeParams.userAccountId;
        self.oldPassword = '';
        self.newPassword = '';
        self.reEnterNewPassword = '';

        self.init = function() {
            console.log('in init of account');
        }

        // ------------------ Load accounts when the page loads -----------
        self.loadAccounts = function () {
            document.getElementById("noMoreAccountsError").innerHTML = "";

            self.service.get().$promise.then(function (response) {
                self.accounts = response.content;

                self.balanceService.get().$promise.then(function (response) {
                    self.balances = response.content;

                    self.accountsBalances = self.accounts.map(function (item, index) {
                        return { account: item, balance: self.balances[index] };
                    });
                });
            }).catch(function (error) {
                // Trying to handle each and every error that the user can generate (even through the URL as well)
                if (error.status === 302) {
                    $location.path('/login'); // Nobody (user/admin) is logged in yet, so login
                } else if (error.status === 403) {
                    $location.path('/forbidden');
                } else if (error.status === 404) {
                    document.getElementById("noMoreAccountsError").innerHTML = "No More Accounts Exist!";
                } else {
                    $location.path('/notFound'); // Means this error/request couldn't be handled
                }
            });
        }

        //----------- search account by name function --------------
        self.search = function() {
                    document.getElementById("noMoreAccountsError").innerHTML = "";

            var nameParam = '';
            if (self.name) {
                nameParam = self.name;
            } else {
                nameParam = '';
            }
            self.service.get({ extraPath: 'findByName', name: nameParam }).$promise.then(function(response) {
                self.accounts = response.content;

                self.balanceService.get({ extraPath: 'getLatestBalances' }).$promise.then(function(response) {
                    self.balances = response.content;

                    self.accountsBalances = self.accounts.map(function(item, index) {
                        return { account: item, balance: self.balances[index] };
                    })
                })
            })
            .catch(function(error){
                            if(error.status === 404){
                                document.getElementById("noMoreAccountsError").innerHTML = "No More Accounts Exist!";
                            }
                        })
        }

        //----------- create account function --------------
        self.save = function() {
            self.service.save(self.accountItem).$promise.then(function(response) {
                //self.accountItem = response.content;
                alert('account created successfully');
                $location.path('/account');
            });
        }

        //----------- update account redirect function --------------
        self.update = function(id) {
            $location.path('/updateAccount');
            self.service.get({ id: id }).$promise.then(function(response) {
                self.sharedService.account = response.content;
            });
        }

        //----------- update account function --------------
        self.updateAccount = function() {
            self.service.save(self.sharedService.account).$promise.then(function(response) {
                alert('account updated successfully');
                $location.path('/account');
            });
        }

        //----------- delete account function --------------
        self.delete = function(id) {
            console.log('id for delete is: '+id);
            self.service.delete({ extraPath: '', id: id }).$promise.then(function(response) {
                alert('account deleted successfully');
                self.loadAccounts();
            });
        }

        //------------View account details function----------
        self.viewDetails = function (id) {
            $location.path('/accountDetails');

            // Fetch account details
            self.service.get({ id: id }).$promise.then(function (response) {
                self.sharedService.account = response.content;

                // Fetch account balance
                            self.balanceService.get({ accountId: id }).$promise.then(function (response) {
                                self.sharedService.accountBalance = response.content;
                            });

            }).catch(function (error) {
                              if (error.status === 302) {
                                  $location.path('/login');
                              } else if (error.status === 403) {
                                  $location.path('/forbidden');
                              } else {
                                  $location.path('/notFound'); // Means this error/request couldn't be handled
                              }
                              })
        }

        //-------------View balance history of account-----------------
        self.viewBalanceHistory = function (id) {
            // Fetch balance history
            self.balanceService.get({ extraPath: 'balanceHistory', accountId: id }).$promise.then(function (response) {
                self.showBalanceHistory = true;
                self.balanceHistory = response.content;
            });
        }

        //---------------Get transaction history by account id--------------
        self.viewTransactions = function (id) {
            // Fetch all transactions
            self.transactionService.get({ extraPath: 'allTransactions', accountId: id }).$promise.then(function (response) {
                self.showTransactions = true;
                self.accountTransactions = response.content;
            })
        }

        //---------------View User Details--------------
        self.viewUserDetails = function(id) {
            document.getElementById("limitedBalanceError").innerHTML = "";

            // Fetch account details
            self.service.get({ extraPath: 'accountDetails', id: id }).$promise.then(function(response) {
                self.accountItem = response.content;
            }).catch(function(error){
                if(error.status === 403){
                        $location.path('/forbidden');
                }
            })

            // Fetch account balance
            self.balanceService.get({ accountId: id }).$promise.then(function(response) {
                self.balanceItem = response.content;
            });
        };

        //---------------Check Transfer by user--------------
                self.checkTransfer = function(prevBalance, fromAccId) {
                        document.getElementById("limitedBalanceError").innerHTML = "";

                    if(prevBalance > 10){
                        $location.path('/transferByUser/' + fromAccId + '/' + prevBalance);
                    }
                    else {
                        document.getElementById("limitedBalanceError").innerHTML = "You cannot transfer because your balance is limited!";
                    }
                };
        //---------------change password--------------
         self.goToChangePassword = function(accId) {
                        $location.path('/changePassword/' + accId);
         }
         //----------change password-------------------
         self.changePassword = function() {
                             // Fetch user details
                             self.userService.get({ accId: $routeParams.accId }).$promise.then(function(response) {
                                 self.userItem = response.content;
                                 var passWithoutNoop = self.userItem.password.slice(6);
                                 console.log('withut noop:'+passWithoutNoop+"::");
                                 if(self.oldPassword != passWithoutNoop){
                                     document.getElementById("oldPasswordNotMatchesError").innerHTML = "Old password not matched!";
                                         document.getElementById("reEnterPasswordNotSameError").innerHTML = "";

                                 }
                                 else{
                                     document.getElementById("oldPasswordNotMatchesError").innerHTML = "";

                                     if(self.newPassword != self.reEnterNewPassword){
                                         document.getElementById("reEnterPasswordNotSameError").innerHTML = "New password must match with re-entered password!";
                                     }
                                     else{
                                                             self.userService.save({ extraPath: 'changePassword', accId: $routeParams.accId }, self.newPassword).$promise.then(function(response) {
                                                                 self.userItem = response.content;
                                                                 alert('password changed successfully');
                        $location.path('/userDetails/' + $routeParams.accId);

                                                             });
                                     }
                                 }
                             });

                         };




        self.init();
    }

    angular.module("bankingapp-ui").controller('AccountController', ['$location', '$routeParams', 'AccountService', 'BalanceService', 'TransactionService', 'UserService', 'SharedService', AccountController]);

}());

