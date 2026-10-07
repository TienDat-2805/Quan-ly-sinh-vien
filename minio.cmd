@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\start-minio.ps1" %*
exit /b %errorlevel%
