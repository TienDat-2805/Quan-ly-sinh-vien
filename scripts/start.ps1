param([switch]$Build, [switch]$OpenBrowser)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$jarPath = Join-Path $projectRoot 'target\educaze-0.0.1-SNAPSHOT.jar'
$port = if ($env:SERVER_PORT) { $env:SERVER_PORT } else { '8080' }
$applicationUrl = "http://127.0.0.1:$port/test/"
if ($OpenBrowser -and -not $Build) {
    try {
        $existingPage = Invoke-WebRequest -Uri $applicationUrl -UseBasicParsing -TimeoutSec 2
        if ($existingPage.StatusCode -eq 200 -and $existingPage.Content -match '<title>Educare\s*\|') {
            Start-Process -FilePath $applicationUrl
            return
        }
    } catch {
        # Start the application when it is not already available.
    }
}
if ($Build -or -not (Test-Path -LiteralPath $jarPath)) {
    & (Join-Path $PSScriptRoot 'build.ps1')
    if ($LASTEXITCODE -ne 0) { throw 'Build failed.' }
}
$javaExe = if ($env:JAVA_HOME -and (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin\java.exe'))) { Join-Path $env:JAVA_HOME 'bin\java.exe' } else { 'java.exe' }
Write-Output "Start MySQL in XAMPP first. Educare: $applicationUrl"
Write-Output 'Press Ctrl+C to stop the application.'
$browserJob = $null
if ($OpenBrowser) {
    $browserJob = Start-Job -ArgumentList $applicationUrl -ScriptBlock {
        param([string]$url)
        $deadline = [DateTime]::UtcNow.AddMinutes(3)
        while ([DateTime]::UtcNow -lt $deadline) {
            $ready = $false
            try {
                $page = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 2
                $ready = $page.StatusCode -eq 200 -and $page.Content -match '<title>Educare\s*\|'
            } catch { }
            if ($ready) {
                Start-Process -FilePath $url
                return
            }
            Start-Sleep -Seconds 1
        }
        Write-Warning "The browser was not opened automatically. Visit $url when the server is ready."
    }
}
Push-Location -LiteralPath $projectRoot
try {
    $ErrorActionPreference = 'Continue'
    & $javaExe -jar $jarPath
    if ($LASTEXITCODE -ne 0) { throw "Application exited with code $LASTEXITCODE." }
} finally {
    Pop-Location
    if ($browserJob) {
        Stop-Job -Job $browserJob -ErrorAction SilentlyContinue
        Receive-Job -Job $browserJob -ErrorAction SilentlyContinue
        Remove-Job -Job $browserJob -Force -ErrorAction SilentlyContinue
    }
}
