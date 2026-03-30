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
    Open AppVault    /admin/dashboard

*** Test Cases ***
Analytics Dashboard Loads Charts
    [Documentation]    Admin dashboard should render Chart.js canvas elements
    Get Element Count    canvas    >=    1

Downloads Chart Has Data
    [Documentation]    Downloads chart canvas should be visible and rendered
    Get Element    \#downloadsChart
    ${height}=    Evaluate JavaScript    \#downloadsChart    (el) => el.height
    Should Be True    ${height} > 0

Stats Cards Show Numbers
    [Documentation]    Stat cards should contain numeric values
    ${values}=    Get Elements    .stat-value
    FOR    ${el}    IN    @{values}
        ${text}=    Get Text    ${el}
        Should Match Regexp    ${text}    \\d+
    END

Analytics API Returns JSON
    [Documentation]    Downloads API endpoint should return a non-empty JSON array
    ${result}=    Evaluate JavaScript    \#downloadsChart
    ...    async (el) => {
    ...        const resp = await fetch('/admin/api/stats/downloads');
    ...        return await resp.json();
    ...    }
    Should Not Be Empty    ${result}
