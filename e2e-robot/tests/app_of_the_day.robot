*** Settings ***
Library     Browser
Resource    ../resources/common.resource
Suite Setup    Setup Browser With Recording
Suite Teardown    Close Browser
Test Teardown    Capture Test Screenshot

*** Variables ***
${AOTD_CARD}    .aotd-slide.active .app-of-the-day

*** Test Cases ***
App Of The Day Card Visible
    [Documentation]    Verify the App of the Day hero banner is visible on the homepage
    Open AppVault
    Get Element    ${AOTD_CARD}

Card Shows App Name
    [Documentation]    Verify the App of the Day card contains text (app name is not empty)
    Open AppVault
    ${text}=    Get Text    ${AOTD_CARD}
    Should Not Be Empty    ${text}

Card Links To App Detail
    [Documentation]    Click the App of the Day card and verify URL contains /app/
    Open AppVault
    Click    .aotd-slide.active
    Wait For Load State    networkidle
    ${url}=    Get Url
    Should Contain    ${url}    /app/

Card Has Background Image
    [Documentation]    Verify the .app-of-the-day element has a background-image CSS property set
    Open AppVault
    ${bg}=    Get Style    ${AOTD_CARD}    background-image
    Should Not Be Equal    ${bg}    none
    Should Not Be Empty    ${bg}
