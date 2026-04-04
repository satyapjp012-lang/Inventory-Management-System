# Build and Run Script - Compiles to 'bin' folder, keeps Src clean

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$srcDir = Join-Path $projectRoot "Src"
$binDir = Join-Path $projectRoot "bin"
$mainClass = "inventory.management.system.LoginFrame"

# Create bin directory if it doesn't exist
if (-not (Test-Path $binDir)) {
    New-Item -ItemType Directory -Path $binDir | Out-Null
}

# Compile to bin directory
Write-Host "Compiling Java files..." -ForegroundColor Cyan
javac -d $binDir (Get-ChildItem -Path $srcDir -Recurse -Include "*.java" | ForEach-Object { $_.FullName })

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Compilation successful!" -ForegroundColor Green
    Write-Host "Running application..." -ForegroundColor Cyan
    
    # Run from bin directory
    Push-Location $binDir
    java $mainClass
    Pop-Location
} else {
    Write-Host "✗ Compilation failed!" -ForegroundColor Red
}
