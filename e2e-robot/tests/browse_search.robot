*** Settings ***
Library     Browser
Resource    ../resources/common.resource
Suite Setup    Setup Browser With Recording
Suite Teardown    Close Browser
Test Teardown    Capture Test Screenshot

*** Test Cases ***
Browse Page Shows Apps
    [Documentation]    The browse page should display app cards
    Open AppVault    /browse
    Get Element Count    .app-card-grid    greater than    0

Category Filter Works
    [Documentation]    Clicking a category pill should filter by category
    Open AppVault    /browse
    Click    .category-pill >> text=Productivity
    Wait For Load State    networkidle
    Get Url    contains    category

Search Returns Results For Known App
    [Documentation]    Searching for a known app name should show results
    Open AppVault    /search?q=Focus
    Get Text    body    contains    Focus

Search Handles No Results Gracefully
    [Documentation]    Searching for nonsense should not crash
    Open AppVault    /search?q=xyznonexistent123
    Get Element    body

App Detail Page Loads
    [Documentation]    An app detail page should show app info and reviews
    Open AppVault    /app/1
    Get Element    h1
    Get Text    body    contains    Reviews
