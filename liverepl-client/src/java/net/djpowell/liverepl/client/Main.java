package net.djpowell.liverepl.client;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import clojure.lang.RT;
import clojure.lang.Var;
import clojure.lang.Symbol;
import clojure.lang.IFn;

public class Main {

    // bind to localhost to keep things more secure
    public static final InetAddress LOCALHOST;
    static {
        try {
            LOCALHOST = InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static final IFn REQUIRE = RT.var("clojure.core", "require");
    private static final Symbol CLIENT_CORE = Symbol.intern("net.djpowell.liverepl.client.core");
    private static final IFn RUN_COMMAND = RT.var("net.djpowell.liverepl.client.core", "run-command");
    private static final IFn CONNECT_REPL = RT.var("net.djpowell.liverepl.client.core", "connect-repl");
    static {
	REQUIRE.invoke(CLIENT_CORE);
    }
    
    
    private static int getFreePort(InetAddress host) {
        // open a server socket on a random port, then close it
        // so that we can tell the server to listen on that port
        // and we will know which port to connect to
        try {
            ServerSocket server = new ServerSocket(0, 0, host);
            int port = server.getLocalPort();
            server.close();
            return port;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getLocalPid() {
        String vmid = ManagementFactory.getRuntimeMXBean().getName();
        int p = vmid.indexOf('@');
        if (p == -1) return null;
        return vmid.substring(0, p);
    }
    
    private static void listPids() {
        String localPid = getLocalPid();
        System.out.println("liverepl - Connect a clojure repl to any running Java or Clojure process");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  liverepl                        - list the pids of all available processes");
        System.out.println("  liverepl <pid>                  - list the class-loaders of the selected process");
        System.out.println("  liverepl <pid> 0                - connect a repl to the default class-loader");
        System.out.println("  liverepl <pid> <classloader-id> - connect a repl to a specific class-loader");
        System.out.println();
        System.out.println("List of Java processes:");
        System.out.println();
        System.out.format("%1$-6s %2$.60s%n", "pid", "Details");
        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            if (localPid != null && localPid.equals(vmd.id())) continue; // skip our own pid
            System.out.format("%1$-6s %2$.60s%n", vmd.id(), vmd.displayName());
        }
    }
    
    public static void main(String[] args) throws Exception {
        // Usage: <clojurepath> <agentjarpath> <serverjarpath> <jvmpid> <classloaderid>
        if (args.length < 4) {
            listPids();
            System.exit(0);
        }

	System.out.println("CARGS: " + java.util.Arrays.asList(args));

        String clojurepath = args[0];
        String agentpath = args[1];
        String serverpath = args[2];
        String pid = args[3];
        String classLoaderId;
        if (args.length < 5) {
            classLoaderId = "L";
            System.out.println("List of ClassLoaders for process #" + pid + ":");
        } else {
            classLoaderId = args[4];
        }

	System.out.println("CLID: " + classLoaderId);

        TRC.fine("Attaching to pid: " + pid);
        final VirtualMachine vm = VirtualMachine.attach(pid);
        
        int port = getFreePort(LOCALHOST);
        // start the agent, which will create the server socket, then return
        String agentArgs = String.valueOf(port) + "\n" + clojurepath + "\n" + serverpath + "\n" + classLoaderId;
        vm.loadAgent(agentpath, agentArgs);

        boolean listClassLoaders = "L".equals(classLoaderId);
	
	if (listClassLoaders) {
	    Object ret = RUN_COMMAND.invoke(LOCALHOST.getHostAddress(), port, "liverepl-list-classloaders");
	    System.out.println(ret);
	} else {
	    CONNECT_REPL.invoke(LOCALHOST.getHostAddress(), port);
	}

        // the server will shutdown when the client disconnects
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    vm.detach();
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    
    private static final Logger TRC = Logger.getLogger(Main.class.getName()); 

}
