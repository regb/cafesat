CAFESAT
=======

<p align="center">
  <img height="300px" src="/logo/cafesat2.jpg" />
</p>

This is the official repository for the CafeSat source code. CafeSat is a
SAT/SMT solver written entirely in Scala. The aim of CafeSat is to provide an
efficient command-line tool to solve SMT problems, as well as a native library
for Scala programs.

Setup
-----

CafeSat is build with `sbt`.

A jar file that can be integrated to another project can be generated with:

    sbt package

If you wish to run CafeSat as a standalone tool, the jar file can be executed
using the JVM.  You need to invoke the `cafesat.Main` class.

The prefered way to use CafeSat as a standalone tool is to generate a runner
script:

    sbt cafesat

Then you can run CafeSat as follows:

    ./target/cafesat [ OPTIONS ] [ INPUT ]

<!--
If no INPUT is specified, then CafeSat will expect SMT-LIB commands on the standard input, which
is the standard behaviour of the SMT-LIB specifications. If an input is specified, then it will
be parsed as an SMT-LIB script, and fully interpreted. Options can be used to modify this behaviour,
for example the `- -dimacs` option will interpret the INPUT file in Dimacs CNF format.
-->

<!--
Examples
--------

To start an interactive session in the REPL with SMT-LIB:

    ./target/cafesat

To execute an SMT-LIB script you can do the following:

    ./target/cafesat < input.smt2

which simply transparently redirect stdin to the content of the file. Or use:

    ./target/cafesat input.smt2

in which CafeSat will open the file before feeding it to the SMT solver.

To solve Dimacs SAT problems, use:

    ./target/cafesat - -dimacs input.cnf

-->

Scala API
---------

CafeSat exports an API usable from Scala programs. The API is not stable
yet and is expected to change frequently. It will NOT be backward compatible.

A minimal Scala doc is available [here](http://regb.github.io/cafesat/apidocs/#cafesat.api.package).

The best way to learn the API is probably to look at some projects relying on CafeSat:

  * [Cafedoku](https://github.com/regb/cafedoku)

Be sure to check which version of the library is used on each project.

Literature
----------

CafeSat has been first presented in the [Scala'13 workshop](http://dx.doi.org/10.1145/2489837.2489839).
However, note that the content of the paper is getting out of date.

Licence
-------

CafeSat is distributed under the terms of The MIT License.

All source code in this repository is distributed under this license. The
reference text is in the file LICENSE, no copy of the text shall be included in
any of the source file, but it is implicitly assumed they are available under
the terms specified in LICENSE.

BY COMMITTING TO THIS REPOSITORY, YOU ACCEPT TO RELEASE YOUR CODE UNDER
THE TERMS OF THE MIT LICENSE AS DESCRIBED IN THE LICENSE FILE.

Copyright
---------

The copyright for each portion of code is owned by the respective committer,
based on the git history. There is no per file or per function copyright as
this does not make sense in general. Sorry to be picky, but that's copyright
law for you. More information can be found in the COPYRIGHT.md file.
