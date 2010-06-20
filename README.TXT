Clojure Live REPL - 2009-10-18
David Powell <djpowell@djpowell.net>
<http://github.com/djpowell/liverepl>

This software is distributed under the MIT licence.

----------------------------------------

Build
=====

To build:

  Copy clojure.jar to: ./liverepl-server/lib/clojure.jar

  Run ant

The build will be copied to: ./build/

----------------------------------------

Configuration
=============

Edit liverepl.bat to point to your installed JDK

----------------------------------------

Operation
=========

To see a list of running Java processes on the system, and their
process ids, enter:

   liverepl

To connect a repl shell to a running Java process, enter:

   liverepl <pid>

      -- where the pid is the process id for the process.

