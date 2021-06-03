@echo off
@setlocal
set APPLICATION_JAR=%~dp0..\web\tbp-web.jar
set APPLICATION_CONF=%~dp0..\conf\application.yml

java -jar %APPLICATION_JAR% --spring.config.location=file:%APPLICATION_CONF%
@endlocal