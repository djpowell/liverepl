package net.djpowell.liverepl.server;

import clojure.lang.RT;
import clojure.lang.IFn;
import clojure.lang.Symbol;
import clojure.lang.Keyword;
import clojure.lang.Var;

import java.net.InetAddress;
import java.util.Map;
import java.util.HashMap;

public class Repl {

    private final static IFn REQUIRE = RT.var("clojure.core", "require");
    private final static Symbol SERVER = Symbol.intern("clojure.tools.nrepl.server");

    private final static IFn START_SERVER = RT.var("clojure.tools.nrepl.server", "start-server");
    private final static IFn STOP_SERVER = RT.var("clojure.tools.nrepl.server", "stop-server");

    private final static Object PORT = Keyword.intern("port");
    private final static Object BIND = Keyword.intern("bind");
    
	public static void main(InetAddress host, int port) throws Exception {
	    REQUIRE.invoke(SERVER);
	    Map m = new HashMap((Map)START_SERVER.invoke(PORT, port,
							 BIND, host.getHostAddress()));
	    // TODO store this server object somewhere so that we can
	    // stop it from a custom-middleware
	    System.out.println("RET: " + m);
	}
	
}
