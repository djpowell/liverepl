package net.djpowell.liverepl.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

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
    
    private static void listPids() {
        System.out.println();
        System.out.println("liverepl");
        System.out.println("Usage: liverepl <pid>");
        System.out.println();
        System.out.println("List of Java processes");
        System.out.format("%1$-6s %2$.60s%n", "pid", "Details");
        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            System.out.format("%1$-6s %2$.60s%n", vmd.id(), vmd.displayName());
        }
    }
    
    public static void main(String[] args) throws Exception {
        // Usage: <clojurepath> <agentjarpath> <serverjarpath> <jarsdir> <jvmpid> <classloaderid>
        if (args.length < 5) {
            listPids();
            System.exit(0);
        }
        String clojurepath = args[0];
        String agentpath = args[1];
        String serverpath = args[2];
        String jarsdir = args[3];
        String pid = args[4];
        String classLoaderId;
        if (args.length < 6) {
            classLoaderId = "L";
            System.out.println();
            System.out.println("List of ClassLoaders for process #" + pid);
        } else {
            classLoaderId = args[5];
        }

        TRC.fine("Attaching to pid: " + pid);
        final VirtualMachine vm = VirtualMachine.attach(pid);
        
        int port = getFreePort(LOCALHOST);
        // start the agent, which will create the server socket, then return
        String agentArgs = String.valueOf(port) + "\n" + clojurepath + "\n" + serverpath + "\n" + jarsdir + "\n" + classLoaderId;
        vm.loadAgent(agentpath, agentArgs);

        boolean listClassLoaders = "L".equals(classLoaderId);
        // start the code that will connect to the server socket
        Console.main(LOCALHOST, port, !listClassLoaders);

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
