@echo off
setlocal

:: Directories to remove and copy
set MAIN_RESOURCES_DIR=src\main\resources\generated-templates-assets
set TEST_RESOURCES_DIR=src\test\resources\generated-templates-assets

:: Remove the directories if they exist
if exist "%MAIN_RESOURCES_DIR%" (
    echo Removing %MAIN_RESOURCES_DIR%...
    rmdir /s /q "%MAIN_RESOURCES_DIR%"
)

if exist "%TEST_RESOURCES_DIR%" (
    echo Removing %TEST_RESOURCES_DIR%...
    rmdir /s /q "%TEST_RESOURCES_DIR%"
)

:: Run the template builder script
echo Running the template generation process...
cd scripts\templates-builder
call npm ci
call npm run generate
if errorlevel 1 (
    echo Failed to generate templates. Exiting.
    exit /b 1
)
cd ..\..

:: Copy generated templates to test resources
if exist "%MAIN_RESOURCES_DIR%" (
    echo Copying %MAIN_RESOURCES_DIR% to %TEST_RESOURCES_DIR%...
    mkdir "%TEST_RESOURCES_DIR%"
    xcopy /e /i "%MAIN_RESOURCES_DIR%" "%TEST_RESOURCES_DIR%"
) else (
    echo Directory %MAIN_RESOURCES_DIR% does not exist. Nothing to copy.
    exit /b 1
)

echo Process completed successfully!
exit /b 0