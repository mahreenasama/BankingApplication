(function() {
    'use strict';

    function AccountService($resource) {
        return $resource('/bankingapp/api/v1/accounts/:id', { id: '@id' });
    }
    angular.module('bankingapp-ui').factory('AccountService', ['$resource', AccountService]);

    function BalanceService($resource) {
        return $resource('/bankingapp/api/v1/balances/:extraPath', { extraPath: '@extraPath' });
    }
    angular.module('bankingapp-ui').factory('BalanceService', ['$resource', BalanceService]);

    function TransactionService($resource) {
        return $resource('/bankingapp/api/v1/transactions/:extraPath', { extraPath: '@extraPath' });
    }
    angular.module('bankingapp-ui').factory('TransactionService', ['$resource', TransactionService]);

    function UserService($resource) {
            return $resource('/bankingapp/api/v1/users/:extraPath', { extraPath: '@extraPath' });
    }
    angular.module('bankingapp-ui').factory('UserService', ['$resource', UserService]);


    function AccountController($location, $routeParams, AccountService, BalanceService, TransactionService, UserService) {
        var self = this;

        self.service = AccountService;
        self.balanceService = BalanceService;
        self.transactionService = TransactionService;
        self.userService = UserService;

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
        self.oldPassword = '';
        self.newPassword = '';
        self.reEnterNewPassword = '';
        self.userAccountId;
        self.accountsExist = true;

        self.init = function() {
            console.log('in init of account');
        }

        //----------- search account by name function --------------
        self.search = function() {
            self.accountsExist = true;
            document.getElementById("noMoreAccountsError").innerHTML = "";

            var nameParam = '';
            if (self.name) {
                nameParam = self.name;
            } else {
                nameParam = '';
            }

            self.service.get({ name: nameParam }).$promise.then(function(response) {
                console.log('get accounts');
                self.accounts = response.content;

                self.balanceService.get({ extraPath: 'latest' }).$promise.then(function(response) {
                    console.log('get balances');
                    self.balances = response.content;

                    self.accountsBalances = self.accounts.map(function(item, index) {
                        return { account: item, balance: self.balances[index] };
                    });
                }).catch(function (error) {
                    console.log('get bal error');
                    if (error.status === -1 || error.status === 401) {
                        $location.path('/login');
                    } else if (error.status === 403) {
                        $location.path('/forbidden');
                    } else {
                        $location.path('/notFound');
                    }
                });
            }).catch(function (error) {
                console.log('get accounts error');
                if (error.status === -1 || error.status === 401) {
                    $location.path('/login');
                } else if (error.status === 403) {
                    $location.path('/forbidden');
                } else if (error.status === 404) {
                    self.accountsExist = false;
                    document.getElementById("noMoreAccountsError").innerHTML = "Not Exist!";
                } else {
                    $location.path('/notFound');
                }
            });
        };

        //----------- create account function --------------
        self.save = function() {
            self.service.save(self.accountItem).$promise.then(function(response) {
                //self.accountItem = response.content;
                alert('account created successfully');
                $location.path('/accounts');
            }).catch(function (error) {
                if (error.status === -1 || error.status === 401 || error.status === 403) {
                    $location.path('/login');
                } else if (error.status === 409) {
                    alert("account already exists");
                } else {
                    $location.path('/notFound');
                }
            });
        };

        //----------- update account page loads --------------
        self.getPrevDataToUpdate = function() {
            self.service.get({ id: $routeParams.id }).$promise.then(function(response) {
                self.accountItem = response.content;
            }).catch(function (error) {
                if (error.status === -1 || error.status === 401) {
                    $location.path('/login');
                } else if (error.status === 403) {
                    $location.path('/forbidden');
                } else {
                    $location.path('/notFound');
                }
            });
        };

        //----------- update account function --------------
        self.updateAccount = function() {
            self.service.save(self.accountItem).$promise.then(function(response) {
                alert('account updated successfully');
                $location.path('/accounts');
            }).catch(function (error) {
                if (error.status === -1 || error.status === 401 || error.status === 403) {
                    $location.path('/login');
                } else {
                    $location.path('/notFound');
                }
            });
        }

        //----------------Delete account function-----------------
        self.deleteAccount = function(id) {
            self.service.delete({ id: id }).$promise.then(function(response) {
                alert('account deleted successfully');
                self.name = '';
                self.search();
            }).catch(function (error) {
                if (error.status === -1 || error.status === 401 || error.status === 403) {
                    $location.path('/login');
                } else {
                    $location.path('/notFound');
                }
            });
        }

        //------------View account details function----------
        self.accountDetails = function () {
            self.userAccountId = $routeParams.accountId;

            // Fetch account details
            self.service.get({ id: $routeParams.accountId }).$promise.then(function (response) {
                self.accountItem = response.content;

                // Fetch account balance
                self.balanceService.get({ accountId: $routeParams.accountId }).$promise.then(function (response) {
                    self.balanceItem = response.content;
                }).catch(function (error) {
                    if (error.status === -1 || error.status === 401) {
                        $location.path('/login');
                    } else {
                        $location.path('/notFound');
                    }
                });
            }).catch(function (error) {
                if (error.status === -1 || error.status === 401) {
                    $location.path('/login');
                } else if (error.status === 403) {
                    $location.path('/forbidden');
                } else {
                    $location.path('/notFound'); // Means this error/request couldn't be handled
                }
            });
        }

        //-------------View balance history of account-----------------
        self.viewBalanceHistory = function (id) {
            // Fetch balance history
            self.balanceService.get({ extraPath: 'balance-history', accountId: id }).$promise.then(function (response) {
                self.showBalanceHistory = true;
                self.balanceHistory = response.content;
            }).catch(function (error) {
                if (error.status === -1 || error.status === 401) {
                    $location.path('/login');
                } else {
                    $location.path('/notFound'); // Means this error/request couldn't be handled
                }
            });
        }

        //-------------View transaction history of account-----------------
        self.viewTransactions = function (id) {
            // Fetch all transactions
            self.transactionService.get({ extraPath: 'transaction-history', accountId: id }).$promise.then(function (response) {
                self.showTransactions = true;
                self.accountTransactions = response.content;
            }).catch(function (error) {
                if (error.status === -1 || error.status === 401) {
                    $location.path('/login');
                } else if (error.status === 403) {
                    $location.path('/forbidden');
                } else if (error.status === 404) {
                    self.showTransactions = true;
                    document.getElementById('noTransactionsError').innerHTML = "No Transactions yet!";
                } else {
                    $location.path('/notFound');
                }
            });
        }

        //---------------Check Transfer by user--------------
        self.checkTransfer = function(prevBalance) {
            document.getElementById("limitedBalanceError").innerHTML = "";
            if (prevBalance > 10) {
                $location.path('/transferAmount/' + $routeParams.accountId);
            } else {
                document.getElementById("limitedBalanceError").innerHTML = "You cannot transfer because your balance is limited!";
            }
        };

        //----------change password-------------------
        self.changePassword = function() {
            if (self.newPassword != self.reEnterNewPassword) {
                document.getElementById("reEnterPasswordNotSameError").innerHTML = "New password must match with re-entered password!";
            } else {
                self.userService.save({ extraPath: 'changePassword', accountId: $routeParams.accountId }, self.newPassword).$promise.then(function(response) {
                    self.userItem = response.content;
                    alert('password changed successfully');
                    $location.path('/userDetails/' + $routeParams.accountId);
                }).catch(function(error) {
                    if (error.status === -1 || error.status === 401 || error.status === 403) {
                        $location.path('/login');
                    } else {
                        $location.path('/notFound');
                    }
                });
            }
        };


        self.init();
    }

    angular.module("bankingapp-ui").controller('AccountController', ['$location', '$routeParams', 'AccountService', 'BalanceService', 'TransactionService', 'UserService', AccountController]);

}());

