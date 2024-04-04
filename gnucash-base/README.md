# Notes on the Module "Base"

This module is a small helper library that has been spun off from the API module, the main library. 

This might seem overly complicated, but in fact, it was necessary, because the author uses it in another, external project as well, without using the API there.

## Major Changes 
### V. 1.4 &rarr; 1.5
* `FixedPointNumber`: Ironed out some inconsistencies: Some methods would change the (value of the) object itself, some others would not and instead generate a new one. Now, every calc-operation changes the (value of the) object itself. 

  This admittedly leads to less-than-beautiful code in the other modules, because you now have to use the method `clone()` a lot of times, plus explicit casting, but we had to do that here and there before the changes anyway, and at least it's consistent now.

* Better test coverage: Now, I feel much better about it.

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
* Class `FixedPointNumber`: I essentially took it from the original author. Apart from the fact that it has a misleading name, I am less than convinced that it is optimal for this project, at least in its current form: 

	* Using it gets clumsy from time to time.
	* Parsing strings is not safe and not locale-flexible.
	* It is not compatible with GnuCash's internal way of doing exact computations with rational numbers. 

  ==> Re-evaluate: Should we possibly switch completely switch to another class, possibly based on `BigRational` (cf. test cases) or s.t. comparable?