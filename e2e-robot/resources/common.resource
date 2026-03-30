*** Settings ***
Library     Browser
Resource    ../resources/common.resource

*** Variables ***
${BASE_URL}         http://localhost:8080
${VIDEO_DIR}        ${CURDIR}/../results/videos
${SCREENSHOT_DIR}   ${CURDIR}/../results/screenshots

*** Keywords ***
Setup Browser With Recording
    New Browser    chromium    headless=true
    New Context    recordVideo={"dir": "${VIDEO_DIR}"}

Capture Test Screenshot
    Take Screenshot    fullPage=true

Open AppVault
    [Arguments]    ${path}=/
    New Page    ${BASE_URL}${path}

Login As Admin
    Open AppVault    /auth/login
    Fill Text    input[name="username"]    admin@appvault.com
    Fill Text    input[name="password"]    Admin123!
    Click    button[type="submit"]
    Wait For Load State    networkidle

Login As User
    Open AppVault    /auth/login
    Fill Text    input[name="username"]    user@appvault.com
    Fill Text    input[name="password"]    User123!
    Click    button[type="submit"]
    Wait For Load State    networkidle
