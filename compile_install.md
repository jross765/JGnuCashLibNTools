# Compile and Install

## Compiling the Sources
To compile the sources, do the following:

1) Make sure that you have Maven installed on your system.

2) Build and install [`SchnorxoLib`](https://github.com/jross765/Schnorxolib) V. 0.3
   (cf. details there).

3) Clone this repository as well as its sub-repositories. 

      ```console
    $ git clone --recurse-submodules https://github.com/jross765/JGnuCashLibNTools
      ```

4) Check out the latest version tag. In this case: `V_2026-06`.

      The current maintainer has, in the course of his professional career, met plenty of self-declared 
      super-pro developers who do not seem to understand the concept of version tags and configuration 
      management, so please bear with him for telling you the seemlingly obvious...

5) Compile the sources:

      a) Adapt the path to your local repository in *all* pom.xml files 
         (search for "`schnorxolib-base-systemPath`").
         All other libs are drawn from Maven Central.

      b) Type:

         ```console
       $ ./build.sh
         ```

6) Perform the test cases (optional):

      ```console
    $ ./test.sh
      ```

## Installing and Using the Software

Installation is a manual process -- there is no "install" target
in the build process (well, actually, there is one, but only
in the Maven sense, meaning its repository under `~/.m2`).

Consequently, there is no pre-defined/default path for the software; 
it does not really matter.

As always with Java libs, you will have to set the classpath file,
preferrably in a file called `environment.sh` that you must source
before starting one of the tools. Don't forget the basic libs used 
(list above).

For convenience, the build process also generates top-level JAR files 
that contain all dependencies (modules 
"gnucash-tools" and "gnucash-viewer").

You will also have to write your own wrapper scripts for the tools (for now).
(No, the maintainer cannot provide his own ones, at least not right now,
for specific reasons which he won't dive into now.)
You will find an example wrapper script in the folder `doc/user`.

In short: Nothing special; just as it's usually done with Java software...
