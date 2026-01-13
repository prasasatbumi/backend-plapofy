$base = "http://localhost:8081"
$ErrorActionPreference = "Stop"

function Login($username, $password) {
    $body = @{
        username = $username
        password = $password
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod -Method Post -Uri "$base/api/auth/login" -Body $body -ContentType "application/json"
        return $response.data.token
    } catch {
        Write-Error "Login failed for $username : $($_.Exception.Message)"
    }
}

function Get-Headers($token) {
    return @{
        Authorization = "Bearer $token"
        "Content-Type" = "application/json"
    }
}

Write-Host "1. Login as NASABAH..."
$nasabahToken = Login "nasabah" "password123"
$nasabahHeaders = Get-Headers $nasabahToken

Write-Host "2. Get Plafonds..."
$plafonds = Invoke-RestMethod -Method Get -Uri "$base/api/plafonds" -Headers $nasabahHeaders
$plafondId = $plafonds.data[0].id
Write-Host "   Selected Plafond ID: $plafondId"

Write-Host "3. Submit Loan..."
$loanBody = @{
    plafondId = $plafondId
    amount = 5000000
} | ConvertTo-Json
$loan = Invoke-RestMethod -Method Post -Uri "$base/api/loans" -Headers $nasabahHeaders -Body $loanBody
$loanId = $loan.data.id
Write-Host "   Loan Submitted. ID: $loanId"

Write-Host "4. Login as MARKETING..."
$marketingToken = Login "marketing" "password123"
$marketingHeaders = Get-Headers $marketingToken

Write-Host "5. Review Loan..."
$reviewBody = @{
    remarks = "Review OK"
} | ConvertTo-Json
$reviewed = Invoke-RestMethod -Method Patch -Uri "$base/api/loans/$loanId/review" -Headers $marketingHeaders -Body $reviewBody
Write-Host "   Loan Reviewed. Status: $($reviewed.data.currentStatus)"

Write-Host "6. Login as BRANCH_MANAGER..."
$bmToken = Login "branch_manager" "password123"
$bmHeaders = Get-Headers $bmToken

Write-Host "7. Approve Loan..."
$approveBody = @{
    remarks = "Approve OK"
} | ConvertTo-Json
$approved = Invoke-RestMethod -Method Patch -Uri "$base/api/loans/$loanId/approve" -Headers $bmHeaders -Body $approveBody
Write-Host "   Loan Approved. Status: $($approved.data.currentStatus)"

Write-Host "8. Login as BACK_OFFICE..."
$boToken = Login "back_office" "password123"
$boHeaders = Get-Headers $boToken

Write-Host "9. Disburse Loan..."
$disburseBody = @{
    remarks = "Disburse OK"
} | ConvertTo-Json
$disbursed = Invoke-RestMethod -Method Patch -Uri "$base/api/loans/$loanId/disburse" -Headers $boHeaders -Body $disburseBody
Write-Host "   Loan Disbursed. Status: $($disbursed.data.currentStatus)"

Write-Host "10. Negative Test: Review as NASABAH (Should fail with 403)..."
try {
    Invoke-RestMethod -Method Patch -Uri "$base/api/loans/$loanId/review" -Headers $nasabahHeaders -Body $reviewBody
    Write-Error "Test Failed: Should have received 403 Forbidden"
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    if ($statusCode -eq 403) {
        Write-Host "   Success: Received 403 Forbidden as expected."
    } else {
        Write-Error "Test Failed: Received $statusCode instead of 403"
    }
}

Write-Host "E2E Test Completed Successfully."
