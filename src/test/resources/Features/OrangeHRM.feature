Feature: Login and Add Employee
  Scenario: Add 3-4 Employees using Data Driven Testing
    Given User is on the login page
    When User enters valid username and password
    And User clicks on the login button
    When User clicks on PIM
    And User adds employees from the data in the CSV file
    And User verifies the presence of added employee on employee list page
    And User logs out from the dashboard
