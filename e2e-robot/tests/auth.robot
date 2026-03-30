*** Settings ***
Library     Browser
Resource    ../resources/common.resource
Suite Setup    Setup Browser With Recording
Suite Teardown    Close Browser
Test Teardown    Capture Test Screenshot

*** Test Cases ***
Login Page Renders
    [Documentation]    Verify the login form has expected fields
    Open AppVault    /auth/login
    Get Element    input[name="username"]
    Get Element    input[name="password"]
    Get Element    button[type="submit"]

Register Page Renders
    [Documentation]    Verify the registration form has expected fields
    Open AppVault    /auth/register
    Get Element    input[name="firstName"]
    Get Element    input[name="lastName"]
    Get Element    input[name="email"]
    Get Element    input[name="password"]

Successful Admin Login
    [Documentation]    Admin should be able to log in and see profile
    Login As Admin
    Open AppVault    /user/profile
    Get Text    body    contains    Admin

Failed Login Shows Error
    [Documentation]    Invalid credentials should show an error page
    Open AppVault    /auth/login
    Fill Text    input[name="username"]    bad@email.com
    Fill Text    input[name="password"]    wrongpassword
    Click    button[type="submit"]
    Wait For Load State    networkidle
    Get Url    contains    error

Register New User
    [Documentation]    Registering a new user should redirect to success page
    ${timestamp}=    Evaluate    __import__('time').time()
    Open AppVault    /auth/register
    Fill Text    input[name="firstName"]    Robot
    Fill Text    input[name="lastName"]    Tester
    Fill Text    input[name="email"]    robot_${timestamp}@example.com
    Fill Text    input[name="password"]    Password1!
    Fill Text    input[name="confirmPassword"]    Password1!
    Click    input[name="firstName"]
    Evaluate JavaScript    input[name="firstName"]
    ...    (elem) => elem.closest("form").submit()
    Wait For Load State    networkidle
    Get Url    contains    registered

Unauthenticated User Redirected To Login
    [Documentation]    Visiting a protected page without login should redirect
    Open AppVault    /user/profile
    Get Url    contains    auth/login
