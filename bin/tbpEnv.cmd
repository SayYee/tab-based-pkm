@echo off

set TBPCFGDIR=%~dp0%..\conf
set TBP_LOG_DIR=%~dp0%..\logs
set TBP_LOG4J_PROP=INFO,CONSOLE

@REM 将 配置文件 设置为 类路径
set CLASSPATH=%TBPCFGDIR%

@REM 将lib下的文件加入类路径
SET CLASSPATH=%~dp0..\lib\*;%CLASSPATH%

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
 
set JAVACMD="%JAVA_HOME%"\bin\java

