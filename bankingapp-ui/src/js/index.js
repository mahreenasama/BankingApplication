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
            .when('/updateAccount/:id', {
                templateUrl: 'account/updateAccount.html',
                controller: 'AccountController as $ctrl'
            })
            .when('/accountDetails/:accountId', {
                templateUrl: 'account/accountDetails.html',
                controller: 'AccountController as $ctrl'
            })
            .when('/accounts', {
                templateUrl: 'account/accounts.html',
                controller: 'AccountController as $ctrl'
            })
            .when('/transactions', {
                templateUrl: 'transaction/transactions.html',
                controller: 'TransactionController as $ctrl'
            })
            .when('/createTransaction', {
                templateUrl: 'transaction/createTransaction.html',
                controller: 'TransactionController as $ctrl'
            })
            .when('/transferAmount/:user', {
                templateUrl: 'transaction/transferAmount.html',
                controller: 'TransactionController as $ctrl'
            })
            .when('/userDetails/:accountId', {
                templateUrl: 'user/userDetails.html',
                controller: 'AccountController as $ctrl'
            })
            .when('/changePassword/:accountId', {
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
