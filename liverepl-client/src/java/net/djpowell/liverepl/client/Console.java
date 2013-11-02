package net.djpowell.liverepl.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

import clojure.lang.RT;
import clojure.lang.IFn;
import clojure.lang.Symbol;
import clojure.lang.Keyword;
import clojure.lang.Var;

public class Console {

    private final static IFn REQUIRE = RT.var("clojure.core", "require");
    private final static IFn ARRAYMAP = RT.var("clojure.core", "array-map");
    private final static Symbol REPLY = Symbol.intern("reply.main");

    private final static IFn LAUNCH = RT.var("reply.main", "launch-nrepl");

    private final static Object ATTACH = Keyword.intern("attach");
    private final static Object INPUT = Keyword.intern("input-stream");
    private final static Object OUTPUT = Keyword.intern("output-stream");

    
    public static void main(InetAddress host, int port, boolean readInput) throws Exception {
	REQUIRE.invoke(REPLY);
	Object args = ARRAYMAP.invoke(ATTACH, host.getHostAddress() + ":" + port,
				      INPUT, System.in,
				      OUTPUT, System.out);
	// TODO add a custom middleware to handle stopping the server
	System.out.println("Launching: " + args);
	LAUNCH.invoke(args);
	System.out.println("Launched");
    }
    
    private static final Logger TRC = Logger.getLogger(Console.class.getName()); 

}
