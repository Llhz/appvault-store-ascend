*** Settings ***
Library     Browser
Resource    ../resources/common.resource
Suite Setup    Setup Admin Session
Suite Teardown    Close Browser
Test Teardown    Capture Test Screenshot

*** Keywords ***
Setup Admin Session
    Setup Browser With Recording
    Login As Admin

*** Test Cases ***
Admin Dashboard Loads
    [Documentation]    Admin should see the dashboard
    Open AppVault    /admin/dashboard
    Get Text    body    contains    Dashboard

Manage Apps Page Shows Apps
    [Documentation]    Admin apps list should show seeded apps
    Open AppVault    /admin/apps
    Get Text    body    contains    Focus

New App Form Loads
    [Documentation]    New app form should have a name input
    Open AppVault    /admin/apps/new
    Get Element    input[name="name"]

Edit App Form Loads With Data
    [Documentation]    Edit form should have a pre-filled name field
    Open AppVault    /admin/apps/1/edit
    ${value}=    Get Property    input[name="name"]    value
    Should Not Be Empty    ${value}

Manage Users Page Loads
    [Documentation]    Users list should show admin email
    Open AppVault    /admin/users
    Get Text    body    contains    admin@appvault.com

Regular User Gets 403 On Admin Pages
    [Documentation]    Non-admin user should be denied access
    New Context
    Login As User
    ${response}=    Goto    ${BASE_URL}/admin/dashboard
    Get Text    body    contains    403
