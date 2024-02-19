`JGnuCashLib` is a Java library for reading and writing the XML file format of the 
GnuCash open source accounting software.

# Compatibility
## System Compatibility
Version 1.4 of the library has been tested with 5.4 on Linux (locale de_DE) and 
OpenJDK 17.0.

## Locale/Language Compatibility
**Caution:** Will only work on systems with the following locale languages:

* English
* French
* Spanish
* German

However, it has **not** been thoroughly tested with all of them, but just on a system 
with locale de_DE (for details, cf. the API module documentation).

## Version Compatibility
| Version | Backward Compat. | Note                           |
|---------|------------------|--------------------------------|
| 1.3     | no               | "Medium" changes in interfaces |
| 1.2     | no               | Minor changes in interfaces    |
| 1.1     | no               | Major changes in interfaces    |
| 1.0.1   | yes              |                                

# Modules and Further Details

* [API](https://github.com/jross765/jgnucashlib/tree/master/gnucash-api/README.md)

* [Example Programs](https://github.com/jross765/jgnucashlib/tree/master/gnucash-api-examples/README.md)

# Sister Project
This project has a sister project: 

[`JKMyMoneyLib`](https://github.com/jross765/jkmymoneylib)

Both projects do not have the same level of maturity, `JGnuCashLib` is currently more 
advanced than `JKMyMoneyLib`. Obviously, the author strives to keep both projects symmetrical 
and to eventually have them both on a comparable level of maturity.

What is meant by "symmetry" is this context? It means that `JKMyMoneyLib` has literally evolved / 
is literally evolving from a source-code copy of its sister, this project (i.e., copy the code, 
then adapt it). Given that KMyMoney and GnuCash are two finance applications with quite a few 
similarities (both in business logic and file format), this approach makes sense. 

Of course, this is a "10.000-metre bird's-eye view". As always in life, things are a little more
complicated once you go into the details. Still, looking at the big picture and at least 
up to the current state of development, the author has managed to keep both projects very 
similar on a source code level -- so much so that you partially can use `diff`. You will, 
however, also see some exceptions here and there where that "low-level-symmetry" is not 
maintainable.

# Acknowledgements

Special thanks to:

* **Marcus Wolschon (Sofware-Design u. Beratung)** for the original version (from 2005) and 
  the pioneering work (which, by the way, contained way more than what you see here) and for 
  putting the base of this project under the GPL.

    This project is based on Marcus's work. There have been major changes since then, but you can still see where it originated from.

    (Forked from http://sourceforge.net/projects/jgnucashlib / revived in 2017, after some years of enchanted sleep.)

* **Deniss Larka** for kissing the beauty awake and taking care of her for a couple of years.

  (Forked from https://github.com/DenissLarka/jgnucashlib in 2023)
