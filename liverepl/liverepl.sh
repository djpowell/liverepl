#!/bin/sh
# Starter script for Clojure liverepl

[ -z "$JDK_HOME" ] && JDK_HOME=/usr/lib/jvm/default-java
LIVEREPL_HOME="$(cd -P -- "$(dirname -- "$0")" && pwd -P)"
CLOJURE_JAR="$LIVEREPL_HOME/lib/clojure-1.3.0.jar"

if [ ! -f "$JDK_HOME/lib/tools.jar" ]; then
   echo 'Unable to find $JDK_HOME/lib/tools.jar'
   echo "Please set the JDK_HOME environment variable to the location of your JDK."
   exit 1
fi

java -cp "$LIVEREPL_HOME/lib/liverepl-client-1.1.0.jar:$JDK_HOME/lib/tools.jar" net.djpowell.liverepl.client.Main "$CLOJURE_JAR" "$LIVEREPL_HOME/lib/liverepl-agent-1.1.0.jar" "$LIVEREPL_HOME/lib/liverepl-server-1.1.0.jar" "$@"

