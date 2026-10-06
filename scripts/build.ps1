param([switch]$SkipTests)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
function Invoke-CheckedNative {
    param([string]$Executable, [string[]]$Arguments)
    $nativePreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try { & $Executable @Arguments; $nativeExit = $LASTEXITCODE }
    finally { $ErrorActionPreference = $nativePreference }
    if ($nativeExit -ne 0) { throw "$Executable failed with exit code $nativeExit." }
}
function Set-ProjectJava {
    if ($env:JAVA_HOME -and (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin\javac.exe'))) { return }
    $compiler = Get-Command javac.exe -ErrorAction SilentlyContinue
    if ($compiler) {
        $javaCandidates = @('C:\Program Files\Java', 'C:\Program Files\Eclipse Adoptium')
        foreach ($javaCandidate in $javaCandidates) {
            if (Test-Path -LiteralPath $javaCandidate) {
                $jdk = Get-ChildItem -LiteralPath $javaCandidate -Directory | Sort-Object Name -Descending | Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName 'bin\javac.exe') } | Select-Object -First 1
                if ($jdk) { $env:JAVA_HOME = $jdk.FullName; return }
            }
        }
        $env:JAVA_HOME = Split-Path -Parent (Split-Path -Parent $compiler.Source)
    }
    if (-not $env:JAVA_HOME) { throw 'Install JDK 21 or newer and set JAVA_HOME.' }
}
Set-ProjectJava
Push-Location -LiteralPath (Join-Path $projectRoot 'frontend')
try {
    Invoke-CheckedNative -Executable 'npm.cmd' -Arguments @('ci', '--no-audit', '--no-fund')
    Invoke-CheckedNative -Executable 'npm.cmd' -Arguments @('run', 'build')
} finally { Pop-Location }
Push-Location -LiteralPath $projectRoot
try {
    $mavenArgs = @('-B', 'clean', 'package')
    if ($SkipTests) { $mavenArgs += '-DskipTests' }
    Invoke-CheckedNative -Executable (Join-Path $projectRoot 'mvnw.cmd') -Arguments $mavenArgs
} finally { Pop-Location }
Write-Output 'Build complete. Run start.cmd to launch Educare.'
