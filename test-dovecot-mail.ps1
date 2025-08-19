# Test Dovecot Mail Retrieval
Write-Host "🧪 Testing Dovecot Mail Retrieval..." -ForegroundColor Green
Write-Host ""

$BASE_URL = "http://localhost:8080/mail-project/api/mail"

try {
    # 1. Test IMAP Connection
    Write-Host "1️⃣ Testing IMAP Connection..." -ForegroundColor Yellow
    $imapResponse = Invoke-WebRequest -Uri "$BASE_URL/test-imap" -Method GET
    Write-Host "✅ IMAP Connection: $($imapResponse.Content)" -ForegroundColor Green
    Write-Host ""

    # 2. Test Fetch Mails
    Write-Host "2️⃣ Testing Mail Fetching..." -ForegroundColor Yellow
    $fetchResponse = Invoke-WebRequest -Uri "$BASE_URL/fetch-now" -Method POST
    Write-Host "✅ Mail Fetching: $($fetchResponse.Content)" -ForegroundColor Green
    Write-Host ""

    # 3. Test Health Check
    Write-Host "3️⃣ Testing Health Check..." -ForegroundColor Yellow
    $healthResponse = Invoke-WebRequest -Uri "$BASE_URL/health" -Method GET
    Write-Host "✅ Health Check: $($healthResponse.Content)" -ForegroundColor Green
    Write-Host ""

    # 4. Test Get Received Mails (might fail due to auth)
    Write-Host "4️⃣ Testing Get Received Mails..." -ForegroundColor Yellow
    try {
        $receivedResponse = Invoke-WebRequest -Uri "$BASE_URL/recived" -Method GET
        Write-Host "✅ Received Mails: $($receivedResponse.Content)" -ForegroundColor Green
    } catch {
        Write-Host "⚠️ Received Mails: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""

    # 5. Test Get All Mails for specific user (might fail due to auth)
    Write-Host "5️⃣ Testing Get All Mails for honhuan2002@hvn.com..." -ForegroundColor Yellow
    try {
        $allMailsResponse = Invoke-WebRequest -Uri "$BASE_URL/getAllMail?toEmail=honhuan2002@hvn.com&page=0&size=10" -Method GET
        Write-Host "✅ All Mails: $($allMailsResponse.Content)" -ForegroundColor Green
    } catch {
        Write-Host "⚠️ All Mails: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""

    Write-Host "🎉 Core tests completed successfully!" -ForegroundColor Green

} catch {
    Write-Host "❌ Test failed: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response.StatusCode -eq 403) {
        Write-Host "🔒 403 Forbidden - This might be due to authentication requirements" -ForegroundColor Yellow
    }
    
    if ($_.Exception.Message -like "*Connection refused*") {
        Write-Host "🔌 Connection refused - Make sure the Spring Boot application is running on port 8080" -ForegroundColor Yellow
    }
}
