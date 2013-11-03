package net.djpowell.liverepl.server;

import clojure.lang.RT;
import clojure.lang.IFn;
import clojure.lang.Symbol;
import clojure.lang.Keyword;
import clojure.lang.Var;

import java.net.InetAddress;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class Repl {

    private final static IFn REQUIRE = RT.var("clojure.core", "require");
    private final static Symbol SERVER = Symbol.intern("net.djpowell.liverepl.server.repl");
    static {
	REQUIRE.invoke(SERVER);
    }
    
    private final static IFn START_SERVER = RT.var("net.djpowell.liverepl.server.repl", "start-server");
    
    public static void main(InetAddress host, int port, Callable<String> doDiscover) throws Exception {
	Map m = (Map)START_SERVER.invoke(host.getHostAddress(),
					 port,
					 doDiscover);
	// TODO store this server object somewhere so that we can
	// stop it from a custom-middleware
	System.out.println("RET: " + m);
    }
	
}
