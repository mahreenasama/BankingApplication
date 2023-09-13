(function() {
    'use strict';

    function TransactionService($resource) {
        return $resource('/bankingapp/api/v1/transactions/:extraPath', {
            extraPath: '@extraPath'
        });
    }
    angular.module('bankingapp-ui').factory('TransactionService', ['$resource', TransactionService]);

    function TransactionBalanceService($resource) {
        return $resource('/bankingapp/api/v1/balances/:extraPath', { extraPath: '@extraPath' });
    }
    angular.module('bankingapp-ui').factory('TransactionBalanceService', ['$resource', TransactionBalanceService]);


    function TransactionController($location, $routeParams, TransactionService, TransactionBalanceService) {
        var self = this;

        self.service = TransactionService;
        self.transactionBalanceService = TransactionBalanceService;

        self.transactions = [];
        self.transactionItem = {};
        self.transactionItem.description = 'Deposit';
        self.showTransactions = false;
        self.accountId;
        self.fromAccountId;
        self.toAccountId;
        self.amount;
        self.prevBalance;
        self.transferByAdmin = false;


        self.init = function() {
            console.log('in init of trans');
        };

        //------------------------------------------------------------------------------------
        self.allTransactions = function() {
            document.getElementById("noTransactionsError").innerHTML = "";
            self.service.get().$promise.then(function(response) {
                self.showTransactions = true;
                self.transactions = response.content;
            }).catch(function (error) {
                if (error.status === -1 || error.status === 401) {
                    $location.path('/login');
                } else if (error.status === 403) {
                    $location.path('/forbidden');
                } else if (error.status === 404) {
        self.showTransactions = true;
                    document.getElementById("noTransactionsError").innerHTML = "No transaction done yet!";
                } else {
                    $location.path('/notFound');
                }
            });
        };

        //------------------------------------------------------------------------------------
        self.checkCreateTransaction = function() {
            document.getElementById("minDepositError").innerHTML = "";
            document.getElementById("accountError").innerHTML = "";
            document.getElementById("withdrawError").innerHTML = "";

            if (self.transactionItem.description == 'Deposit') {
                self.transactionItem.debitCreditIndicator = "+ CR";
            } else {
                self.transactionItem.debitCreditIndicator = "- DB";
            }
            // First, check prev balance
            self.transactionBalanceService.get({ accountId: self.accountId }).$promise.then(function(response) {
                self.prevBalance = response.content.amount;
            }).then(self.checkDepositOrWithdraw).catch(function(error) {
                if (error.status === -1 || error.status === 401 || error.status === 403) {
                    $location.path('/login');
                } else if (error.status === 404) {
                    document.getElementById("accountError").innerHTML = "Account number " + self.accountId + " not exist!";
                } else {
                    $location.path('/notFound');
                }
            });
        };

        //-----------------------------------------------------
        self.checkDepositOrWithdraw = function() {
            if (self.transactionItem.description == 'Deposit') {
                if (self.prevBalance === 0 && self.transactionItem.amount < 10) {
                    // Means account was recently created
                    document.getElementById("minDepositError").innerHTML = "Deposit at least PKR 10!";
                } else {
                    self.createTransaction();
                }
            } else {
                if (self.prevBalance > 10) {
                    if (self.prevBalance - self.transactionItem.amount < 10) {
                        document.getElementById("withdrawError").innerHTML = "Can withdraw at max PKR " + (self.prevBalance - 10) + "!";
                    } else {
                        self.createTransaction();
                    }
                } else {
                    document.getElementById("withdrawError").innerHTML = "Cannot withdraw because balance is limited!";
                }
            }
        };

        //---------------------------------------------------------------------------
        self.createTransaction = function(){
            document.getElementById("accountError").innerHTML = "";

            self.service.save({ accountId: self.accountId }, self.transactionItem).$promise.then(function(response) {
                alert(self.transactionItem.description + ' Successful');
                $location.path('/transactions');
            }).catch(function (error) {
                if (error.status === -1 || error.status === 401 || error.status === 403) {
                    $location.path('/login');
                } else if (error.status === 404) {
                    document.getElementById("accountError").innerHTML = "Account number "+self.accountId+" not exist!";
                } else {
                    $location.path('/notFound');
                }
            });
        }

        //------------------------------------------------------------------------------------
        self.enableAdminFields = function() {
            if ($routeParams.user == 'admin') {
                self.transferByAdmin = true;
            } else if (isNaN(parseInt($routeParams.user))) {
                $location.path('/notFound');
            } else {
                self.transferByAdmin = false;
                self.fromAccountId = $routeParams.user;
            }
        }

        //------------------------------------------------------------------------------------
        self.transferAmount = function() {
            document.getElementById("accountError").innerHTML = "";
            document.getElementById("transferError").innerHTML = "";

            self.transactionBalanceService.get({ accountId: self.fromAccountId }).$promise.then(function (response) {
                self.prevBalance = response.content.amount;

                if(self.prevBalance > 10) {
                    if(self.prevBalance - self.amount < 10) {
                        document.getElementById("transferError").innerHTML = "Can transfer at max PKR "+ (self.prevBalance-10) +"!";
                    }
                    else if(self.fromAccountId == self.toAccountId) {
                        document.getElementById("transferError").innerHTML = "Cannot transfer to yourself!";
                    }
                    else{
                        self.service.save({
                            extraPath: 'transfer',
                            fromAccountId: self.fromAccountId,
                            toAccountId: self.toAccountId
                        }, self.amount).$promise.then(function(response) {
                            //self.transactionItem.id = response.content.id;
                            alert('amount transferred successfully');

                            if($routeParams.user == 'admin') {
                                $location.path('/transactions');
                            }
                            else{
                                $location.path('/userDetails/'+ self.fromAccountId);
                            }
                        }).catch(function(error){
                            if(error.status === -1 || error.status === 401 || error.status === 403){
                                $location.path('/login');
                            }
                            else if(error.status === 404) {
                                document.getElementById("accountError").innerHTML = "Account number "+self.toAccountId+" (receiver) not exist!";
                            }
                            else{
                                $location.path('/notFound');
                            }
                        });
                    }
                }
                else{
                    document.getElementById("transferError").innerHTML = "Cannot transfer because sender's balance is limited!";
                }
            }).catch(function (error) {
                if (error.status === -1 || error.status === 401) {
                    $location.path('/login');
                } else if (error.status === 404) {
                    document.getElementById("accountError").innerHTML = "Account number "+self.fromAccountId+" (sender) not exist!";
                } else {
                    $location.path('/notFound');
                }
            });
        };


        self.init();
    }

    angular.module("bankingapp-ui").controller('TransactionController', ['$location', '$routeParams', 'TransactionService', 'TransactionBalanceService', TransactionController]);
})();
