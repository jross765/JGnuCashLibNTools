# Selecting Securities (Users)

Due to the specialness of securities (commodities), both on the technical level and 
on the business-logic level, there are several things you must understand/keep in mind 
before working with them.

In a nutshell, it boils down to the following:

* *Technical level*: Securities have no technical IDs in 
  GnuCash. 
  Instead, they are technically selected with a (pseudo-)technical ID, constisting of a 
  namespace and a code.

* *Business-logic level*: In the real world out there, you normally would identify a security by its 
  public security code, which hopefully you have used when entering the security in the GnuCash file:
  * If you live in the US or Canada, then you would typically use the 1-to-4-char ticker (such as "T" or "MSFT").
    But strictly speaking, this is not enough. It always has to be qualified with the according exchange 
    (such as "NYSE_AMERICAN" (formerly known as "AMEX") or "NASDAQ").
    (The pros among you might use the CUSIP intead.)
  * If you live outside of the US and Canada, then you would typically use something else.
    In the EU, where the current maintainer lives, people typically use the ISIN (international).
    In Germany, the nostalgic folks prefer the WKN.
    Similarly, in the rest of Europe: The SEDOL (UK) or the VALOR (Switzerland), etc. etc.
  
  If you have a global portfolio, it makes sense to use the ISIN.[^1]

## Overview

Figure \ref{fig:overview} shows the two levels of security IDs in GnuCash.

![ID layers in GnuCash \label{fig:overview}](../xsec/secid-logic.png)

In theory, the two layers are clearly separated, i.e. technical IDs and business-logic ID
do not have anything to do with each other. However, the 
GnuCash 
developers chose to 
make things a little more complicated: They intentionally blurred the line between
the two layers, so that technical IDs are, in fact, pseudo-technical and quasi-business.

If they had chosen to treat securties as any other entity in GnuCash, then the 
technical ID would look something like this: `14305dc80e034834b3f531696d81b493`.
This string obviously has no meaning, i.e. it cannot / shall not be "interpreted" 
or "understood".

Well, they chose another approach: In GnuCash, a (pseudo-)technical ID looks something
like these:

* EURONEXT:SAP
* ISIN:DE000BASF111AP

Just by glancing at these, you can see that they are essentially business-logic IDs 
maskerading as technical ones; they do mean something (i.e., they have semantics) and 
can thus very well be interpreted and understood.

It is important to understand this (non-)difference between technical and business-logic IDs in
GnuCash 
before reading the next section; otherwise it will probably confuse you.

You can also see that the two examples above are composed of two parts, which is no 
coincidence but the system that GnuCash imposes: `<namespace>:<code>`. Whereas the 
name space can, in theory, be freely chosen by the user, it makes sense not to do so 
but instead choosing from a pre-defined set that `JGnuCashLib` provides. This then
leads to the concept of providing IDs "indirectly", i.e. composing them: providing
the name space and the code separately.


## Using the Tools

We use the test data file provided with module "API Extensions".[^2]
For test and illustration purposes, we have put securities into this file using various different systems (i.e., name spaces) -- something you normally would not do in real life.

We have provided a wrapper script for the according tool: ::TODO

Then, you have several options:

