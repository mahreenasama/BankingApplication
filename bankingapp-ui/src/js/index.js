(function() {
    'use strict';

    function BankingappConfig($locationProvider, $routeProvider) {
        $locationProvider.hashPrefix('!');
        $routeProvider
            .when('/home', {
                templateUrl: '/home.html'
            })
            .when('/contact', {
                templateUrl: '/contact.html'
            })
            .when('/about', {
                templateUrl: '/about.html'
            })
            .when('/notFound', {
                templateUrl: '/notFound.html',
            })
            .when('/forbidden', {
                templateUrl: '/forbidden.html',
            })
            .when('/login', {
                templateUrl: 'signing/login.html',
                controller: 'SigningController as $ctrl'
            })
            .when('/logout', {
                templateUrl: 'signing/logout.html',
                controller: 'SigningController as $ctrl'
            })
            .when('/createAccount', {
                templateUrl: 'account/createAccount.html',
                controller: 'AccountController as $ctrl'
            })
            .when('/updateAccount', {
                templateUrl: 'account/updateAccount.html',
                controller: 'AccountController as $ctrl'
            })
            .when('/accountDetails', {
                templateUrl: 'account/accountDetails.html',
                controller: 'AccountController as $ctrl'
            })
            .when('/account', {
                templateUrl: 'account/accounts.html',
                controller: 'AccountController as $ctrl'
            })
            .when('/transaction', {
                templateUrl: 'transaction/transaction.html',
                controller: 'TransactionController as $ctrl'
            })
            .when('/deposit', {
                templateUrl: 'transaction/deposit.html',
                controller: 'TransactionController as $ctrl'
            })
            .when('/withdraw', {
                templateUrl: 'transaction/withdraw.html',
                controller: 'TransactionController as $ctrl'
            })
            .when('/transfer', {
                templateUrl: 'transaction/transfer.html',
                controller: 'TransactionController as $ctrl'
            })
            .when('/userDetails/:userAccountId', {
                templateUrl: 'user/userDetails.html',
                controller: 'AccountController as $ctrl'
            })
            .when('/transferByUser/:fromAccountId/:prevBalance', {
                 templateUrl: 'transaction/transferByUser.html',
                 controller: 'TransactionController as $ctrl'
            })
            .when('/changePassword/:accId', {
                 templateUrl: 'user/changePassword.html',
                 controller: 'AccountController as $ctrl'
            })
            .otherwise({
                redirectTo: '/home'
            });
    }

    angular.module('bankingapp-ui', ['ngRoute', 'ngResource', 'ng'])
        .config(['$locationProvider', '$routeProvider', BankingappConfig]);

}());
