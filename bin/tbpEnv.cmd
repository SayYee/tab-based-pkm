@echo off

set TBPCFGDIR=%~dp0%..\conf
set TBP_LOG_DIR=%~dp0%..\logs
set TBP_LOG4J_PROP=INFO,CONSOLE

set CLASSPATH=%TBPCFGDIR%

REM make it work in the release
SET CLASSPATH=%~dp0..\*;%~dp0..\lib\*;%CLASSPATH%

REM make it work for developers
SET CLASSPATH=%~dp0..\build\classes;%~dp0..\build\lib\*;%CLASSPATH%

set TBPCFG=%TBPCFGDIR%\tbp.cfg

@REM setup java environment variables

if not defined JAVA_HOME (
  echo Error: JAVA_HOME is not set.
  goto :eof
)

set JAVA_HOME=%JAVA_HOME:"=%

if not exist "%JAVA_HOME%"\bin\java.exe (
  echo Error: JAVA_HOME is incorrectly set: %JAVA_HOME%
  echo Expected to find java.exe here: %JAVA_HOME%\bin\java.exe
  goto :eof
)

REM strip off trailing \ from JAVA_HOME or java does not start
if "%JAVA_HOME:~-1%" EQU "\" set "JAVA_HOME=%JAVA_HOME:~0,-1%"
 
set JAVA="%JAVA_HOME%"\bin\java

