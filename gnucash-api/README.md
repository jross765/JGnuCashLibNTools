# Notes on the Module "API"

This is the core module of the project, providing all low-level read-/write access functions to a GnuCash file.

## Major Changes 
### V. 1.5 &rarr; 1.6
* Fixed a couple of bugs in write-branch of API (for various entities), esp. in object-deleting code.

* Introduced `GCsh(Writable)AccountLot(Impl)`, which has a similar relationship to GnuCashAccount as `GnuCash(Writable)TransactionSplit` has to `GnuCash(Writable)Transaction`.
    
  This is needed to report on lots in securities (stock) accounts,
  and that, in turn, is needed to prepare German tax filings (so-called "FIFO principle").

* `GnuCashWritableTransactionSplitImpl`: Fixed bug in setQuantiy().

* `GnuCashWritableAccount(Impl)`, `GnuCash(Writable)File(Impl)`: Expanded interface and implemented it.
    
* Significantly improved overall test coverage, esp. in write-branch.

* Various minor changes, cleaning and improving code.

### V. 1.4 &rarr; 1.5
* `GnuCash(Writable)Commodity(Impl)`: New methods `get(Writable)StockAccounts()`, which is a handy short-cut for specific use cases.

* `GnuCash(Writable)File(Impl)`: Implemented empty skeleton methods that had been forgotten and previously just returned null, such as `getAccountsByName()`.

* Added a few method implementations that had been forgotten here and there.

* Re-iterated over some code, e.g.:
	* Handling of invoice entries (all variants)
	* `GnuCashJobInvoice(Entry)` vs `GnuCashGenerInvoice(Entry)` (inconsistencies)
	* `GnuCash(Writable)GenerInvoice(Impl)` vs. `GnuCash(Writable)GenerJob(Impl)`
	* `GnuCash(Writable)[Customer|etc](Impl)`
	* `GnuCashCommodityImpl`
	* `GnuCash(Writable)FileImpl`
	* `GCshOwner(Impl)`
	* Improved overall robustness by more consistently checking method parameters.

* Finally completed list of internal manager classes `File[XYZ]Manager`.

* Minor changes in interfaces.

* Fixed a couple of bugs.

* Some code-cleanup here and there.

* Improved test coverage.

### V. 1.3 &rarr; 1.4
* Extracted some basic packages to new module "Base".

* Non-trivial clean-up work, most of it under the hood. Among other things: 
  * Changed the semantics of `GnuCashObject(Impl)` (taken from sister project): Handling of slots now has moved to `Has(Writable)UserDefinedAttributes(Impl)`. Simulaneously, got rid of some code redundancies.
  * Introduced class `GnuCashGenerJobImpl` and changed implementation of 
its derivatives `GnuCash[Customer|Vendor]JobImpl` so that it is now symmetrical to the implementation of `GnuCashGenerInvoiceImpl` and its derivatives `GnuCashCustomerInvoiceImpl` etc.
  * Changed interfaces: Used `List` instead of `Collection` where appropriate (not everywhere).

* Bug-fixing.

* Changed all interface and class names from `GnucashXYZ` to `GnuCashXYZ`, following the usual spelling convention.

* De-cluttered interfaces.

* Improved test coverage.

### V. 1.2 &rarr; 1.3
* Introduced interfaces/classes for employees, emloyee vouchers and employee jobs, completely analogous to the according structures for customers and vendors in V. 1.1 (except for the fact that are no "employee jobs" that one might expect):
  * `Gnucash(Writable)Employee(Impl)` 
  * `Gnucash(Writable)EmployeeVoucher(Impl)` 

* Generalized (technically) locale-specific code (e.g., GnuCash stores certain semi-internal XML-tags with locale-specific values for transaction splits' actions). Until V. 1.2, all this was tightly tied to the german locale with hard-coded valus (de_DE). Now, we have a generalized approach open for all locales (still hard-coded, though), and support for the following locales is actually implemented:
  * English
  * French
  * Spanish
  * German

* Enhanced type safety by: 
  * introducing package `org.gnucash.basetypes`, containing, among other things, the class GCshID as a wrapper for the UUIDs used throughout the system (instead of just treating them as unsafe strings).
  * introducing several enums where before we had string constants,

* Partially re-wrote package `org.gnucash.currency`: Safer and clearer implementation, now leveraging newly introduced types from package `org.gnucash.basetypes`.

* Re-iterated and completed code in sub-package "`aux`" (both in read- and in write-branch): Now, classes and interfaces are cleanly separated for read- and write-operations *comme il faut*.

* Renamed `GCsh(Writable)Price(Impl)` to `Gnucash(Writable)Price(Impl)` and moved it from the "`aux`" sub-package up one level, as, in fact, price is a main entity.

* Introduced new packages "`hlp`" (both for interfaces and implementations) for interfaces/classes that are purely internal. *Note: This is not the same as the "`aux`" packages, whose contents are auxiliary but public).*

