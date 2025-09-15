# Notes on the Module "Base"

This module is a small helper library that has been spun off from the API module, the main library. 

This might seem overly complicated, but in fact, it was necessary, because the author uses it in another, external project as well, without using the API there.

## Major Changes 
### V. 1.6 &rarr; 1.7
* Introduced new (dummy) ID types for type safety and better symmetry with sister project: `GCshAcctID`, `GCshTrxID`, etc.

  *Rationale:* Improve type safety and overall robustness, the really important part being to use those new types in the the interfaces, of the other modules, e.g.: 

  `getAccountByID(GCshID acctID)` &rarr; `getAccountByID(GCshAcctID acctID)`

  This now looks much like the according types in the sister project, but in contrast to there, there is actually no internal difference between those types. 

  I have been weighing over the pros and cons of this step for quite a while now (I admit, it seems exaggerated at first glance) and finally came to the conclusion: Yes, it should be done -- not so much for the symmetry between the two sister projects (one can also over-emphasize that), but for the (pseudo-)type safety and thus in order to significantly reduce the probability of making certain kinds of mistakes (and also to reduce the time finding them), fully leveraging Java's static type safety -- both for the developer(s) of this lib and for its users (after all, that was the rationale behind the introduction of GCshID in the first place, the original author handling e.t. just with strings -- yes, it works, but it's far too error-prone).

### V. 1.5 &rarr; 1.6
* `GCshID`: Added constructor with UUID (this comes naturally).

### V. 1.4 &rarr; 1.5
* `FixedPointNumber`: Ironed out some inconsistencies: Some methods would change the (value of the) object itself, some others would not and instead generate a new one. Now, every calc-operation changes the (value of the) object itself. 

  This admittedly leads to less-than-beautiful code in the other modules, because you now have to use the method `copy()` a lot of times, but we had to do so before the changes anyway here and there, and at least it's consistent now.

* Better test coverage: Now, I feel much better about it.

* Finally: Moved package `numbers` to `SchnorxoLib` (and thus merged it with the almost 100%-redundant code in the sister project's according package).

### V. 1.3 &rarr; 1.4
* Created, spun off from the API module.

* Better test coverage

## Planned
./.

## Known Issues
./.
