*** Settings ***
Library     Browser
Resource    ../resources/common.resource
Suite Setup    Setup Browser With Recording
Suite Teardown    Close Browser
Test Teardown    Capture Test Screenshot

*** Test Cases ***
Screenshot Thumbnails Visible
    [Documentation]    App detail page should display screenshot thumbnails
    Open AppVault    /app/1
    Get Element Count    .screenshot-img    greater than    0
    Get Element States    .screenshot-img >> nth=0    contains    visible

Lightbox Opens On Click
    [Documentation]    Clicking a screenshot thumbnail opens the lightbox overlay
    Open AppVault    /app/1
    Click    .screenshot-img >> nth=0
    Wait For Elements State    \#screenshotLightbox.active    visible    timeout=5s
    Get Element States    .lightbox-image    contains    visible

Lightbox Can Be Closed
    [Documentation]    The lightbox can be closed via the close button
    Open AppVault    /app/1
    Click    .screenshot-img >> nth=0
    Wait For Elements State    \#screenshotLightbox.active    visible    timeout=5s
    Click    .lightbox-close
    Wait For Elements State    \#screenshotLightbox.active    detached    timeout=5s

Lightbox Navigation
    [Documentation]    Clicking the next arrow changes the displayed image
    Open AppVault    /app/1
    Click    .screenshot-img >> nth=0
    Wait For Elements State    \#screenshotLightbox.active    visible    timeout=5s
    ${first_src}=    Get Attribute    .lightbox-image    src
    Click    .lightbox-next
    ${second_src}=    Get Attribute    .lightbox-image    src
    Should Not Be Equal    ${first_src}    ${second_src}
