# Notes on the Module "Base"

This module is a small helper library that has been spun off from the API module, the main library. 

This might seem overly complicated, but in fact, it was necessary, because the author uses it in another, external project as well, without using the API there.

## Major Changes 
### V. 1.3 &rarr; 1.4
* Created, spun off from the API module.

* Better test coverage

## Planned
* Get rid of inconsistencies in class `FixedPointNumber` (cf. below).

* Same class: Support for more data types in constructors and operation methods.

* Possibly spin off package `numbers` in yet another, package outside of this
  project (as it is completely redundant to the according package in the sister project).
  
* Alternatively to the above-mentioned points: 
  Research and re-evaluate whether we actually need this package or if 
  there is not an equivalent one from a widely used library (such as Apache Commons)
  that we can migrate to.

## Known Issues
* Inconsistencies in class `FixedPointNumber`: Every operation method changes
  the value of the instance (`add()`, `subtract()`, `multiply()`), except
  `divide()`.
