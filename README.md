# liverepl

liverepl lets you connect a Clojure repl to any running Java or Clojure process on your machine.
The process does not need to have been set up in any special way beforehand.

You can use the repl to develop interactively, run code, inspect variables, and
redefine Clojure functions.

## Getting started

Download and unzip the binary release from:
<https://github.com/djpowell/liverepl/downloads>

Set the JDK_HOME environment variable to point to the location of your JDK.

## Usage

To see a list of running Java processes on the system, and their
process ids, enter:

```sh
liverepl
```

To see the available ClassLoaders for a specific process, enter:

```sh
liverepl <pid>
```

      -- where the pid is the process id for the process, obtained in
         the step above.

To connect a repl to the process, enter:

```sh
liverepl <pid> <classloader-id>
```

      -- where the pid is the process id for the process.
      -- and the classloader-id was obtained in the step above.

If you aren't sure which ClassLoader to use, try '0', which will always be the System ClassLoader.

For advanced configurations, such as connecting to a web application on Tomcat, you may want to
connect to an alternate classloader.

liverepl currently finds classloaders for:

  * The system classloader
  * Thread context classloaders for all threads
  * Webapp classloaders for Tomcat webapps

You will now be presented with a clojure repl, which you can use to type clojure code.  For clojure
programs you will be connected using the verson of clojure used by that program, otherwise the
version of clojure from liverepl will be used.

## Applications

### Interactive development

In emacs use:

```
C-u M-x run-lisp
liverepl <pid> <classloader-id>
```

To interactively develop clojure code in `clojure-mode`, using `C-xC-e` to redeploy functions in the
running process

### Concurrency testing

Start a standalone clojure repl, and then connect multiple live-repls to it.  You will be able
to test out clojure's concurrency features, by using multiple repls connected to the same environment.

## Build

To build:

Ensure that the version of `java` on your path is from a JDK.

```sh
cd liverepl-agent
lein install
cd ..
cd liverepl-client
lein install
cd ..
cd liverepl-server
lein install
cd ..
cd liverepl
ant
```


The build will be available in: `build`


## License

liverepl
<https://github.com/djpowell/liverepl>
Copyright (C) 2009-2012 David Powell

Distributed under the Eclipse Public License, the same as Clojure.

