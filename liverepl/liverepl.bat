@echo off
rem Starter script for Clojure liverepl

if "%JDK_HOME%" == "" set JDK_HOME=c:\jdk
set LIVEREPL_HOME=%~dp0
set CLOJURE_JAR=%LIVEREPL_HOME%lib\clojure-1.3.0.jar

"%JDK_HOME%\bin\java" -cp "%LIVEREPL_HOME%lib\liverepl-client-1.1.0.jar";"%JDK_HOME%\lib\tools.jar" net.djpowell.liverepl.client.Main "%CLOJURE_JAR%" "%LIVEREPL_HOME%lib\liverepl-agent-1.1.0.jar" "%LIVEREPL_HOME%lib\liverepl-server-1.1.0.jar" %*
