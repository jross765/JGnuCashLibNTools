# Notes on the Module "API Extensions"

This module provides simplified, high-level access functions to a GnuCash file 
via the "API" module (sort of "macros") for specialized, complex tasks.

## Sub-Modules
Currently, the module consists of just one single sub-module: "SecAcct".

### SecAcct
Currently, this sub-module contains just one single class: `SecuritiesAccountTransactionManager`, 
which provides a simplified, high-level interface for generating buy- and dividend transactions 
in a securities account (brokerage account).

## Major Changes
### V. 1.4 &rarr; 1.5
* Sub-module SecAcct:
  * Added support for stock splits / reverse splits.
  * Added helper class that filters out inactive stock accounts.
  * Added `WritableSecuritiesAccountManager` (analogous to separation in module "API").

### V. 1.3 &rarr; 1.4
Created module.

## Planned
* Sub-module SecAcct: 
	* More variants of buy/sell/dividend/etc. transactions, including wrappers which you provide account names to instead of account IDs.
	* Possibly new class for high-level consistency checks of existing transactions, e.g.: All dividends of domestic shares are actually posted to the domestic dividend account.

* New sub-module for accounting-macros, such as closing the books.

* New sub-module for management of commodities and currencies (esp. bulk quote import).

* New sub-module for management of customer jobs and invoices and possibly employee vouchers.

## Known Issues
(None)

