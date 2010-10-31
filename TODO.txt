
* The ClassLoaderDiscovery stuff is a bit heavy-weight, and gets permanently loaded into the target
  process.  If the target process is long-running, and we upgrade liverepl, then the changes to
  liverepl won't get picked up as the old class will already be loaded.

  Make the agent very minimal, and load the discovery stuff in a separate ClassLoader so that it can
  be discarded.  The ClassLoaderRegistry will need to be kept in the apps ClassLoader though, to ensure
  that we can refer to the ClassLoader that the user has specified earlier.

* Provide a flag for unloading the ClassLoaderRegistry

* Factor out the server-killer stuff that is currently in Agent and repl.clj

* Support other repls

* Find a way of connecting to a target that is running as a system service on Windows.
