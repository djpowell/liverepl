#!/bin/sh
# Starter script for Clojure liverepl

[ -z "$JDK_HOME" ] && JDK_HOME=/usr/lib/jvm/default-java
LIVEREPL_HOME="$(cd -P -- "$(dirname -- "$0")" && pwd -P)"
CLOJURE_JAR="$LIVEREPL_HOME/clojure.jar"

if [ ! -f "$JDK_HOME/lib/tools.jar" ]; then
   echo 'Unable to find $JDK_HOME/lib/tools.jar'
   echo "Please set the JDK_HOME environment variable to the location of your JDK."
   exit 1
fi

if which rlwrap >/dev/null; then
    echo "Found rlwrap\n"
    breakchars="(){}[],^%$#@\"\";:''|\\"
     exec rlwrap --remember -c -b "$breakchars" \
		java -cp "$LIVEREPL_HOME/liverepl-agent.jar:$JDK_HOME/lib/tools.jar" net.djpowell.liverepl.client.Main "$CLOJURE_JAR" "$LIVEREPL_HOME/liverepl-agent.jar" "$LIVEREPL_HOME/liverepl-server.jar" "$@"
else
	echo "rlwrap missing\n"
	java -cp "$LIVEREPL_HOME/liverepl-agent.jar:$JDK_HOME/lib/tools.jar" net.djpowell.liverepl.client.Main "$CLOJURE_JAR" "$LIVEREPL_HOME/liverepl-agent.jar" "$LIVEREPL_HOME/liverepl-server.jar" "$@"

fi



