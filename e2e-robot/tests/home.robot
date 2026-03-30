*** Settings ***
Library     Browser
Resource    ../resources/common.resource
Suite Setup    Setup Browser With Recording
Suite Teardown    Close Browser
Test Teardown    Capture Test Screenshot

*** Test Cases ***
Home Page Loads Successfully
    [Documentation]    Verify the home page renders with expected content
    Open AppVault
    Get Title    contains    AppVault
    Get Text    body    contains    Top Free Apps

Home Page Displays App Categories
    [Documentation]    Verify category names appear on the home page
    Open AppVault
    Get Text    body    contains    Productivity
    Get Text    body    contains    Games
    Get Text    body    contains    Education

Home Page Has Navigation Links
    [Documentation]    Verify Browse and Sign In links are visible in the navbar
    Open AppVault
    Get Element    .nav-link >> text=Browse
    Get Element    .nav-link >> text=Sign In
