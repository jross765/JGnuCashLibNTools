# Major Changes
Here, only the top-level changes on module-level are mentioned. 
For more details, cf. the README files of the resp. modules (links above).

## V. 2026-04 &rarr; 2026-06
* Overall / cross-module:
  * Introduced budgets.
  * Deprecated all `FixedPointNumber`-related stuff.
  * Partially re-implemented things so that `BigFraction` is used
    instead of `FixedPointNumber`.

* Parent repo (this one): Nothing special.

* Module "Base": Nothing special apart from above-mentioned.

* Module "API (Core)":
  * Maintenance.

* Module "API Specialized Entities: Maintenance.

* Module "API Extensions": Maintenance.

* Module "API Examples": 
  * New example programs for budgets.

* Module "Tools": 
  * New tools for budgets.
  * Maintenance.

* Module "Viewer":
  * Added french language files.
  * Maintenance

Module versions:

| **Name**                 | **Version** |
|--------------------------|---------|
| Base                     | 1.9     |
| API (Core)               | 1.9     |
| API Specialized Entities | 0.4     |
| API Extensions           | 1.9     |
| API Examples             | 1.9     |
| Tools                    | 1.9     |
| Viewer                   | 1.3     |

## V. 1.8 &rarr; 2026-04
**Caution: With this release, the top-level version naming scheme has changed
in order to avoid confusion with the single modules' version numbers.**

* Parent repo (this one): Nothing special.

* Module "Base": Changed class names, now finally fully honoring the GnuCash naming 
  convention ("security" vs  "commodity").

* Module "API (Core)":
  * Loading files now shows progress bars in console (optional).
  * Maintenance.

* Module "API Specialized Entities: New.

* Module "API Extensions": Maintenance.

* Module "API Examples": 
  * New example program for API Specialized Entities".
  * New package structure to better reflect different modules.

* Module "Tools":
  * All tools now load files showing progress bars (cf. Module "API (Core)").
  * Maintenance.

* Module "Viewer":
  * Program now accepts various command line args, supporting start variants,
    supporting various additonal use cases.
  * Maintenance.

Module versions:

| **Name**                 | **Version** |
|--------------------------|---------|
| Base                     | 1.8     |
| API (Core)               | 1.8     |
| API Specialized Entities | 0.3     |
| API Extensions           | 1.8     |
| API Examples             | 1.8     |
| Tools                    | 1.8     |
| Viewer                   | 1.2     |

## V. 1.7 (RESTRUCT) &rarr; 1.8
**Caution: Please note that, due to the changes in the last major release 
(splitting up the one big repository in several smaller ones), 
from now on, each module is versioned on its own, and the overall project's version 
(1.8, in this case) 
need not be/is not identical to the single modules' versions any more.**

* Parent repo (this one): Finished restruct work, i.e. made the
  (Maven) modules' repos Git sub-modules as well.

* Module "Viewer": New.

  Well, only technically new in this project; originally written by Marcus Wolschon 
  and maintained by Roberto Bertolino for a while, I have taken it and adapted it 
  to this fork. In short: Simplified it (viewer only, no editing) and I18N.

* Module "API": Bug-fixes and mini-improvements.

* The other modules have changed only technically; essentially (i.e., code) unchanged:
  * "Base"
  * "API Examples"
  * "API Extensions"
  * "Tools"

Module versions:

| **Name**                 | **Version** |
|--------------------------|---------|
| Base                     | 1.7.1   |
| API (Core)               | 1.7.1   |
| API Extensions           | 1.7.1   |
| API Examples             | 1.7.1   |
| Tools                    | 1.7.1   |
| Viewer                   | 1.1.0   |

## V. 1.7 &rarr; 1.7 (RESTRUCT)
Split up the all-encompassing repository into several ones: One per module plus one for the parent (this one).

Apart from that, I have made *no relevant changes* (i.e. only small changes in the README-files etc., but not in the actual source code).

*Rationale*:

I know, that comes with some disadvantages, and there are quite a few people who would advise against it for valid reasons. 

That being said, life's not black and white, and while I acknowledge that having everything in one single repository makes things easier in the early stages of development, I am convinced that in the long run, the advantages of doing so will outweigh the disadvantages for the following reasons:

* The modules' rates of change will vary considerably (they already do, and they will problably do even more in the years to come).

* It feels odd *not* to have "API Examples" and "Tools" in separate repositories (and that's just the most obvious example).

* The measure will greatly facilitate accepting and managing future contributions from others (or possibly handing single modules completely over to others), which I currently would feel much more inclined to do for the modules "API Extensions" and "Tools" than for the other ones.
  
* Last not least, I manage some additional (unpublished) projects that way, and I would like to keep things consistent (you see, my day has only 24 hours just as yours, and I have other things to do...).

*History*:

I have made a clean cut:

* The top-level repository (this one) contains the whole history up to V. 1.7. 
* The newly-generated sub-repos contain no history until V. 1.7. But they will contain their respective module's history from that point onwards.

## V. 1.6 &rarr; 1.7
* Overall:
  * Introduced new (dummy) ID types for type safety and better symmetry with sister project.

## V. 1.5 &rarr; 1.6
* Module "API": 
 
  * Some bug-fixing and cleanup-work, making code more robust.
  * New functionalities.

* Module "API Extensions": 
  * New sub-module.
  * Expanded functionality of already-existing module.

* Module "Tools": Maintenance.

## V. 1.4 &rarr; 1.5
* Added module "Tools".

* New external dependency (outside of Maven central): 
[`SchnorxoLib`](https://github.com/jross765/Schnorxolib), 
a small library that contains some auxiliary stuff that is used both in this and the sister project. Some of the code in the module "Base" has moved there.

## V. 1.3 &rarr; 1.4
Changed project structure:

* Introduced new module "Base" (spun off from "API").

	This was necessary because the author is using the new module in other, external projects (not published).

* Introduced new module "API Extensions"

	Currently, this module it is very small. It will (hopefully) grow.

## V. 1.2 &rarr; 1.3 and Before
Cf. the README file of modules "API" and "Example programs" (links below).
