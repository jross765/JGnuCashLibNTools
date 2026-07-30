# Compatibility

## System and Format Compatibility
Version 2026-07
of the libs and tools has been tested with 
GnuCash 5.16 
on Linux (locale de_DE) and 
OpenJDK 21.0.

## Locale/Language Compatibility

* *API*:
  **Caution:** Due to certain design decisions the GnuCash developers took, 
  you will be able to fully leverage all the API's (Core) features *only* on 
  systems with the following locale languages:

  * English
  * Spanish
  * French
  * German
  
  That affects certain string-enum-mappings (such as for a transaction
  split's action). However, you will still be able to use the API on other
  system locales, albeit with less convenience, and you will have to 
  deal with strings in the locale's language.
  
  Please notice that the API has **not** been thoroughly tested with all of 
  the above-mentioned languages (for details, cf. the API module documentation).

* *Viewer*:
  The viewer supports the following locale languages:

  * English
  * French 
  * German

## Version Compatibility

| **Overall Version** | **Backward Compat.** | **Note**       |
|---------|---------|-----------------------------------------|
| 2026-07 | yes     | Only additions to and deprecations in interfaces |
| 2026-04 | no      | "Medium" changes in interfaces          |
| 1.8     | almost  | Minor changes in interfaces             |
| (older) |         | (Cf. Git history)                       |
