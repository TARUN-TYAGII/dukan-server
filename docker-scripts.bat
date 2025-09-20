@echo off
REM Docker management scripts for Bookshop application

if "%1"=="build" goto build
if "%1"=="up" goto up
if "%1"=="down" goto down
if "%1"=="logs" goto logs
if "%1"=="clean" goto clean
if "%1"=="prod-up" goto prod-up
if "%1"=="prod-down" goto prod-down
goto help

:build
echo Building Docker image...
docker build -t bookshop-server .
goto end

:up
echo Starting development environment...
docker-compose up -d
goto end

:down
echo Stopping development environment...
docker-compose down
goto end

:logs
echo Showing application logs...
docker-compose logs -f app
goto end

:clean
echo Cleaning up Docker resources...
docker-compose down -v
docker system prune -f
goto end

:prod-up
echo Starting production environment...
if not exist secrets mkdir secrets
if not exist secrets\mysql_root_password.txt echo Shivvvva@1 > secrets\mysql_root_password.txt
if not exist secrets\mysql_password.txt echo bookshop_pass > secrets\mysql_password.txt
docker-compose -f docker-compose.prod.yml up -d
goto end

:prod-down
echo Stopping production environment...
docker-compose -f docker-compose.prod.yml down
goto end

:help
echo Available commands:
echo   build      - Build the Docker image
echo   up         - Start development environment
echo   down       - Stop development environment
echo   logs       - Show application logs
echo   clean      - Clean up Docker resources
echo   prod-up    - Start production environment
echo   prod-down  - Stop production environment
echo.
echo Usage: docker-scripts.bat [command]
goto end

:end
