@echo off
rem Starter script for Clojure liverepl

if "%JDK_HOME%" == "" set JDK_HOME=c:\jdk
set LIVEREPL_HOME=%~dp0
set CLOJURE_JAR=%LIVEREPL_HOME%clojure.jar

"%JDK_HOME%\bin\java" -cp "%LIVEREPL_HOME%liverepl-client.jar";"%JDK_HOME%\lib\tools.jar" net.djpowell.liverepl.client.Main "%CLOJURE_JAR%" "%LIVEREPL_HOME%liverepl-agent.jar" "%LIVEREPL_HOME%liverepl-server.jar" %*
