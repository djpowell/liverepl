LiveRepl
========

***
New download locations for binaries:
  http://djpowell.net/files/liverepl-1.0.zip
  http://djpowell.net/files/liverepl-1.1-alpha.zip
***


Start a Clojure REPL connected to any running Java or Clojure process
without needing the process to be setup in any special way beforehand.

Now supports connecting to Tomcat web applications.

You can use the repl to run code, inspect variables, and
redefine Clojure functions.


Clojure Live REPL - 2009-10-18
David Powell <djpowell@djpowell.net>
<http://github.com/djpowell/liverepl>

This software is distributed under the MIT licence.

----------------------------------------

Build
=====

To build:
   lein uberjar

----------------------------------------

Operation
=========

To see a list of running Java processes on the system, and their
process ids, enter:

   lein run

To see the available ClassLoaders for a specific process, enter:

   lein run <pid>

      -- where the pid is the process id for the process, obtained in
         the step above.

To connect a repl to the process, enter:

   lein run <pid> <classloader-id>

      -- where the pid is the process id for the process.
      -- and the classloader-id was obtained in the step above.

   if you aren't sure which ClassLoader to use, try '0', which
   will always be the System ClassLoader.

