package net.djpowell.liverepl.server;

import java.net.InetAddress;

import clojure.lang.Namespace;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public class Repl {

    final static public Symbol REPL_NS = Symbol.create("net.djpowell.liverepl.server.repl");
    final static public Namespace NS = Namespace.findOrCreate(REPL_NS);
    final static public Var REQUIRE = Var.intern(RT.CLOJURE_NS, Symbol.create("require"));
    final static public Var REPL = Var.intern(NS, Symbol.create("repl"));
    
	public static void main(InetAddress host, int port) throws Exception {
		// not really needed in clojure 1.1, as context class loaders are the default
		// required so that clojure can load the repl code 
		Var.pushThreadBindings(new PersistentArrayMap(new Object[] {RT.USE_CONTEXT_CLASSLOADER, Boolean.TRUE}));
		try {
			REQUIRE.invoke(REPL_NS);
			REPL.invoke(port, 0, host);
		} finally {
			Var.popThreadBindings();
		}
	}
	
}
