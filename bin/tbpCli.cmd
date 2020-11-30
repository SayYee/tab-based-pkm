setlocal

call "%~dp0tbpEnv.cmd"

set TBPMAIN=com.sayyi.software.tbp.cli.CliCommand
call %JAVA% -cp "%CLASSPATH%" %TBPMAIN% "%TBPCFG%" %*

endlocal