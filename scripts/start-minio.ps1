$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$toolRoot = Join-Path $projectRoot '.tools\minio'
$minioExe = Join-Path $toolRoot 'bin\minio.exe'
if (-not (Test-Path -LiteralPath $minioExe)) { & (Join-Path $PSScriptRoot 'install-minio.ps1') }
$credentialsPath = Join-Path $toolRoot 'credentials.json'
if (-not (Test-Path -LiteralPath $credentialsPath)) {
    $randomBytes = New-Object byte[] 32
    $randomSource = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    try { $randomSource.GetBytes($randomBytes) } finally { $randomSource.Dispose() }
    $secretKey = [Convert]::ToBase64String($randomBytes)
    $credentials = @{ accessKey = ('educare-' + [Guid]::NewGuid().ToString('N').Substring(0, 12)); secretKey = $secretKey; endpoint = 'http://127.0.0.1:9000'; bucket = 'educare-documents' }
    [System.IO.File]::WriteAllText($credentialsPath, ($credentials | ConvertTo-Json), [System.Text.UTF8Encoding]::new($false))
}
$credentials = Get-Content -LiteralPath $credentialsPath -Raw | ConvertFrom-Json
$env:MINIO_ROOT_USER = $credentials.accessKey
$env:MINIO_ROOT_PASSWORD = $credentials.secretKey
$dataPath = Join-Path $toolRoot 'data'
New-Item -ItemType Directory -Path $dataPath -Force | Out-Null
Write-Output 'Local MinIO API: http://127.0.0.1:9000; Console: http://127.0.0.1:9001'
Write-Output 'Keep this terminal open. Press Ctrl+C to stop MinIO.'
$ErrorActionPreference = 'Continue'
& $minioExe server $dataPath --address '127.0.0.1:9000' --console-address '127.0.0.1:9001'
if ($LASTEXITCODE -ne 0) { throw "MinIO exited with code $LASTEXITCODE." }
