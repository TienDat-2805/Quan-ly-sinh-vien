param([switch]$Build)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$jarPath = Join-Path $projectRoot 'target\educaze-0.0.1-SNAPSHOT.jar'
if ($Build -or -not (Test-Path -LiteralPath $jarPath)) {
    & (Join-Path $PSScriptRoot 'build.ps1')
    if ($LASTEXITCODE -ne 0) { throw 'Build failed.' }
}
$javaExe = if ($env:JAVA_HOME -and (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin\java.exe'))) { Join-Path $env:JAVA_HOME 'bin\java.exe' } else { 'java.exe' }
$port = if ($env:SERVER_PORT) { $env:SERVER_PORT } else { '8080' }
Write-Output "Start MySQL in XAMPP first. Educare: http://127.0.0.1:$port/test/"
Write-Output 'Press Ctrl+C to stop the application.'
Push-Location -LiteralPath $projectRoot
try {
    $ErrorActionPreference = 'Continue'
    & $javaExe -jar $jarPath
    if ($LASTEXITCODE -ne 0) { throw "Application exited with code $LASTEXITCODE." }
} finally { Pop-Location }
