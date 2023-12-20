package org.gnucash.api.read.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.GncV2.GncBook.GncGncInvoice;
import org.gnucash.api.generated.GncV2.GncBook.GncGncInvoice.InvoiceOwner;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashObject;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;
import org.gnucash.api.read.impl.spec.GnucashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnucashInvoice that uses JWSDP.
 */
public class GnucashGenerInvoiceImpl implements GnucashGenerInvoice 
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GnucashGenerInvoiceImpl.class);

  protected static final DateTimeFormatter DATE_OPENED_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
  protected static final DateTimeFormatter DATE_OPENED_FORMAT_BOOK = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
  protected static final DateTimeFormatter DATE_OPENED_FORMAT_PRINT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  
  // ::TODO Outdated
  // Cf. https://stackoverflow.com/questions/10649782/java-cannot-format-given-object-as-a-date
  protected static final DateFormat        DATE_OPENED_FORMAT_1 = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);  
  protected static final DateFormat        DATE_POSTED_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

  // -----------------------------------------------------------------

  /**
   * the JWSDP-object we are facading.
   */
  protected GncV2.GncBook.GncGncInvoice jwsdpPeer;

  /**
   * The file we belong to.
   */
  protected final GnucashFile file;
  
  /**
   * Helper to implement the {@link GnucashObject}-interface.
   */
  protected GnucashObjectImpl helper;

  // ------------------------------

  /**
   * @see GnucashGenerInvoice#getDateOpened()
   */
  protected ZonedDateTime dateOpened;
  
  /**
   * @see GnucashGenerInvoice#getDatePosted()
   */
  protected ZonedDateTime datePosted;

  /**
   * The entries of this invoice.
   */
  protected Collection<GnucashGenerInvoiceEntry> entries = new HashSet<GnucashGenerInvoiceEntry>();

  /**
   * The transactions that are paying for this invoice.
   */
  private final Collection<GnucashTransaction> payingTransactions = new ArrayList<GnucashTransaction>();

  // ------------------------------

  /**
   * @see #getDateOpenedFormatted()
   * @see #getDatePostedFormatted()
   */
  private DateFormat dateFormat = null;

  /**
   * The currencyFormat to use for default-formating.<br/>
   * Please access only using {@link #getCurrencyFormat()}.
   * @see #getCurrencyFormat()
   */
  private NumberFormat currencyFormat = null;

  // -----------------------------------------------------------------

  /**
   * @param peer the JWSDP-object we are facading.
   * @see #jwsdpPeer
   * @param gncFile the file to register under
   */
  @SuppressWarnings("exports")
  public GnucashGenerInvoiceImpl(
          final GncV2.GncBook.GncGncInvoice peer,
          final GnucashFile gncFile) {
      super();

	if (peer.getInvoiceSlots() == null) {
	    peer.setInvoiceSlots(new ObjectFactory().createSlotsType());
	}


      jwsdpPeer = peer;
      file = gncFile;
      
	helper = new GnucashObjectImpl(peer.getInvoiceSlots(), gncFile);
  }

  // Copy-constructor
  public GnucashGenerInvoiceImpl(final GnucashGenerInvoice invc)
  {
      super();

	if (invc.getJwsdpPeer().getInvoiceSlots() == null) {
	    invc.getJwsdpPeer().setInvoiceSlots(new ObjectFactory().createSlotsType());
	}


      this.jwsdpPeer = invc.getJwsdpPeer();
      this.file      = invc.getFile();

	helper = new GnucashObjectImpl(invc.getJwsdpPeer().getInvoiceSlots(), invc.getFile());
	
	for ( GnucashGenerInvoiceEntry entr : invc.getGenerEntries() ) {
	    addGenerEntry(entr);
	}
  }

  // -----------------------------------------------------------------

  /**
   * Examples: The user-defined-attribute "hidden"="true"/"false" was introduced
   * in gnucash2.0 to hide accounts.
   *
   * @param name the name of the user-defined attribute
   * @return the value or null if not set
   */
  public String getUserDefinedAttribute(final String name) {
	return helper.getUserDefinedAttribute(name);
  }

  /**
   * @return all keys that can be used with
   *         ${@link #getUserDefinedAttribute(String)}}.
   */
  public Collection<String> getUserDefinedAttributeKeys() {
	return helper.getUserDefinedAttributeKeys();
  }

  // -----------------------------------------------------------------

  /**
   * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   * @see GnucashGenerInvoice#isNotInvcFullyPaid()
   */
  public boolean isInvcFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return ! isNotInvcFullyPaid();
  }

  /**
   * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   * @see GnucashGenerInvoice#isNotInvcFullyPaid()
   */
  public boolean isNotInvcFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return getInvcAmountWithTaxes().isGreaterThan(getInvcAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
  }
  
  // ------------------------------

  /**
   * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   * @see GnucashGenerInvoice#isNotInvcFullyPaid()
   */
  public boolean isBillFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return ! isNotBillFullyPaid();
  }

  /**
   * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   * @see GnucashGenerInvoice#isNotInvcFullyPaid()
   */
  public boolean isNotBillFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return getBillAmountWithTaxes().isGreaterThan(getBillAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
  }

  // ------------------------------

  /**
   * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   * @see GnucashGenerInvoice#isNotInvcFullyPaid()
   */
  public boolean isVoucherFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return ! isNotVoucherFullyPaid();
  }

  /**
   * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   * @see GnucashGenerInvoice#isNotInvcFullyPaid()
   */
  public boolean isNotVoucherFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return getVoucherAmountWithTaxes().isGreaterThan(getVoucherAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
  }

  // ------------------------------

  /**
   * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   * @see GnucashGenerInvoice#isNotInvcFullyPaid()
   */
  public boolean isJobFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return ! isNotJobFullyPaid();
  }

  /**
   * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   * @see GnucashGenerInvoice#isNotInvcFullyPaid()
   */
  public boolean isNotJobFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return getJobAmountWithTaxes().isGreaterThan(getJobAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
  }

  // -----------------------------------------------------------------


	/**
	 * {@inheritDoc}
	 */
	public void addPayingTransaction(final GnucashTransactionSplit trans) {
		payingTransactions.add(trans.getTransaction());
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTransaction(final GnucashTransaction trans) {
		//

	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<GnucashTransaction> getPayingTransactions() {
		return payingTransactions;
	}

	/**
	 * {@inheritDoc}
	 */
	public GCshID getPostAccountID() {
	    try {
		return new GCshID( jwsdpPeer.getInvoicePostacc().getValue() );
	    } catch ( NullPointerException exc ) {
		return null;
	    }
	}

	/**
	 * {@inheritDoc}
	 */
	public GCshID getPostTransactionID() {
	    try {
		return new GCshID( jwsdpPeer.getInvoicePosttxn().getValue() );
	    } catch ( NullPointerException exc ) {
		return null;
	    }
	}

	/**
	 * {@inheritDoc}
	 */
	public GnucashAccount getPostAccount() {
		if (getPostAccountID() == null) {
			return null;
		}
		return file.getAccountByID(getPostAccountID());
	}
	
	/**
	 * @return the transaction that transferes the money from the customer to
	 *         the account for money you are to get and the one you owe the
	 *         taxes.
	 */
	public GnucashTransaction getPostTransaction() {
		if (getPostTransactionID() == null) {
			return null;
		}
		return file.getTransactionByID(getPostTransactionID());
	}
	
  // -----------------------------------------------------------------

  /**
   * {@inheritDoc}
   * @throws WrongInvoiceTypeException 
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
	@Override
  public FixedPointNumber getInvcAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
  
	if ( getType() != TYPE_CUSTOMER &&
	     getType() != TYPE_JOB )
		    throw new WrongInvoiceTypeException();

  	return ((FixedPointNumber) getInvcAmountWithTaxes().clone()).subtract(getInvcAmountPaidWithTaxes());
  }

  /**
	 * {@inheritDoc}
   * @throws WrongInvoiceTypeException 
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
	 */
	@Override
	public FixedPointNumber getInvcAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {

		if ( getType() != TYPE_CUSTOMER &&
		     getType() != TYPE_JOB )
			    throw new WrongInvoiceTypeException();

      FixedPointNumber takenFromReceivableAccount = new FixedPointNumber();
		for ( GnucashTransaction trx : getPayingTransactions() ) {
			for ( GnucashTransactionSplit split : trx.getSplits() ) {
				if ( split.getAccount().getType() == GnucashAccount.Type.RECEIVABLE ) {
				  if ( ! split.getValue().isPositive() ) { 
				    takenFromReceivableAccount.subtract(split.getValue());
				  }
               }
			} // split
		} // trx

		return takenFromReceivableAccount;
	}

	@Override
	public FixedPointNumber getInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {

	    if ( getType() != TYPE_CUSTOMER &&
		 getType() != TYPE_JOB )
		throw new WrongInvoiceTypeException();

	  FixedPointNumber retval = new FixedPointNumber();
	  
	  for (GnucashGenerInvoiceEntry entry : getGenerEntries()) {
        if ( entry.getType() == getType() ) {
          retval.add(entry.getInvcSumExclTaxes());
        }
	  }
	  
	  return retval;
	}

    /**
   * {@inheritDoc}
   * @throws WrongInvoiceTypeException 
   */
  @Override
  public FixedPointNumber getInvcAmountWithTaxes() throws WrongInvoiceTypeException {
  
	if ( getType() != TYPE_CUSTOMER &&
	     getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

  	FixedPointNumber retval = new FixedPointNumber();
  
  	//TODO: we should sum them without taxes grouped by tax% and
  	//      multiply the sums with the tax% to be calculatng
  	//      correctly
  
  	for (GnucashGenerInvoiceEntry entry : getGenerEntries()) {
      if ( entry.getType() == getType() ) {
  		retval.add(entry.getInvcSumInclTaxes());
      }
  	}
  	
  	return retval;
  }

    /**
   * {@inheritDoc}
   * @throws WrongInvoiceTypeException 
   */
  @Override
  public FixedPointNumber getInvcAmountWithoutTaxes() throws WrongInvoiceTypeException {
  
	if ( getType() != TYPE_CUSTOMER &&
	     getType() != TYPE_JOB )
		    throw new WrongInvoiceTypeException();

  	FixedPointNumber retval = new FixedPointNumber();
  
  	for (GnucashGenerInvoiceEntry entry : getGenerEntries()) {
      if ( entry.getType() == getType() ) {
  		retval.add(entry.getInvcSumExclTaxes());
      }
  	}
  
  	return retval;
  }
  
  // ------------------------------
  
  /**
   * {@inheritDoc}
   * @throws WrongInvoiceTypeException 
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  @Override
 public String getInvcAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
      return this.getCurrencyFormat().format(this.getInvcAmountUnpaidWithTaxes());
  }

  /**
   * {@inheritDoc}
   * @throws WrongInvoiceTypeException 
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  @Override
  public String getInvcAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
    return this.getCurrencyFormat().format(this.getInvcAmountPaidWithTaxes());
  }

  /**
   * {@inheritDoc}
   * @throws WrongInvoiceTypeException 
   */
  @Override
  public String getInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
    return this.getCurrencyFormat().format(this.getInvcAmountPaidWithoutTaxes());
  }

  /**
   * {@inheritDoc}
   * @throws WrongInvoiceTypeException 
   */
  @Override
  public String getInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
      return this.getCurrencyFormat().format(this.getInvcAmountWithTaxes());
  }

  /**
   * {@inheritDoc}
   * @throws WrongInvoiceTypeException 
   */
  @Override
  public String getInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
      return this.getCurrencyFormat().format(this.getInvcAmountWithoutTaxes());
  }

  // -----------------------------------------------------------------

    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public FixedPointNumber getBillAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
    
      // System.err.println("debug: GnucashInvoiceImpl.getAmountUnpaid(): "
      // + "getBillAmountUnpaid()="+getBillAmountWithoutTaxes()+" getBillAmountPaidWithTaxes()="+getAmountPaidWithTaxes() );
    
      return ((FixedPointNumber) getBillAmountWithTaxes().clone()).subtract(getBillAmountPaidWithTaxes());
    }
    
    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public FixedPointNumber getBillAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {

      FixedPointNumber takenFromPayableAccount = new FixedPointNumber();
        for ( GnucashTransaction trx : getPayingTransactions() ) {
            for ( GnucashTransactionSplit split : trx.getSplits() ) {
                if ( split.getAccount().getType() == GnucashAccount.Type.PAYABLE ) {
                  if ( split.getValue().isPositive() ) {
                    takenFromPayableAccount.add(split.getValue());
                  }
                }
            } // split
        } // trx

        //        System.err.println("getBillAmountPaidWithTaxes="+takenFromPayableAccount.doubleValue());

        return takenFromPayableAccount;
    }

    @Override
    public FixedPointNumber getBillAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
      FixedPointNumber retval = new FixedPointNumber();
      
      for (GnucashGenerInvoiceEntry entry : getGenerEntries()) {
        if ( entry.getType() == getType() ) {
          retval.add(entry.getBillSumExclTaxes());
        }
      }
      
      return retval;
    }
    
    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public FixedPointNumber getBillAmountWithTaxes() throws WrongInvoiceTypeException {
    
      FixedPointNumber retval = new FixedPointNumber();
    
      //TODO: we should sum them without taxes grouped by tax% and
      //      multiply the sums with the tax% to be calculatng
      //      correctly
    
      for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
        if ( entry.getType() == getType() ) {
          retval.add(entry.getBillSumInclTaxes());
        }
      }
      
      return retval;
    }

      /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public FixedPointNumber getBillAmountWithoutTaxes() throws WrongInvoiceTypeException {
    
      FixedPointNumber retval = new FixedPointNumber();
    
      for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
        if ( entry.getType() == getType() ) {
          retval.add(entry.getBillSumExclTaxes());
        }
      }
    
      return retval;
    }

    // ------------------------------
    
    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getBillAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
        return this.getCurrencyFormat().format(this.getBillAmountUnpaidWithTaxes());
    }

    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getBillAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
      return this.getCurrencyFormat().format(this.getBillAmountPaidWithTaxes());
    }

    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public String getBillAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
      return this.getCurrencyFormat().format(this.getBillAmountPaidWithoutTaxes());
    }

    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public String getBillAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
        return this.getCurrencyFormat().format(this.getBillAmountWithTaxes());
    }

    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public String getBillAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
        return this.getCurrencyFormat().format(this.getBillAmountWithoutTaxes());
    }

    // -----------------------------------------------------------------

    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public FixedPointNumber getVoucherAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
    
      // System.err.println("debug: GnucashInvoiceImpl.getAmountUnpaid(): "
      // + "getVoucherAmountUnpaid()="+getVoucherAmountWithoutTaxes()+" getVoucherAmountPaidWithTaxes()="+getAmountPaidWithTaxes() );
    
      return ((FixedPointNumber) getVoucherAmountWithTaxes().clone()).subtract(getVoucherAmountPaidWithTaxes());
    }
    
    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public FixedPointNumber getVoucherAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {

      FixedPointNumber takenFromPayableAccount = new FixedPointNumber();
        for ( GnucashTransaction trx : getPayingTransactions() ) {
            for ( GnucashTransactionSplit split : trx.getSplits() ) {
                if ( split.getAccount().getType() == GnucashAccount.Type.PAYABLE ) {
                  if ( split.getValue().isPositive() ) {
                    takenFromPayableAccount.add(split.getValue());
                  }
                }
            } // split
        } // trx

        //        System.err.println("getVoucherAmountPaidWithTaxes="+takenFromPayableAccount.doubleValue());

        return takenFromPayableAccount;
    }

    @Override
    public FixedPointNumber getVoucherAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
      FixedPointNumber retval = new FixedPointNumber();
      
      for (GnucashGenerInvoiceEntry entry : getGenerEntries()) {
        if ( entry.getType() == getType() ) {
          retval.add(entry.getVoucherSumExclTaxes());
        }
      }
      
      return retval;
    }
    
    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public FixedPointNumber getVoucherAmountWithTaxes() throws WrongInvoiceTypeException {
    
      FixedPointNumber retval = new FixedPointNumber();
    
      //TODO: we should sum them without taxes grouped by tax% and
      //      multiply the sums with the tax% to be calculatng
      //      correctly
    
      for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
        if ( entry.getType() == getType() ) {
          retval.add(entry.getVoucherSumInclTaxes());
        }
      }
      
      return retval;
    }

      /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public FixedPointNumber getVoucherAmountWithoutTaxes() throws WrongInvoiceTypeException {
    
      FixedPointNumber retval = new FixedPointNumber();
    
      for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
        if ( entry.getType() == getType() ) {
          retval.add(entry.getVoucherSumExclTaxes());
        }
      }
    
      return retval;
    }

    // ------------------------------
    
    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getVoucherAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
        return this.getCurrencyFormat().format(this.getVoucherAmountUnpaidWithTaxes());
    }

    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getVoucherAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
      return this.getCurrencyFormat().format(this.getVoucherAmountPaidWithTaxes());
    }

    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public String getVoucherAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
      return this.getCurrencyFormat().format(this.getVoucherAmountPaidWithoutTaxes());
    }

    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public String getVoucherAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
        return this.getCurrencyFormat().format(this.getVoucherAmountWithTaxes());
    }

    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public String getVoucherAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
        return this.getCurrencyFormat().format(this.getVoucherAmountWithoutTaxes());
    }

    // ---------------------------------------------------------------

    /**
     * @return what the customer must still pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public FixedPointNumber getJobAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();
	
	    GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	    if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_CUSTOMER) )
		return getInvcAmountUnpaidWithTaxes();
	    else if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_VENDOR) )
		return getBillAmountUnpaidWithTaxes();
	    
	    return null; // Compiler happy
    }

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public FixedPointNumber getJobAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();
	
	    GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	    if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_CUSTOMER) )
		return getInvcAmountPaidWithTaxes();
	    else if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_VENDOR) )
		return getBillAmountPaidWithTaxes();
	    
	    return null; // Compiler happy
    }

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public FixedPointNumber getJobAmountPaidWithoutTaxes() throws WrongInvoiceTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();
	
	    GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	    if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_CUSTOMER) )
		return getInvcAmountPaidWithoutTaxes();
	    else if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_VENDOR) )
		return getBillAmountPaidWithoutTaxes();
	    
	    return null; // Compiler happy
    }

    /**
     * @return what the customer needs to pay in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public FixedPointNumber getJobAmountWithTaxes() throws WrongInvoiceTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();
	
	    GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	    if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_CUSTOMER) )
		return getInvcAmountWithTaxes();
	    else if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_VENDOR) )
		return getBillAmountWithTaxes();
	    
	    return null; // Compiler happy
    }

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public FixedPointNumber getJobAmountWithoutTaxes() throws WrongInvoiceTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();
	
	    GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	    if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_CUSTOMER) )
		return getInvcAmountWithoutTaxes();
	    else if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_VENDOR) )
		return getBillAmountWithoutTaxes();
	    
	    return null; // Compiler happy
    }

// ----------------------------

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer must still pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getJobAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	      return this.getCurrencyFormat().format(this.getJobAmountUnpaidWithTaxes());
    }

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getJobAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	      return this.getCurrencyFormat().format(this.getJobAmountPaidWithTaxes());
    }

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getJobAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException {
	return this.getCurrencyFormat().format(this.getJobAmountPaidWithoutTaxes());
    }

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer needs to pay in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getJobAmountWithTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException {
	return this.getCurrencyFormat().format(this.getJobAmountWithTaxes());
    }

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getJobAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException {
	return this.getCurrencyFormat().format(this.getJobAmountWithoutTaxes());
    }

    // -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 * @throws WrongInvoiceTypeException 
	 */
    @Override
	public GCshTaxedSumImpl[] getInvcTaxes() throws WrongInvoiceTypeException {

		if ( getType() != TYPE_CUSTOMER &&
		     getType() != TYPE_JOB )
		    throw new WrongInvoiceTypeException();

		List<GCshTaxedSumImpl> taxedSums = new ArrayList<GCshTaxedSumImpl>();

		invoiceentries:
		for (GnucashGenerInvoiceEntry entry : getGenerEntries()) {
          if ( entry.getType() == getType() ) {
			FixedPointNumber taxPerc = entry.getInvcApplicableTaxPercent();

			for (GCshTaxedSumImpl taxedSum2 : taxedSums) {
				GCshTaxedSumImpl taxedSum = taxedSum2;
				if (taxedSum.getTaxpercent().equals(taxPerc)) {
					taxedSum.setTaxsum(
							taxedSum.getTaxsum().add(
									entry.getInvcSumInclTaxes().subtract(entry.getInvcSumExclTaxes())
							)
					);
					continue invoiceentries;
				}
			}

			GCshTaxedSumImpl taxedSum = new GCshTaxedSumImpl(taxPerc, entry.getInvcSumInclTaxes().subtract(entry.getInvcSumExclTaxes()));
			taxedSums.add(taxedSum);
          } // type
		} // for

		return taxedSums.toArray(new GCshTaxedSumImpl[taxedSums.size()]);

	}

    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public GCshTaxedSumImpl[] getBillTaxes() throws WrongInvoiceTypeException {

	if ( getType() != TYPE_VENDOR &&
	     getType() != TYPE_JOB )
		    throw new WrongInvoiceTypeException();

        List<GCshTaxedSumImpl> taxedSums = new ArrayList<GCshTaxedSumImpl>();

        invoiceentries:
        for (GnucashGenerInvoiceEntry entry : getGenerEntries()) {
          if ( entry.getType() == getType() ) {
            FixedPointNumber taxPerc = entry.getBillApplicableTaxPercent();

            for (GCshTaxedSumImpl taxedSum2 : taxedSums) {
                GCshTaxedSumImpl taxedSum = taxedSum2;
                if (taxedSum.getTaxpercent().equals(taxPerc)) {
                    taxedSum.setTaxsum(
                            taxedSum.getTaxsum().add(
                                    entry.getBillSumInclTaxes().subtract(entry.getBillSumExclTaxes())
                            )
                    );
                    continue invoiceentries;
                }
            }

            GCshTaxedSumImpl taxedSum = new GCshTaxedSumImpl(taxPerc, entry.getBillSumInclTaxes().subtract(entry.getBillSumExclTaxes()));
            taxedSums.add(taxedSum);
          } // type
        } // for

        return taxedSums.toArray(new GCshTaxedSumImpl[taxedSums.size()]);
    }
    
    /**
     * {@inheritDoc}
     * @throws WrongInvoiceTypeException 
     */
    @Override
    public GCshTaxedSumImpl[] getVoucherTaxes() throws WrongInvoiceTypeException {

	if ( getType() != TYPE_EMPLOYEE &&
	     getType() != TYPE_JOB )
		    throw new WrongInvoiceTypeException();

        List<GCshTaxedSumImpl> taxedSums = new ArrayList<GCshTaxedSumImpl>();

        invoiceentries:
        for (GnucashGenerInvoiceEntry entry : getGenerEntries()) {
          if ( entry.getType() == getType() ) {
            FixedPointNumber taxPerc = entry.getVoucherApplicableTaxPercent();

            for (GCshTaxedSumImpl taxedSum2 : taxedSums) {
                GCshTaxedSumImpl taxedSum = taxedSum2;
                if (taxedSum.getTaxpercent().equals(taxPerc)) {
                    taxedSum.setTaxsum(
                            taxedSum.getTaxsum().add(
                                    entry.getVoucherSumInclTaxes().subtract(entry.getVoucherSumExclTaxes())
                            )
                    );
                    continue invoiceentries;
                }
            }

            GCshTaxedSumImpl taxedSum = new GCshTaxedSumImpl(taxPerc, entry.getVoucherSumInclTaxes().subtract(entry.getBillSumExclTaxes()));
            taxedSums.add(taxedSum);
          } // type
        } // for

        return taxedSums.toArray(new GCshTaxedSumImpl[taxedSums.size()]);
    }
    
    /**
    *
    * @return For a vendor bill: How much sales-taxes are to pay.
    * @throws WrongInvoiceTypeException
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
    * @see GCshTaxedSumImpl
    */
    GCshTaxedSumImpl[] getJobTaxes() throws WrongInvoiceTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	if ( jobInvc.getJobType() == GCshOwner.Type.CUSTOMER )
	    return getInvcTaxes();
	else if ( jobInvc.getJobType() == GCshOwner.Type.VENDOR )
	    return getBillTaxes();
	else if ( jobInvc.getJobType() == GCshOwner.Type.EMPLOYEE )
	    return getVoucherTaxes();

	return null; // Compiler happy
    }
    
    // ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public GCshID getID() {
		return new GCshID(getJwsdpPeer().getInvoiceGuid().getValue());
	}

    /**
     * {@inheritDoc}
     */
    public GCshOwner.Type getType() {
        return GCshOwner.Type.valueOff( getJwsdpPeer().getInvoiceOwner().getOwnerType() );
    }

    @Deprecated
    public String getTypeStr() {
        return getJwsdpPeer().getInvoiceOwner().getOwnerType();
    }

	/**
	 * {@inheritDoc}
	 */
	public GCshID getLotID() {
		if (getJwsdpPeer().getInvoicePostlot() == null) {
			return null; //unposted invoices have no postlot
		}
		return new GCshID( getJwsdpPeer().getInvoicePostlot().getValue() );
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return getJwsdpPeer().getInvoiceNotes();
	}
	
	// ----------------------------

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("exports")
    public GncGncInvoice getJwsdpPeer() {
        return jwsdpPeer;
    }

	/**
	 * {@inheritDoc}
	 */
	public GnucashFile getFile() {
		return file;
	}

    // ----------------------------

    /**
     * {@inheritDoc}
     */
    public GnucashGenerInvoiceEntry getGenerEntryByID(final GCshID id) {
        for (GnucashGenerInvoiceEntry element : getGenerEntries()) {
            if (element.getID().equals(id)) {
                return element;
            }

        }
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
	public Collection<GnucashGenerInvoiceEntry> getGenerEntries() {
	    return entries;
	}

    /**
     * {@inheritDoc}
     */
    public void addGenerEntry(final GnucashGenerInvoiceEntry entry) {
        if (!entries.contains(entry)) {
            entries.add(new GnucashGenerInvoiceEntryImpl(entry));
        }
    }

	/**
	 * {@inheritDoc}
	 */
	public ZonedDateTime getDateOpened() {
		if (dateOpened == null) {
			String dateStr = getJwsdpPeer().getInvoiceOpened().getTsDate();
			try {
				//"2001-09-18 00:00:00 +0200"
				dateOpened = ZonedDateTime.parse(dateStr, DATE_OPENED_FORMAT);
			}
			catch (Exception e) {
				IllegalStateException ex = new IllegalStateException(
						"unparsable date '"
								+ dateStr
								+ "' in invoice!");
				ex.initCause(e);
				throw ex;
			}

		}
		return dateOpened;
	}

	/**
	 * @see #getDateOpenedFormatted()
	 * @see #getDatePostedFormatted()
	 * @return the Dateformat to use.
	 */
	protected DateFormat getDateFormat() {
		if (dateFormat == null) {
			dateFormat = DateFormat.getDateInstance();
		}

		return dateFormat;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDateOpenedFormatted() {
		return getDateFormat().format(getDateOpened());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDatePostedFormatted() {
		return getDateFormat().format(getDatePosted());
	}

	/**
	 * {@inheritDoc}
	 */
	public ZonedDateTime getDatePosted() {
	    if (datePosted == null) {
		String dateStr = getJwsdpPeer().getInvoiceOpened().getTsDate();
		try {
		    // "2001-09-18 00:00:00 +0200"
		    datePosted = ZonedDateTime.parse(dateStr, DATE_OPENED_FORMAT);
		} catch (Exception e) {
		    IllegalStateException ex = new IllegalStateException(
			    "unparsable date '" + dateStr + "' in invoice entry!");
		    ex.initCause(e);
		    throw ex;
		}

	    }
	    return datePosted;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getNumber() {
		return getJwsdpPeer().getInvoiceId();
	}
	
	// -----------------------------------------------------------

	public GCshID getOwnerID() {
	    return getOwnerId_direct();
	}

    public GCshID getOwnerID(ReadVariant readVar) throws WrongInvoiceTypeException {
      if ( readVar == ReadVariant.DIRECT )
        return getOwnerId_direct();
      else if ( readVar == ReadVariant.VIA_JOB )
        return getOwnerId_viaJob();
      
      return null; // Compiler happy
    }

    protected GCshID getOwnerId_direct() {
      assert getJwsdpPeer().getInvoiceOwner().getOwnerId().getType().equals(Const.XML_DATA_TYPE_GUID);
        return new GCshID( getJwsdpPeer().getInvoiceOwner().getOwnerId().getValue() );
    }

    protected GCshID getOwnerId_viaJob() throws WrongInvoiceTypeException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();
	
	GnucashGenerJob job = file.getGenerJobByID(getOwnerID());
	return job.getOwnerID();
    }
    
    // ----------------------------

    @Override
    public GCshOwner.Type getOwnerType(ReadVariant readVar) throws WrongInvoiceTypeException {
	if (readVar == ReadVariant.DIRECT)
	    return getOwnerType_direct();
	else if (readVar == ReadVariant.VIA_JOB)
	    return getOwnerType_viaJob();

	return null; // Compiler happy
    }

    public GCshOwner.Type getOwnerType_direct() {
	return GCshOwner.Type.valueOff( getJwsdpPeer().getInvoiceOwner().getOwnerType() );
    }

    @Deprecated
    public String getOwnerType_directStr() {
	return getJwsdpPeer().getInvoiceOwner().getOwnerType();
    }

    protected GCshOwner.Type getOwnerType_viaJob() throws WrongInvoiceTypeException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	GnucashGenerJob job = file.getGenerJobByID(getOwnerID());
	return job.getOwnerType();
    }
    
	// -----------------------------------------------------------
    
    @Override
    public String getURL() {
	return getUserDefinedAttribute(Const.SLOT_KEY_ASSOC_URI);
    }

	// -----------------------------------------------------------

	/**
	 * sorts primarily on the date the transaction happened
	 * and secondarily on the date it was entered.
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * @param o invoice to compare with
	 * @return -1 0 or 1
	 */
	public int compareTo(final GnucashGenerInvoice otherInvc) {
		try {
			int compare = otherInvc.getDatePosted().compareTo(getDatePosted());
			if (compare != 0) {
				return compare;
			}

			return otherInvc.getDateOpened().compareTo(getDateOpened());
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnucashGenerInvoiceImpl [");
		
        buffer.append("id=");
        buffer.append(getID());
        
        buffer.append(", owner-id=");
        buffer.append(getOwnerID());
        
        buffer.append(", owner-type (dir.)=");
        try {
	    buffer.append(getOwnerType(ReadVariant.DIRECT));
	} catch (WrongInvoiceTypeException e) {
	    // TODO Auto-generated catch block
	    buffer.append("ERROR");
	}
        
		buffer.append(", number='");
		buffer.append(getNumber() + "'");
		
		buffer.append(", description='");
		buffer.append(getDescription() + "'");
		
		buffer.append(", #entries=");
		buffer.append(entries.size());
		
		buffer.append(", date-opened=");
		try {
		  buffer.append(getDateOpened().toLocalDate().format(DATE_OPENED_FORMAT_PRINT));
		}
		catch (Exception e) {
          buffer.append(getDateOpened().toLocalDate().toString());
		}
		
		buffer.append("]");
		return buffer.toString();
	}
	
	// ---------------------------------------------------------------

	/**
	 *
	 * @return the currency-format to use if no locale is given.
	 */
	protected NumberFormat getCurrencyFormat() {
		if (currencyFormat == null) {
			currencyFormat = NumberFormat.getCurrencyInstance();
		}

		return currencyFormat;
	}

	@SuppressWarnings("exports")
	@Override
	public InvoiceOwner getOwnerPeerObj() {
	    return jwsdpPeer.getInvoiceOwner();
	}

}
