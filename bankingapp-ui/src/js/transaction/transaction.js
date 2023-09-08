(function() {
    'use strict';

    function TransactionService($resource) {
        return $resource('bankingapp/api/v1/transactions/:extraPath/:accountId/:toAccountId', {
            extraPath: '@extraPath',
            accountId: '@accountId',
            toAccountId: '@toAccountId'
        });
    }
    angular.module('bankingapp-ui').factory('TransactionService', ['$resource', TransactionService]);


    function TransactionController($location, $routeParams, TransactionService) {
        var self = this;

        self.service = TransactionService;
        self.transactions = [];
        self.transactionItem = {};
        self.showTransactions = false;
        self.accId;
        self.fromAccId;
        self.toAccId;
        self.amount;

        self.init = function() {
            console.log('in init');
        };

        self.allTransactions = function() {
            self.service.get().$promise.then(function(response) {
                self.showTransactions = true;
                self.transactions = response.content;
            });
        };

        self.deposit = function() {
            self.service.save({
                extraPath: 'deposit',
                accountId: self.accId
            }, self.transactionItem).$promise.then(function(response) {
                //self.transactionItem.id = response.content.id;
                alert('amount deposited successfully');
                $location.path('/transaction');
            });
        };

        self.withdraw = function() {
            self.service.save({
                extraPath: 'withdraw',
                accountId: self.accId
            }, self.transactionItem).$promise.then(function(response) {
                self.transactionItem.id = response.content.id;
                alert('amount withdrawn successfully');
                $location.path('/transaction');
            });
        };

        self.transfer = function() {
            self.service.save({
                extraPath: 'transfer',
                accountId: self.fromAccId,
                toAccountId: self.toAccId
            }, self.amount).$promise.then(function(response) {
                self.transactionItem.id = response.content.id;
                alert('amount transferred successfully');
                $location.path('/transaction');
            });
        };

        self.transferByUser = function() {

                        document.getElementById("TransferError").innerHTML = "";

                    self.fromAccId = $routeParams.fromAccountId;

                    if($routeParams.prevBalance - self.amount < 10){
                        document.getElementById("TransferError").innerHTML = "You can transfer at max PKR "+ ($routeParams.prevBalance-10) +"!";
                    }
                    else if(self.fromAccId == self.toAccId){
                        document.getElementById("TransferError").innerHTML = "Cannot transfer to yourself!";
                    }
                    else{
                        self.service.save({
                                                extraPath: 'transfer',
                                                accountId: self.fromAccId,
                                                toAccountId: self.toAccId
                                            }, self.amount).$promise.then(function(response) {
                                                self.transactionItem.id = response.content.id;
                                                alert('amount transferred successfully');
                                                $location.path('/userDetails/'+ self.fromAccId);
                                            })
                                            .catch(function(error){
                                                if(error.status === 403){
                                                    //self.transferByUser();          //trying to resolve that issue
                                                }
                                                if(error.status === 404){
                        document.getElementById("TransferError").innerHTML = "Account number "+self.toAccId+" not found!";

                                                }
                                            })
                    }


                };

        self.init();
    }

    angular.module("bankingapp-ui").controller('TransactionController', ['$location', '$routeParams', 'TransactionService', TransactionController]);

})();
