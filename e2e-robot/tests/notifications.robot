*** Settings ***
Library     Browser
Resource    ../resources/common.resource
Suite Teardown    Close Browser
Test Teardown    Capture Test Screenshot

*** Test Cases ***
Notification Bell Visible When Logged In
    [Documentation]    Authenticated user should see the notification bell in the navbar
    Setup Browser With Recording
    Login As User
    Open AppVault    /
    Wait For Elements State    \#notificationBell    visible    timeout=5s

Notification Dropdown Opens
    [Documentation]    Clicking the bell should open a dropdown panel
    Setup Browser With Recording
    Login As User
    Open AppVault    /
    Click    \#notifDropdownToggle
    Wait For Elements State    \#notifDropdown.show    visible    timeout=5s

Notifications Show Items Or Empty State
    [Documentation]    The dropdown should show notification items or an empty message
    Setup Browser With Recording
    Login As User
    Open AppVault    /
    Click    \#notifDropdownToggle
    Wait For Elements State    \#notifDropdown.show    visible    timeout=5s
    Sleep    1s    Wait for AJAX to load notifications
    ${item_count}=    Get Element Count    \#notifList .notif-item
    ${text}=    Get Text    \#notifList
    ${has_items}=    Evaluate    ${item_count} > 0
    ${has_empty}=    Evaluate    "No notifications" in """${text}"""
    ${has_content}=    Evaluate    ${has_items} or ${has_empty}
    Should Be True    ${has_content}    Dropdown should show items or empty state message

Bell Hidden When Anonymous
    [Documentation]    Anonymous users should not see the notification bell
    Setup Browser With Recording
    Open AppVault    /
    ${count}=    Get Element Count    \#notificationBell
    Should Be Equal As Integers    ${count}    0
