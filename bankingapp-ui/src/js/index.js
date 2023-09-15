(function() {
    'use strict';


angular.module('bankingapp-ui', ['ngRoute', 'ngResource', 'ng'])
        .config(['$locationProvider', '$routeProvider', BankingappConfig])
        .run(['$rootScope', function ($rootScope) {
                $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
                $rootScope.title = current.$$route.title;
                document.title = $rootScope.title;
            });
        }]);


    function BankingappConfig($locationProvider, $routeProvider) {
        $locationProvider.hashPrefix('!');
        $routeProvider
            .when('/home', {
                title: 'Banking WebApp',
                templateUrl: '/home.html'
            })
            .when('/contact', {
                title: 'Contact',
                templateUrl: '/contact.html'
            })
            .when('/about', {
                title: 'About',
                templateUrl: '/about.html'
            })
            .when('/notFound', {
                templateUrl: '/notFound.html',
                title: '404 Error'
            })
            .when('/forbidden', {
                templateUrl: '/forbidden.html',
                title: '403 Error'
            })
            .when('/login', {
                templateUrl: 'signing/login.html',
                controller: 'SigningController as $ctrl',
                title: 'Login'
            })
            .when('/logout', {
                templateUrl: 'signing/logout.html',
                controller: 'SigningController as $ctrl',
                title: 'Logout'
            })
            .when('/createAccount', {
                templateUrl: 'account/createAccount.html',
                controller: 'AccountController as $ctrl',
                title: 'Create Account'
            })
            .when('/updateAccount/:id', {
                templateUrl: 'account/updateAccount.html',
                controller: 'AccountController as $ctrl',
                title: 'Update Account'
            })
            .when('/accountDetails/:accountId', {
                templateUrl: 'account/accountDetails.html',
                controller: 'AccountController as $ctrl',
                title: 'Account Details'
            })
            .when('/accounts', {
                templateUrl: 'account/accounts.html',
                controller: 'AccountController as $ctrl',
                title: 'Accounts'
            })
            .when('/transactions', {
                templateUrl: 'transaction/transactions.html',
                controller: 'TransactionController as $ctrl',
                title: 'Transactions'
            })
            .when('/createTransaction', {
                templateUrl: 'transaction/createTransaction.html',
                controller: 'TransactionController as $ctrl',
                title: 'Create Transaction'
            })
            .when('/transferAmount/:user', {
                templateUrl: 'transaction/transferAmount.html',
                controller: 'TransactionController as $ctrl',
                title: 'Transfer Amount'
            })
            .when('/userDetails/:accountId', {
                templateUrl: 'user/userDetails.html',
                controller: 'AccountController as $ctrl',
                title: 'User Details'
            })
            .when('/changePassword/:accountId', {
                 templateUrl: 'user/changePassword.html',
                 controller: 'AccountController as $ctrl',
                 title: 'Change Password'
            })
            .otherwise({
                redirectTo: '/home'
            });

    }


}());
