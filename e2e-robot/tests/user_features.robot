*** Settings ***
Library     Browser
Resource    ../resources/common.resource
Suite Setup    Setup User Session
Suite Teardown    Close Browser
Test Teardown    Capture Test Screenshot

*** Keywords ***
Setup User Session
    Setup Browser With Recording
    Login As User

*** Test Cases ***
Profile Page Loads
    [Documentation]    Authenticated user should see profile form
    Open AppVault    /user/profile
    Get Element    input[name="firstName"]

Update Profile
    [Documentation]    User should be able to update their profile
    Open AppVault    /user/profile
    Fill Text    input[name="firstName"]    Updated
    Fill Text    input[name="lastName"]    Name
    Evaluate JavaScript    input[name="firstName"]
    ...    (elem) => elem.closest("form").submit()
    Wait For Load State    networkidle
    Get Url    contains    saved

My Reviews Page Loads
    [Documentation]    My reviews page should be accessible
    Open AppVault    /user/my-reviews
    Get Element    body
