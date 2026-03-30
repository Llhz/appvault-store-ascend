*** Settings ***
Library     Browser
Resource    ../resources/common.resource
Suite Setup    Setup Browser With Recording
Suite Teardown    Close Browser
Test Setup    Open AppVault
Test Teardown    Capture Test Screenshot

*** Test Cases ***
Auto-suggest Appears On Typing
    [Documentation]    Typing at least 2 chars into the search input shows the suggestion dropdown
    Fill Text    \#searchInput    Foc
    Wait For Elements State    \#searchSuggest.active    visible    timeout=3s

Suggestions Show Matching Apps
    [Documentation]    Suggestion list contains an app name matching the query
    Fill Text    \#searchInput    Focus
    Wait For Elements State    \#searchSuggest.active    visible    timeout=3s
    ${name}=    Get Text    .search-suggest-item >> nth=0 >> .suggest-name
    Should Not Be Empty    ${name}
    Should Contain    ${name}    Focus

Clicking Suggestion Navigates To App
    [Documentation]    Clicking the first suggestion navigates to the app detail page
    Fill Text    \#searchInput    Focus
    Wait For Elements State    \#searchSuggest.active    visible    timeout=3s
    Click    .search-suggest-item >> nth=0
    Wait For Load State    networkidle
    Get Url    contains    /app/

Suggestions Dismiss On Outside Click
    [Documentation]    Clicking outside the dropdown closes the suggestions
    Fill Text    \#searchInput    Focus
    Wait For Elements State    \#searchSuggest.active    visible    timeout=3s
    Click    .navbar-brand
    Wait For Elements State    \#searchSuggest.active    detached    timeout=3s

Enter Key Submits Full Search
    [Documentation]    Pressing Enter submits the search form and navigates to the results page
    Fill Text    \#searchInput    Focus
    Keyboard Key    press    Enter
    Wait For Load State    networkidle
    Get Url    contains    /search
