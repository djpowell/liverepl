package net.djpowell.liverepl.server;

import java.net.InetAddress;

import clojure.lang.Namespace;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public class Repl {

    private final static Symbol REPL_NS = Symbol.create("net.djpowell.liverepl.server.repl");
    private final static Namespace NS = Namespace.findOrCreate(REPL_NS);
    private final static Var REQUIRE = Var.intern(RT.CLOJURE_NS, Symbol.create("require"));
    private final static Var REPL = Var.intern(NS, Symbol.create("repl"));
    
	public static void main(InetAddress host, int port) throws Exception {
		// not really needed in clojure 1.1, as context class loaders are the default
		Var.pushThreadBindings(new PersistentArrayMap(new Object[] {RT.USE_CONTEXT_CLASSLOADER, Boolean.TRUE}));
		try {
			REQUIRE.invoke(REPL_NS);
			REPL.invoke(port, 0, host);
		} finally {
			Var.popThreadBindings();
		}
	}
	
}
