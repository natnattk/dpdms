$body2 = @{ username = "droughtrecorder1"; password = "password123"; role = "WARD_RECORDER"; hazard = "DROUGHT"; ward = "Kandeya"; district = "Rushinga" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8081/auth/register" -Method Post -Body $body2 -ContentType "application/json"

$loginBody2 = @{ username = "droughtrecorder1"; password = "password123" } | ConvertTo-Json
$droughtToken = (Invoke-RestMethod -Uri "http://localhost:8081/auth/login" -Method Post -Body $loginBody2 -ContentType "application/json").token

$incidentBody = @{
province = "Mashonaland Central"
occurredAt = "2026-09-25T10:00:00"
severity = "HIGH"
latitude = -16.75
longitude = 31.85
peakWaterLevelMetres = 3.2
riverBasin = "Mazowe"
householdsDisplaced = 45
areaFloodedHectares = 12.5
durationDays = 4
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/flood-incidents" -Method Post -Body $incidentBody -ContentType "application/json" -Headers @{ Authorization = "Bearer $droughtToken" }
