$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$toolRoot = Join-Path $projectRoot '.tools\minio'
$minioExe = Join-Path $toolRoot 'bin\minio.exe'
New-Item -ItemType Directory -Path $toolRoot -Force | Out-Null
if (Test-Path -LiteralPath $minioExe) { Write-Output 'Local MinIO is already installed.'; return }
$goExe = Join-Path $toolRoot 'go\bin\go.exe'
if (-not (Test-Path -LiteralPath $goExe)) {
    $releases = Invoke-RestMethod -Uri 'https://go.dev/dl/?mode=json'
    $archive = $releases[0].files | Where-Object { $_.os -eq 'windows' -and $_.arch -eq 'amd64' -and $_.kind -eq 'archive' } | Select-Object -First 1
    if (-not $archive) { throw 'Cannot find the official Windows Go archive.' }
    $archivePath = Join-Path $toolRoot $archive.filename
    $verified = (Test-Path -LiteralPath $archivePath) -and ((Get-FileHash -LiteralPath $archivePath -Algorithm SHA256).Hash -ieq $archive.sha256)
    if (-not $verified) {
        Write-Output 'Downloading the official Go toolchain for a local MinIO source build...'
        & curl.exe --fail --location --silent --show-error --retry 3 --max-time 300 --continue-at - --output $archivePath ('https://go.dev/dl/' + $archive.filename)
        if ($LASTEXITCODE -ne 0) { throw 'Go download failed. Run the installer again to resume.' }
        if ((Get-FileHash -LiteralPath $archivePath -Algorithm SHA256).Hash -ine $archive.sha256) { throw 'Go archive checksum mismatch.' }
    }
    Expand-Archive -LiteralPath $archivePath -DestinationPath $toolRoot -Force
}
$taskGoPath = Join-Path $toolRoot 'gopath'
$taskGoBin = Join-Path $toolRoot 'bin'
$taskGoCache = Join-Path $toolRoot 'go-cache'
$savedEnvironment = @{}
foreach ($envName in @('GOPATH', 'GOBIN', 'GOCACHE', 'CGO_ENABLED', 'GODEBUG', 'PATH')) { $savedEnvironment[$envName] = [Environment]::GetEnvironmentVariable($envName, 'Process') }
try {
    $env:GOPATH = $taskGoPath; $env:GOBIN = $taskGoBin; $env:GOCACHE = $taskGoCache; $env:CGO_ENABLED = '0'
    if (-not $env:GODEBUG) { $env:GODEBUG = 'http2client=0' }
    $env:PATH = (Join-Path $toolRoot 'go\bin') + ';' + $env:PATH
    Write-Output 'Building MinIO from its official community source. This may take several minutes.'
    $ErrorActionPreference = 'Continue'
    & $goExe install github.com/minio/minio@latest
    if ($LASTEXITCODE -ne 0) { throw 'MinIO source build failed.' }
} finally {
    foreach ($envName in $savedEnvironment.Keys) { [Environment]::SetEnvironmentVariable($envName, $savedEnvironment[$envName], 'Process') }
}
Write-Output 'MinIO installed locally in .tools/minio/bin.'