* *Mode "ID"*:
  Specifying the security by its (pseudo-)technical ID.
  
  This variant has two sub-variants:
  
  * *Sub-mode "DIRECT"*:
    Specifying the (pseudo-)technical ID directly, using the syntax 
    `<namespace>:<code>`.

    This approach always works, but does not leverage the pre-defined name spaces. In case of an error, you won't get precise logs and could possibly spend a lot of time finding it.

    Example:

    ```bash
    $ gcsh_get_sec_info.sh \
        -f test.gnucash \
        -ssm ID \
        -sssm DIRECT \
        -sec EURONEXT:SAP
    ```

    or 

    ```bash
    $ gcsh_get_sec_info.sh \
        -f test.gnucash \
        -ssm ID \
        -sssm DIRECT \
        -sec ISIN:DE000BASF111AP
    ```

    In essence, with the direct method, we kind of "stubbornly" pretend not to know that the "technical" ID is, 
    in fact, a business-logic ID. Instead, we see the ID as just an unnecessarily long string
    that has no further meaning.

    The advantage of this "stubborn" approach is that you can use all provided tools
    somewhat symmetrically (remember that all other entities in 
    GnuCash 
    do have genuine
    technical IDs).

  * *Sub-mode "INDIRECT_XYZ"*:
    Specifying the (pseudo-)technical ID indirectly, using the various command line options.

    This approach leverages the 
    `JGnuCashLib`'s 
    built-in type-safety early in the execution
    and will thus lead to far more precise error logs in case of an error.

    There are three sub-modes to choose from: 
    * INDIRECT_EXCHANGE_TICKER:
    
      Here, `<namespace>:<code>` becomes `<exchange:ticker>`, where
      `<exchange>` is the semi-formal, non-standardized but widely-used 
      abbreviation of a major exchange
      (e.g., "NYSE", "EURONEXT", "XETRA", "JPX", etc.),
      and `<ticker>` is the exchange-specific ticker of the security.
      
    * INDIRECT_MIC:
    
      Here, `<namespace>:<code>` becomes `<mic:mic-code>`, where
      `<mic>` is the formal Market Identifier Code (MIC), which has
      been standardized in ISO 10383
      (e.g., "XNYS", "XPAR", "XSHE" etc.),
      and `<mic-code>` is the same as `<ticker>` above.
      
    * INDIRECT_SEC_ID_TYPE:
    
      Here, `<namespace>:<code>` becomes `<sec-id-type:sec-id>`, where
      `<sec-id-type>` is the type of the public security ID
      (e.g. "ISIN", "CUSIP", "SEDOL", "WKN" or similar),
      and `<sec-id>` is the actual security ID under the security ID type's system 
      (e.g., "DE0007100000" for "ISIN", "710000" for "WKN", etc.).

    Example:

    ```bash
    $ gcsh_get_sec_info.sh \
        -f test.gnucash \
        -ssm ID \
        -sssm INDIRECT_EXCHANGE_TICKER \
        -exch EURONEXT \
        -tkr SAP
    ```
    
    or

    ```bash
    $ gcsh_get_sec_info.sh \
        -f test.gnucash \
        -ssm ID \
        -sssm INDIRECT_SEC_ID_TYPE \
        -secid-type ISIN \
        -is DE000BASF111AP
    ```

    In essence, with the indirect method, we "acknowledge" the semantics of the 
    pseudo-technical ID, as opposed to the direct method.
  
    Notice that the second example uses the ISIN, which in this case is used to specify the security's (pseudo-)technical ID (because the parameter `-ssm` is set to "INDIRECT_SEC_ID_TYPE").

    Also notice the parameters `-exch EURONEXT` and `-secid-type ISIN` in the examples. These are, as already
    stated, *pre-defined* values of `JGnuCashLib`, and strictly speaking, they have nothing to do with what you 
    actually have in your GnuCash file (obviously, it is strongly recommended that you use these pre-defined values when editing securities in GnuCash).[^3]
    But if, for example, in one of your file's securities you wrote, say, "EURONXT" instead of "EURONEXT" by accident, 
    then you would not be able to retrieve that one with the above-mentioned indirect method. You would instead have to use 
    the direct method.

    On the other hand, you don't need to make this kind of mistake: Just use the tool `GenSec`, and 
    `JGnuCashLib` will ensure that only the pre-defined values are used.

* *Mode "ISIN"*:
  Specifying the security by its business logic ID (the field "X-Code" in GnuCash lingo), 
  i.e. its ISIN, CUSIP, SEDOL, WKN or similar official security identifiers (without name-space prefix).

  This approach obviously only works when you actually have filled the field 
  "X-Code", 
  which is typically redundant to the security's pseudo-technical ID. 
  That bears potential for errors, so this approach will not always work.
  However, once you have your data in order, it is the most convenient method.

  Example:

  ```bash
  $ gcsh_get_sec_info.sh \
      -f test.gnucash \
      -ssm ISIN \
      -is DE000BASF111
  ```

  Notice the difference to the previous example: We also use the parameter `-is` here, but this time, it is used to search over the field "X-Code" (because the parameter `-ssm` is set to "ISIN").
  Also notice that -- for the time being -- the exact same command is used when you have CUSIPs, SEDOLs, WKNs or something
  else in your X-Code. Still the above notation with "ISIN" etc. is used.[^4]

* *Mode "NAME"*:
  Specifying the security by its name.
  
  ::TODO Select by name (not recommended but possible for get_sec_info, and not supported for upd_sec).


[^1]: This is how thes current maintainer does it in his own portfolio, and it's been working well
      for decades now.

[^2]: The test data files of the other modules will almost certainly work as well -- they all are very similar.

[^3]: To get a list of all these values, just start the program with the help flag: `gcsh_get_sec_info.sh -h`.

[^4]: The current maintainer uses only ISINs in his own 
      business'
      file, because the ISIN is the only truly global and stable identifier.
