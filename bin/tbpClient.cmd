@echo off

@setlocal

@REM 获取工作目录
set saveddir=%CD%

@REM 调用tbpEnv.cmd。%~dp0 是当前cmd文件的所在路径
call "%~dp0tbpEnv.cmd"

set LAUNCHER=com.sayyi.software.tbp.client.Main

"%JAVACMD%" ^
  -classpath %CLASSPATH% ^
  "-Dbasedir=%saveddir%" ^
  %LAUNCHER% %*

@endlocal
