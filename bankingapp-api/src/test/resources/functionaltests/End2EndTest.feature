Feature: First E2E Tests for BankingApp Accounts API

BankingApp swagger URL: http://localhost:9080/bankingapp/swagger-ui/index.html

  Background: User generates token for Authorisation
    Given I am an authorized user

  Scenario: The Authorized user can Add an Account
    Given A list of accounts is available
    When I add an account to bank accounts list
    Then The account is added