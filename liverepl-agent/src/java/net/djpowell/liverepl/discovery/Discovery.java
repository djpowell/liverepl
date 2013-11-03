package net.djpowell.liverepl.discovery;

import net.djpowell.liverepl.discovery.impl.SystemDiscovery;
import net.djpowell.liverepl.discovery.impl.ThreadDiscovery;
import net.djpowell.liverepl.discovery.impl.TomcatDiscovery;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.StringWriter;

/**
 * API for obtaining a list of ClassLoaders by using available implementations of ClassLoaderDiscovery
 */
public class Discovery implements ClassLoaderDiscovery {

    private final List<ClassLoaderDiscovery> impls = new ArrayList<ClassLoaderDiscovery>();

    public Discovery() {
        ClassLoaderRegistry registry=new ClassLoaderRegistry();
        impls.add(new SystemDiscovery(registry));
        impls.add(new TomcatDiscovery(registry));
        impls.add(new ThreadDiscovery(registry));
    }

    public Collection<ClassLoaderInfo> listClassLoaders() {
        List<ClassLoaderInfo> ret = new ArrayList<ClassLoaderInfo>();
        for (ClassLoaderDiscovery discovery : impls) {
            ret.addAll(discovery.listClassLoaders());
        }
        return ret;
    }

    public String discoveryName() {
        return "All ClassLoaders";
    }

    private static final String NEWLINE = System.getProperty("line.separator");

    public void dumpList(BufferedWriter out) throws IOException {
        for (ClassLoaderDiscovery discovery : impls) {
            Collection<ClassLoaderInfo> clis = discovery.listClassLoaders();
            if (!clis.isEmpty()) {
                out.write(NEWLINE);
                out.write(discovery.discoveryName() + ":");
                out.write(NEWLINE);
                out.write(NEWLINE);
                out.write(ClassLoaderInfo.header);
                out.write(NEWLINE);
                for (ClassLoaderInfo cli : clis) {
                    out.write(cli.toString());
		    out.write(NEWLINE);
                }
            }
        }
    }

    public String dumpList() {
	StringWriter sw = new StringWriter();
	try {
	    BufferedWriter out = new BufferedWriter(sw);
	    try {
		dumpList(out);
	    } finally {
		out.close();
	    }
	} catch (IOException e) {
	    // do nothing
	}
	return sw.toString();
    }

    public ClassLoaderInfo findClassLoader(String clId) {
        for (ClassLoaderInfo cli : listClassLoaders()) {
            if (cli.id.equals(clId)) {
                return cli;
            }
        }
        return null;
    }

}