* Significantly improved test coverage. In so doing, found and fixed a couple of bugs.

* Lots of minor improvements and code-cleaning. Among the major changes here: 

  * De-cluttered classes `Gnucash(Writable)FileImpl` by introducing helper classes in sub-package "`hlp`". All this greatly improved the readability and maintainability of the code.
  * Updated the JavaDoc-descriptions (still a lot of work to do, though...).

* Last not least: From this version on, the author tries to keep the project as symmetrical as possible with its sister project, `JKMyMoneyLib`.

### V. 1.1 &rarr; 1.2
* Introduced interfaces/classes `Gnucash(Writable)Commodity(Impl)` for reading and writing "commodities", in GnuCash lingo an umbrella term for 
  * Currencies
  * (Real) Securities (shares, bonds, funds, etc.)
  * Possibly other assets that can be mapped to this GnuCash entity as pseudo-securities, such as crypto-currencies, physical precious metals, etc.

* Reading of commodities' prices is possible, but writing them is not possible yet. Given the above polymorphism of the commodity (cf. above), a "price" is one of the following:

  * A currency's exchange rate
  * A security's quote
  * A pseudo-security's price

   (all of them usually noted in the default currency)

  Given that we have several variants of `CmdtyCurrID` (cf. below), we also have several methods returning various types.

In this context, the following was also necessary/also made sense:

* Introduced auxiliary interface/class `GCshPrice(Impl)` representing an entry of the GnuCash price database.

* Introduced auxiliary class `GCshCmdtyCurrID` (essentially a pair of commodity name space and commodity code), which represents a security-style pseudo-ID for commodities (thus, including currencies, cf. above). In addition to that, introduced (grand-)child classes `GCshCmdtyID`, `GCshCurrID`, `GCshCmdtyID_Exchange`, `GCshCmdtyID_MIC` and `GCshCmdtyID_SecIDType` to represent various variants with adequate type-safety, i.e. for both easy and safe handling of read and write operations. 

  Note that, as opposed to all other entities in the GnuCash XML file, commodities have no internal UUID (a fact that the author finds irritating). This is one of the reasons why (pseudo-)currencies necessitate a fundamentally different approach.

* Closely linked to `GCshCmdtyCurrID` and its descendants: Major rework on the class `GCshCmdtyCurrNameSpace` (formerly `CurrencyNameSpace`): introduced several enums for enhanced type safety.

  *Background*: In and of itself, GnuCash's commodity name space can be freely defined; only suggestions are made on how to define it, e.g. a major stock exchange's abbreviation or a major securities index name. But you *need* not do it like that. The author is well aware of the fact that the GnuCash developers have -- likely after having weighed the pros and cons -- purposefully decided *not* to enforce any pre-defined values. 

  In this lib, we follow this spirit (as well as the path that the original author Marcus Wolschon followed): We *allow* the users to freely define any name space they want, but we *encourage* them to use one of the pre-defined enums in `GCshCmdtyCurrNameSpace` (expecting that buying, holding and selling conventional securities on major markets will be the most typical use case by far).

  *Note that the current definition of the enums in the class `GCshCmdtyCurrNameSpace` is by no means final and "once and for all". The author rather expects some re-work iterations on the existing ones and the introduction of new ones.*

  [ Some readers might be interested in the fact that the author uses the name space `GCshCmdtyCurrNameSpace.SecIdType.ISIN`, because he believes that this is the only one that allows easy and consistent handling of a global portfolio (and the fact that he is not so much interested in where a specific security is traded, because he follows a buy-and-hold investment strategy). ]

Further improvements:

* Interface changes in:

  * `Gnucash(Writable)Account`
  * `Gnucash(Writable)Transaction`
  * `Gnucash(Writable)File`

  Essentially leveraging improved type safety and robustness, primarily through use of `GCshCmdtyCurrID` and descendants.

