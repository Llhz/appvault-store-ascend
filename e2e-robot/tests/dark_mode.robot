*** Settings ***
Library     Browser
Resource    ../resources/common.resource
Suite Setup    Setup Browser With Recording
Suite Teardown    Close Browser
Test Setup    Start Fresh Context
Test Teardown    Capture Test Screenshot

*** Variables ***
${TOGGLE}    id=darkModeToggle
${VIDEO_DIR}    ${CURDIR}/../results/videos

*** Keywords ***
Start Fresh Context
    New Context    recordVideo={"dir": "${VIDEO_DIR}"}

*** Test Cases ***
Dark Mode Toggle Activates
    [Documentation]    Click the dark mode toggle and verify html has data-theme=dark
    Open AppVault
    Click    ${TOGGLE}
    Get Attribute    html    data-theme    ==    dark

Dark Mode Persists Across Pages
    [Documentation]    Activate dark mode on homepage, navigate to /browse, verify persistence
    Open AppVault
    Click    ${TOGGLE}
    Get Attribute    html    data-theme    ==    dark
    Click    .nav-link >> text=Browse
    Wait For Load State    networkidle
    Get Attribute    html    data-theme    ==    dark

Dark Mode Persists After Refresh
    [Documentation]    Activate dark mode, reload page, verify data-theme persists via localStorage
    Open AppVault
    Click    ${TOGGLE}
    Get Attribute    html    data-theme    ==    dark
    Reload
    Wait For Load State    networkidle
    Get Attribute    html    data-theme    ==    dark

Dark Mode Can Be Deactivated
    [Documentation]    Activate dark mode then click toggle again to deactivate
    Open AppVault
    Click    ${TOGGLE}
    Get Attribute    html    data-theme    ==    dark
    Click    ${TOGGLE}
    Wait For Elements State    html[data-theme="dark"]    detached    timeout=5s
