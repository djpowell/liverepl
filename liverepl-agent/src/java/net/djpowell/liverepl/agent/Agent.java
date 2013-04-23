package net.djpowell.liverepl.agent;

import net.djpowell.liverepl.discovery.ClassLoaderInfo;
import net.djpowell.liverepl.discovery.Discovery;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class Agent {

    // bind to localhost to keep things more secure
    public static final InetAddress LOCALHOST;
    static {
        try {
            LOCALHOST = InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Discovery discovery = new Discovery();

    public static interface ConnectNotifyingTask {
        void run(ServerSocket server, AtomicBoolean connected);
    }

    private static Thread startKillerThread(final int connectTimeout, final AtomicBoolean connected, final ServerSocket server) {
        Thread killer = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(connectTimeout);
                } catch (InterruptedException e) {
                    // ignore
                }
                if (connected.get()) {
                    // TRC.fine("Client connect timeout: terminating server");
                    try {
                        server.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, "Killer Thread");
        killer.start();
        return killer;
    }

    public static void runAfterConnect(int port, int connectTimeout, String threadName, final ConnectNotifyingTask task) throws Exception {
        final ServerSocket serverSocket = new ServerSocket(port, 0, LOCALHOST);
        final AtomicBoolean connected = new AtomicBoolean(false);
        Thread taskThread = new Thread(new Runnable() {
            public void run() {
                task.run(serverSocket, connected);
            }
        }, threadName);
        startKillerThread(connectTimeout, connected, serverSocket);
        taskThread.start();
    }

    private static void printClassLoaderInfo(int port) {
        try {
            runAfterConnect(port, 5000, "ClassLoaderInfoThread", new ConnectNotifyingTask() {
                public void run(ServerSocket server, AtomicBoolean connected) {
                    try {
                        Socket socket = server.accept();
                        connected.set(true);
                        try {
                            PrintStream out = new PrintStream(socket.getOutputStream());
                            try {
                                discovery.dumpList(out);
                            } finally {
                                out.close();
                            }
                        } finally {
                            socket.close();
                        }
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ClassLoader pushClassLoader(List<URL> urls, String clId)  {
        TRC.fine("Creating new classloader with: " + urls);
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        TRC.fine("Old classloader: " + old);
        ClassLoaderInfo cli = discovery.findClassLoader(clId);
        if (cli == null) {
            throw new RuntimeException("Unknown class loader: " + clId);
        }
        ClassLoader cl = cli.getClassLoader();
        URLClassLoader withClojure = new URLClassLoader(urls.toArray(new URL[urls.size()]), cl); // TODO
        Thread.currentThread().setContextClassLoader(withClojure);
        return old;
    }
    
    private static void popClassLoader(ClassLoader old) {
        TRC.fine("Restoring old context classloader");
        Thread.currentThread().setContextClassLoader(old);
    }

    private static boolean isClojureLoaded() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            cl.loadClass("clojure.lang.RT");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    public static void agentmain(String agentArgs, Instrumentation inst) {
        TRC.fine("Started Attach agent");

        StringTokenizer stok = new StringTokenizer(agentArgs, "\n");
        if (stok.countTokens() != 4) {
            throw new RuntimeException("Invalid parameters: " + agentArgs);
        }
        
        int port = Integer.parseInt(stok.nextToken());
        TRC.fine("Port: " + port);
        String clojurePath = stok.nextToken();
        String serverPath = stok.nextToken();
        String classLoaderId = stok.nextToken();

        if ("L".equals(classLoaderId)) {
            printClassLoaderInfo(port);
            return;
        }

        boolean clojureLoaded = isClojureLoaded();
        TRC.fine("Clojure is " + (clojureLoaded ? "" : "not ") + "loaded");

        List<URL> urls;
        if (clojureLoaded) {
            urls = getJarUrls(serverPath);
        } else {
            urls = getJarUrls(clojurePath, serverPath);
        }

        ClassLoader old = pushClassLoader(urls, classLoaderId);
        try {
            if (!clojureLoaded) { // if clojure wasn't loaded before, print current status
                TRC.fine("Clojure is " + (isClojureLoaded() ? "" : "not ") + "loaded");
            }
            startRepl(port);
        } finally {
            popClassLoader(old);
        }
    }

    private static List<URL> getJarUrls(String... paths) {
        List<URL> urls = new ArrayList<URL>();
        try {
            for (String path : paths) {
                URL url = new File(path).toURI().toURL();
                urls.add(url);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return urls;
    }

    private static void startRepl(int port) {
        // avoids making load-time references to Clojure classes from the system classloader
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?> repl = Class.forName("net.djpowell.liverepl.server.Repl", true, cl);
            Method method = repl.getMethod("main", InetAddress.class, Integer.TYPE);
            method.invoke(null, LOCALHOST, port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Logger TRC = Logger.getLogger(Agent.class.getName()); 

}
