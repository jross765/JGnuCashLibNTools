# Project "Java GnuCash Lib 'n' Tools"

`JGnuCashLibNTools` 
is a free and open-source set of Java-libraries for reading and writing the XML file 
format of the 
GnuCash open source small-business accounting software 
([gnucash.org](https://gnucash.org)).

This project is not affiliated with nor sponsored or coordinated by the developers of the 
GnuCash 
project.

It began as a friendly fork of Deniss Larka's `jgnucashlib` (cf. section "Acknowledgements" at the end of this document).
After initial cooperation, it has gone its own way (we are still on friendly terms). 
Since then, the code has undergone a lot of changes, and the project has greatly expanded its scope as well as its 
quality by introducing regression tests.

## Modules and Further Details

Here is a high-level overview:

![Module Architecture](doc/developer/module-arch_cut.png)

List of modules and other relevant stuff:

* JGnuCashLibs (the API)

  The four following together form the API:

  * [Base (gnucash-base)](https://github.com/jross765/gnucash-base)

    Some basic data types and helper classes (project-specific).

  * [API (gnucash-api)](https://github.com/jross765/gnucash-api)

    The Core API (read/write) -- no bells, no whistles, yet still providing a certain level 
    of convenience as well as solid type safety and input-value checks.

    "Good and well-tested wrappers for the the JAXB-generated classes".

  * [API Specialized Entities (gnucash-spec)](https://github.com/jross765/gnucash-api-spec)

    Bells & whistles, part 1: Some specialized classes derived from the Core API.

  * [API Extensions (gnucash-ext)](https://github.com/jross765/gnucash-api-ext)

    Bells & whistles, part 2: Some specialized helper classes providing
    high-level functionalities based on low-level actions in of both the Core API
    and the Specialized Entities.

* JGnuCashTools:

  * [Tools (gnucash-tools)](https://github.com/jross765/gnucash-tools)

    CLI Tools (read/write).

  * [Viewer (gnucash-viewer)](https://github.com/jross765/gnucash-viewer)

    The (read-only) GUI.

  * [Example Programs (gnucash-api-examples)](https://github.com/jross765/gnucash-api-examples)


    Some examples on how to use the API (all levels).

    Actually not "officially" part of the 
    JGnuCashTools, 
    but we won't define a new category just for this one, and they are in the same ballpark...

* Project-specific basic stuff:

  There is one project-specific pseudo-base-lib containing some semi-generic
  stuff used by this project as well as by its sister:

  [SchnorxoLib](https://github.com/jross765/schnorxolib)

  It contains some basic data types and helper classes
  used by both projects.

* Miscellaneous:

  Of course, a couple of "real" libs are also used:

  * [Jakarta XML Binding (JAXB)](https://eclipse-ee4j.github.io/jaxb-ri/)

    Obviously...

  * [Apache Commons](https://commons.apache.org)

    Ye olde venerable collection of general-purpose Java libs.
    The following ones are used:

    * [CLI](https://commons.apache.org/proper/commons-cli/)
    * [Configuration](https://commons.apache.org/proper/commons-configuration/)
    * [IO](https://commons.apache.org/proper/commons-io/)
    * [Numbers](https://commons.apache.org/proper/commons-numbers/)

    Actually, the Configurtion lib is, at this stage, not really
    used yet. But it's ready to be and definitely will.

  * [Joda Money](https://www.joda.org/joda-money/)

    (Doesn't provide real added value in this project. We will therefore 
    probably get rid of that dependency in the next release).

  * [Progress Bar](https://github.com/ctongfei/progressbar/)

  * [JLine](https://jline.org/)

## Compatibility
Cf. document "[Compatibility](https://github.com/jross765/JGnuCashLibNTools/compatibility.md)"

## Major Changes
Cf. document "[Major Changes](https://github.com/jross765/JGnuCashLibNTools/major_changes.md)"

## Level of Maturity
This software is beta.

It is worth noting, though, that the author has been using both the published tools 
as well as some unpublished ones (the latter ones also based on 
`JGnuCashLibs`) 
on a nearly daily basis 
for a over a year now (june 2026)
to facilitate and part-automate his 
business' 
accounting. This proves that the software is well-tested and stable enough 
for a real-world setting (as opposed to theoretical test cases and arbitrary examples).

Therefore, the current maintainer now feels confident not just to use the software in 
his own particular productive environment, but also to encourage others to use it. 
However, he is experienced a developer enough to know that there are other production 
environments and other use cases out there, and that only by further usage and testing 
by at least a handful of other users in real-world scenarios for a year or so, the 
software can mature to finally attain genuine "production-ready" status.

In short: You are encouraged to use this software, but be advised to use it under the following principles:

*  Consider the API's read-branch and the read-only tools to be safe (i.e., they are not only *called* read-only, but they actually *are*).

* As for the write-branch, take the usual precautions: 

  * Do not just take the software and "wildly" change things in your valuable 
    GnuCash 
    files that you may have been building for years or possibly even decades. 
    It still might contain some non-trivial bugs, and you should not assume that 
    it works correctly in all conceivable edge and corner cases.
  * If you write your own tools, be aware that the lib allows you to *change* the 
    GnuCash 
    file loaded. You are, however, advised not to do so in the beta stage, but 
    rather to *generate a new one* instead (as done in the published tools) and 
    keep the old version for a while.
  * If you have to change your file, **make backups before you use this lib/these tools!** Take your time and check the generated/changed files thoroughly before moving on.
    The `diff` tool is your friend as well as the provided `Dump` tool!

## Compile and Install
Cf. document "[Compile and Install](https://github.com/jross765/JGnuCashLibNTools/compile_install.md)"

## Planned

### Overall
* Possibly contribute some more Java tools / wrapper scripts that already exist in separate repositories that currently are not published.

### Module-Specific
Cf. the according module's README file (links above).

## Sister Project
This project has a sister project: 
[`JKMyMoneyLibNTools`](https://github.com/jross765/JKMyMoneyLibNTools)

By now, both projects have roughly the same level of maturity. 
Obviously, the author strives to keep both projects symmetrical.

What does "symmetry" mean in this context? It means that 
this project's sister, `JKMyMoneyLibNTools`,
has literally evolved from a source-code copy of
this project.
Meanwhile, changes and adaptations are going in both directions.
Let's call this "coupled development". 
Given that KMyMoney and GnuCash are two finance applications with quite a few 
similarities (both in business logic and file format), this approach makes sense
and has been working well so far.

Of course, this is a "10.000-metre bird's-eye view". As always in life, things are a little more
complicated once you go into the details. Still, looking at the big picture and at least 
up to the current state of development, the author has managed to keep both projects very 
similar on a source code level -- so much so that, throughout large parts of the code,
you can use `diff`. You will, however, also see some exceptions here and there where that 
"low-level-symmetry" is not maintainable.

## Acknowledgements

Special thanks to:

* **Marcus Wolschon (Sofware-Design u. Beratung)** for the original version (from 2005) and the pioneering work and for putting the base of this project under the GPL.

    This project is based on Marcus' work. There have been major changes and additions since then, but you still can see where it originated from.

    (Forked from `http://sourceforge.net/projects/jgnucashlib` and revived in 2017, after some years of enchanted sleep.)

* **Deniss Larka** for kissing the beauty awake and taking care of her for a couple of years (excluding the viewer).

  (Forked from `https://github.com/DenissLarka/jgnucashlib` in 2023)

* **Roberto Bertolino** for contributing to Deniss' work and maintaining the viewer.

  (Module "gnucash-viewer" forked from `https://github.com/rbertoli/gnucash` in 2025)