* Improved retrieval of objects in `GnucashFileImpl` with `getXYZByName()`-methods (e.g., `getAccountsByName()`): There are now two variants for (almost) each of them: One with exact matching and one with relaxed matching, the latter one possibly returning several objects. (Still some work to be done here (esp. with account variants.) Cf. test cases and example programs.

* Improved test coverage.

* Some minor bug-fixing here and there.

### V. 1.0 &rarr; 1.1
* Reading and writing of (technical/generic) invoices: Not just customer invoices, but also vendor bills now. According to the internal XML structure and the part of the code that is generated from it (i.e., the XSD file), both derive from a common class that represents a "generic" invoice. In addition to that, there is also a third type: job invoice (cf. next item).
    
    ==> Introduced specialized interfaces/classes: `GnucashCustomerInvoice(Impl)`, `GnucashVendorBill(Impl)` and `GnucashJobInvoice(Impl)`, which all derive from `GnucashGenerInvoice(Impl)`. Consequently, we have the same for their entries: `GnucashGenerInvoiceEntry(Impl)`, etc.

    (This didn't seem too difficult at first, but actually it was a lot of work.)

* Invoices and bills (both customer's and vendor's) can now be assigned directly to the customer/vendor or indirectly via a job (as opposed to V. 1.0, where everything was indirect via a job). In business terms, the indirect ones are still customer invoices/vendor bills, of course, but the technical XML structure mandates a sligthly different approach: If there is a job, then the invoice's/bill's owner is not the according customer/vendor, but rather the job object, which in turn is owned by a customer/vendor. Thus, analogously to the invoices, we now also have two types of jobs: customer jobs and vendor jobs.

    ==> Introduced specialized interfaces/classes: `GnucashCustomerJob(Impl)` and `GnucashVendorJob(Impl)`, which both derive from `GnucashGenerJob(Impl)`. Both interfaces and implementations.

   (Once the splitting-up of the classes for invoices was done, this part was not too difficult.)

* Introduced handling for terms based on class `GCshBillTerms`: Both the infos of the general list as well as the customers'/vendors' default terms can be read with all details now. In this version, write-operations are only possible in the form of references (i.e., assigning already-existing terms to a customer or a vendor without changing the term details themselves).

* Better handling of tax tables based on class `GCshTaxtable(Entry)`: Analogous to terms above.

* More complete coverage of data access to customer/vendor data:

  * discount/credit (customers only)
  * default tax table (had been forgotten for vendors)
  * default terms (had not been available for both)

* Introduced new packages (both for interfaces and implementations):

   *  "spec" for the above-mentioned special variants of generic classes.
   *  "aux" for auxiliary classes (and for them, a special prefix: `GCsh`). Also, made code both redundancy-free and more readable by getting rid of nested classes (e.g., `Gnucash(xyz)Invoice.Address` &rarr; `GCshAdress` or `GGshTaxTable.TaxTableEntry` &rarr; `GCshTaxTableEntry`).

* Improved exception handling/actual working of code with real-life data (not fundamentally different, but rather small repair work -- the code partially did not work in the above-mentioned environment).

* JUnit-based set of regression test cases (with test data in dedicated test GnuCash file). This alone greatly improves security and peace of mind both for users and developers, let alone learning how to actually use the library (in lack of a proper documentation, simply look into the examples and the test cases to understand how to use the lib).

* Enhanced type safety and compile-time checks -- both were not always as strict as possible.

* (Partially) got rid of overly specific and/or obsolete code (e.g., there were methods that only make sense when using the German standard SKR03/04 chart of accounts, or the 19% VAT (originally, until 2006, 16%, which you still could find in the code)).

* Got rid of some redundancies here and there, introduced class `Const` for that (which in turn contains hard-coded values).

* Got rid of (public) methods that accept internal IDs as arguments: First, internal IDs are -- well -- internal/internally generated and thus should not be part of an API, and second, GnuCash uses only UUIDs anyway, and there is simply no point in generating these outside (to be fair: for all of these methods, there were wrappers without).

* Renamed some classes to honour naming conventions (e.g., `abcMyObjectWritingxyz` &rarr; `abcWritableMyObjectxyz`).

* Some minor cleaning here and there (e.g., small inconsistencies in date-handling, `toString()`-methods, etc.).

* Provided an extensive set of example programs (not generally-usable tools!) in a module of its own. Moved the one single example program that was there before into this module.

## Planned
It should go without saying, but the following items are of course subject to change and by no means a promise that they will actually be implemented soon:

* Invoices and bills: Support more variants, such as choosing the terms of payment or the "tax included" flag for entries.

* Get rid of ugly code redundancies here and there, esp. in the class `Gnucash(Writable)GenerInvoiceImpl`.

* Configurable values in `Const` class.

* Last not least: Provide user documentation.

## Known Issues
* Performance: When using the `Writtable`-classes (i.e., generating new objects or changing existing ones), the performance is less-than-overwhelming, especially when working on larger files.

* As mentioned above: As of now, the lib only works well when your GnuCash files are generated on a handful of system locales.

* Generating new objects currently only works (reliably) when at least one object of the same type (a customer, say) is already in the file.

* When generating invoices, you cannot/should not call the `post()`-method immediately after composing the object. The post-method will work, but the amount of the post-transaction will be wrong (thus, the transaction will be useless as it cannot be corrected manually in GnuCash; post-transactions are read-only). Instead, you should first write the results to the output file using the `GnucashWritableFile.writeFile()`-method, then re-load/re-parse the invoice generated before and then use the `post()`-method. Then, the amount will be correct.

  Cf. test classes `TestGnucashWritableCustomerInvoiceImpl`, `TestGnucashWritableVendorBillImpl` and  `TestGnucashWritableJobInvoiceImpl`.
