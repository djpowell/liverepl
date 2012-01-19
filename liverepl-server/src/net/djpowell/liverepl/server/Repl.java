package net.djpowell.liverepl.server;

import java.net.InetAddress;
import java.lang.instrument.Instrumentation;

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

    private final static Symbol AGENT_NS_NAME = Symbol.create("jvm.agent");
    private final static Namespace AGENT_NS = Namespace.findOrCreate(AGENT_NS_NAME);
    
    public static void main(InetAddress host, int port, Instrumentation inst)
	throws Exception {
	final Var instv = Var.intern(AGENT_NS, Symbol.create("instrumentation"));
	instv.bindRoot(inst);
	REQUIRE.invoke(REPL_NS);
	REPL.invoke(port, 0, host);
    }
}
