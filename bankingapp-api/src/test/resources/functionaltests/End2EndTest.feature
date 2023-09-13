Feature: First E2E Test

  Background: User generates token for Authorisation
    Given I am an authorized user

  Scenario: the Authorized user can Add and Remove an Account
    Given A list of accounts is available
    When I add an account to bank accounts list
    Then the account is added
    When I remove an account from bank accounts list
    Then the account is removed