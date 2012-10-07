Setting up Fabric {#setup}
=================

Requirements
------------
This Fabric distribution builds on Unix. We recommend that you use Java
6 or later. We have experienced problems with older versions. Fabric
is compiled with the [Apache Ant build tool](http://ant.apache.org/).


Configuring
-----------
-# Before using Fabric, you must specify the locations of the Polyglot
   and Jif distributions. Copy `config.properties.in` to
   `config.properties` and edit appropriately. On Mac OS you may also
   need to tell Fabric where to find the Java installation.
~~~
      $ cp config.properties.in config.properties
      $ vim config.properties
~~~
-# Configure the scripts in the `bin` directory. From the top-level
   directory, run:
~~~
      $ ant bin
~~~


Building
--------
Fabric comes pre-compiled as Jar files in the `lib` directory. If you
wish to rebuild Fabric, run `ant` from the top-level directory:
~~~
  $ ant
~~~
For other useful build targets, run `ant -p` from the top-level directory.