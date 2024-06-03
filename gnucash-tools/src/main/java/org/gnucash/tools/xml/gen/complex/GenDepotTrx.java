package org.gnucash.tools.xml.gen.complex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.apiext.secacct.SecuritiesAccountTransactionManager;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.tuples.AcctIDAmountPair;
import org.gnucash.tools.CommandLineTool;
import org.gnucash.tools.xml.helper.CmdLineHelper;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.Helper;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;
import xyz.schnorxoborx.base.dateutils.DateHelpers;
import xyz.schnorxoborx.base.dateutils.LocalDateHelpers;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenDepotTrx extends CommandLineTool
{
  enum BookMode {
	  SINGLE_TRX,
	  LISTFILE
  }

  // -----------------------------------------------------------------

  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GenDepotTrx.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  // ------------------------------

  private static BookMode         mode = null; 
  
  private static String           gcshInFileName = null;
  private static String           gcshOutFileName = null;
  private static GnuCashWritableFileImpl gcshFile = null;
		  
  private static String           bookingListFileName = null;
  
  // ------------------------------
  // BEGIN Core parameters

  private static SecuritiesAccountTransactionManager.Type type = null;
  
  // CAUTION: The following account IDs are all of type
  // GCshAcctID. Why not GCshComplAcctID? Yes, that would work
  // as well, but we never book to the special top-level 
  // accounts. Thus, this is a precautionary measure.
  private static GCshID            stockAcctID = null;
  private static GCshID            incomeAcctID = null;
  private static Collection<AcctIDAmountPair> expensesAcctAmtList = null;
  private static GCshID            offsetAcctID = null;
  
  private static FixedPointNumber  nofStocks = null;
  private static FixedPointNumber  stockPrc = null;
  private static FixedPointNumber  divGross = null;
  private static FixedPointNumber  stockSplitFactor = null;
  
  private static Helper.DateFormat dateFormat = null;
  private static LocalDate         datPst = null;
  private static String            descr = null;

  // END Core parameters
  // ------------------------------

  // batch-mode:
  private static boolean    silent           = false;
  private static boolean    batch            = false;
  private static String     batchOutFileName = null;
  
  // -----------------------------------------------------------------

  // ::MAGIC
  private final static String COMMENT_TOKEN = "#";
  private final static String SEPARATOR     = "§"; // sic, not colon, not comma, not pipe  

  // -----------------------------------------------------------------

  public static void main( String[] args )
  {
    try
    {
      GenDepotTrx tool = new GenDepotTrx ();
      tool.execute(args);
    }
    catch (CouldNotExecuteException exc) 
    {
      System.err.println("Execution exception. Aborting.");
      exc.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  protected void init() throws Exception
  {
    datPst = LocalDateHelpers.parseLocalDate(DateHelpers.DATE_UNSET);

    // cfg = new PropertiesConfiguration(System.getProperty("config"));
    // getConfigSettings(cfg);

    // Options
    // The essential ones
    Option optMode = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("mode")
      .withDescription("Booking mode")
      .withLongOpt("book-mode")
      .create("m");
    	        
    Option optFileIn = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("file")
      .withDescription("GnuCash file (in)")
      .withLongOpt("gnucash-in-file")
      .create("if");
        
    Option optFileOut = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("file")
      .withDescription("GnuCash file (out)")
      .withLongOpt("gnucash-out-file")
      .create("of");
    
    Option optListFile = OptionBuilder
      .hasArg()
      .withArgName("file")
      .withDescription("List file for bookings")
      .withLongOpt("booking-list-file")
      .create("blf");
    	    	        
    // ----------------------------
    // BEGIN Core parameters
        
    Option optType = OptionBuilder
      .hasArg()
      .withArgName("type")
      .withDescription("Transaction type")
      .withLongOpt("type")
      .create("tp");
    	      
    Option optStockAcct = OptionBuilder
      .hasArg()
      .withArgName("acctid")
      .withDescription("Account-ID of stock account")
      .withLongOpt("stock-account-id")
      .create("stacct");
      
    Option optIncomeAcct = OptionBuilder
      .hasArg()
      .withArgName("acctid")
      .withDescription("Account-ID for (dividend) income")
      .withLongOpt("income-account-id")
      .create("inacct");
    	      
    Option optExpensesAcctAmtList = OptionBuilder
      .hasArg()
      .withArgName("pair-list")
      .withDescription("Account-ID/amount pairs for expenses (taxes and fees), " + 
                       "list separated by '|', pairs separated by ';'")
      .withLongOpt("expense-account-amounts")
      .create("eaa");
      
    Option optOffsetAcct = OptionBuilder
      .hasArg()
      .withArgName("acctid")
      .withDescription("Account-ID of offsetting account")
      .withLongOpt("offset-account-id")
      .create("osacct");
    	      
    // ---
    
    Option optNofStocks = OptionBuilder
      .hasArg()
      .withArgName("number")
      .withDescription("Number of stocks to buy/sell")
      .withLongOpt("nof-stocks")
      .create("n");
    	                
    Option optStockPrice = OptionBuilder
      .hasArg()
      .withArgName("amount")
      .withDescription("Stock price")
      .withLongOpt("stock-price")
      .create("p");
              
    Option optDividend = OptionBuilder
      .hasArg()
      .withArgName("amount")
      .withDescription("Gross dividend")
      .withLongOpt("dividend-gross")
      .create("divgr");

    Option optSpltFact = OptionBuilder
      .hasArg()
      .withArgName("amount")
      .withDescription("Stock split factor")
      .withLongOpt("stock-split-factor")
      .create("fct");

    // ---
    
    Option optDatePosted = OptionBuilder
      .hasArg()
      .withArgName("datetime")
      .withDescription("Date posted")
      .withLongOpt("date-posted")
      .create("dtp");
            
    // The convenient ones
    Option optDateFormat = OptionBuilder
      .hasArg()
      .withArgName("date-format")
      .withDescription("Date format")
      .withLongOpt("date-format")
      .create("df");

    Option optDescr = OptionBuilder
      .hasArg()
      .withArgName("descr")
      .withDescription("Description")
      .withLongOpt("description")
      .create("dscr");
              
    // END Core parameters
    // ----------------------------

    Option optSilent = OptionBuilder
      .withDescription("Silent mode")
      .withLongOpt("silent")
      .create("sl");

    Option optBatch = OptionBuilder
   	  .withDescription("Batch mode")
   	  .withLongOpt("batch")
   	  .create("ba");

    Option optOutFile = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Out-file name (for batch mode)")
      .withLongOpt("batch-out-file")
      .create("bof");
    
    // ---
    	    
    options = new Options();
    options.addOption(optMode);
    options.addOption(optFileIn);
    options.addOption(optFileOut);
    options.addOption(optListFile);
    
    options.addOption(optType);
    options.addOption(optStockAcct);
    options.addOption(optIncomeAcct);
    options.addOption(optExpensesAcctAmtList);
    options.addOption(optOffsetAcct);
    options.addOption(optNofStocks);
    options.addOption(optStockPrice);
    options.addOption(optDividend);
    options.addOption(optSpltFact);
    options.addOption(optDatePosted);
    options.addOption(optDateFormat);
    options.addOption(optDescr);
    
    options.addOption(optSilent);
    options.addOption(optBatch);
    options.addOption(optOutFile);
  }

  @Override
  protected void getConfigSettings(PropertiesConfiguration cs) throws Exception
  {
    // ::EMPTY
  }
  
  @Override
  protected void kernel() throws Exception
  {
    gcshFile = new GnuCashWritableFileImpl(new File(gcshInFileName));
    
    if ( mode == BookMode.SINGLE_TRX )
    {
    	bookSingleTrx();
    }
    else if ( mode == BookMode.LISTFILE )
    {
    	bookListFile();
    }
  }

  private void bookSingleTrx() throws IOException
  {
	  BufferedWriter outFile = null;
	  if ( batch )
	  {
		  try 
		  {
			  outFile = new BufferedWriter(new FileWriter(batchOutFileName));
		  } 
		  catch ( Exception exc )
		  {
			  System.err.println("Could not open batch-out-file '" + batchOutFileName + "'");
			  System.err.println("Aborting");
			  System.exit( 1 );
		  }
	  }

	  // ---

	  bookSingleTrxPrep();
	  GCshID newID = bookSingleTrxCore(outFile);
	  
	  // ---

	  gcshFile.writeFile(new File(gcshOutFileName));
		    
	  if ( ! silent )
		  System.out.println("OK");
		    
	  if ( batch )
		  outFile.close();
  }
  
  private void bookListFile() throws IOException, InvalidCommandLineArgsException
  {
	  ArrayList<ParamTuple> paramTupleList = new ArrayList<ParamTuple>();
	  
	  try
	  {
		  readListFile(paramTupleList);
		  System.out.println("Params: ");
//		  for ( ParamTuple elt : paramTupleList )
//			  System.out.println(" - " + elt.toString());
	  }
	  catch ( Exception exc )
	  {
		  System.err.println("Could not parse list file ");
		  LOGGER.error("Could not parse list file ");
		  return;
	  }
	  
	  BufferedWriter outFile = null;
	  if ( batch )
	  {
		  try 
		  {
			  outFile = new BufferedWriter(new FileWriter(batchOutFileName));
		  } 
		  catch ( Exception exc )
		  {
			  System.err.println("Could not open batch-out-file '" + batchOutFileName + "'");
			  LOGGER.error("Could not open batch-out-file '" + batchOutFileName + "'");
			  return;
		  }
	  }

	  // ---

	  bookListFileCore( paramTupleList, outFile );
	  
	  // ---

	  gcshFile.writeFile(new File(gcshOutFileName));
		    
	  if ( ! silent )
		  System.out.println("OK");

	  if ( batch )
		  outFile.close();
  }

  private void bookSingleTrxPrep() throws IOException
  {
	GnuCashAccount stockAcct = gcshFile.getAccountByID(stockAcctID);
	if ( stockAcct == null )
	{
		System.err.println("Error: Cannot get account with ID '" + stockAcctID + "'");
		LOGGER.debug("Error: Cannot get account with ID '" + stockAcctID + "'");
	}
	
	GnuCashAccount incomeAcct = null;
	if ( incomeAcctID != null )
	{
		incomeAcct = gcshFile.getAccountByID(incomeAcctID);
		if ( incomeAcct == null )
		{
			System.err.println("Error: Cannot get account with ID '" + incomeAcctID + "'");
			LOGGER.debug("Error: Cannot get account with ID '" + incomeAcctID + "'");
		}
	}
	
	for ( AcctIDAmountPair elt : expensesAcctAmtList )
	{
		GnuCashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
		if ( expensesAcct == null )
		{
			System.err.println("Error: Cannot get account with ID '" + elt.accountID() + "'");
			LOGGER.debug("Error: Cannot get account with ID '" + elt.accountID() + "'");
		}
	}
	
	GnuCashAccount offsetAcct = null;
	if ( type == SecuritiesAccountTransactionManager.Type.BUY_STOCK || 
		 type == SecuritiesAccountTransactionManager.Type.DIVIDEND )
	{
		offsetAcct = gcshFile.getAccountByID(offsetAcctID);
		if ( offsetAcct == null )
		{
			System.err.println("Error: Cannot get account with ID '" + offsetAcctID + "'");
			LOGGER.debug("Error: Cannot get account with ID '" + offsetAcctID + "'");
		}
	}

	System.err.println("Account 1 name (stock):      '" + stockAcct.getQualifiedName() + "'");
	if ( incomeAcctID != null )
	{
		System.err.println("Account 2 name (income):     '" + incomeAcct.getQualifiedName() + "'");
		LOGGER.debug("Account 2 name (income):     '" + incomeAcct.getQualifiedName() + "'");
	}

	int counter = 1;
	for ( AcctIDAmountPair elt : expensesAcctAmtList )
	{
		GnuCashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
		System.err.println("Account 3." + counter + " name (expenses): '" + expensesAcct.getQualifiedName() + "'");
		counter++;
	}
	
	if ( type == SecuritiesAccountTransactionManager.Type.BUY_STOCK || 
		 type == SecuritiesAccountTransactionManager.Type.DIVIDEND )
	{
		System.err.println("Account 4 name (offsetting): '" + offsetAcct.getQualifiedName() + "'");
		LOGGER.debug("Account 4 name (offsetting): '" + offsetAcct.getQualifiedName() + "'");
	}
  }
  
  private GCshID bookSingleTrxCore(BufferedWriter outFile) throws IOException
  {
    GnuCashWritableTransaction trx = null;
	if ( type == SecuritiesAccountTransactionManager.Type.BUY_STOCK ) 
	{
	    trx = SecuritiesAccountTransactionManager.
	    		genBuyStockTrx(gcshFile, 
	    					   stockAcctID, expensesAcctAmtList, offsetAcctID,
	    					   nofStocks, stockPrc,
	    					   datPst, descr);
	} 
	else if ( type == SecuritiesAccountTransactionManager.Type.DIVIDEND ) 
	{
	    trx = SecuritiesAccountTransactionManager.
	    		genDivivendTrx(gcshFile, 
	    					   stockAcctID, incomeAcctID, expensesAcctAmtList, offsetAcctID,
	    					   divGross,
	    					   datPst, descr);
	}
	else if ( type == SecuritiesAccountTransactionManager.Type.STOCK_SPLIT ) 
	{
	    trx = SecuritiesAccountTransactionManager.
	    		genStockSplitTrx_factor(gcshFile, 
	    					     		stockAcctID,
	    					     		stockSplitFactor,
	    					     		datPst, descr);
	}
    
	// ---
	
    if ( ! silent )
    	System.out.println("Transaction to write: " + trx.toString());

    GCshID newID = trx.getID();
    LOGGER.info( "Generated new Transaction: " + newID);

    if ( batch )
    {
    	try 
    	{
    		outFile.write("" + newID + "\n");
    	} 
    	catch ( Exception exc )
    	{
    		System.err.println("Could not write Transaction ID into batch-out-file");
    		LOGGER.error("Could not write Transaction ID into batch-out-file");
    	}
    }
	  
	// ---
	
    return newID;
  }
  
  private void readListFile(ArrayList<ParamTuple> paramTuples) throws IOException
  {
      BufferedReader br = new BufferedReader(new FileReader(bookingListFileName));

      String zeile;
      while ( (zeile = br.readLine()) != null ) 
      {
        if ( zeile.startsWith(COMMENT_TOKEN) )
          continue;
        
        String[] eintrag = zeile.split(SEPARATOR);
        ParamTuple newTuple = new ParamTuple(eintrag[0], eintrag[1], eintrag[2],
        									 eintrag[3], eintrag[4], eintrag[5],
        									 eintrag[6], eintrag[7], eintrag[8],
        									 eintrag[9], eintrag[10], eintrag[11]);
        
        paramTuples.add(newTuple);
      }
  }

  private void bookListFileCore(ArrayList<ParamTuple> paramTupleList, BufferedWriter outFile)
  {
	  GCshID newID = null;
	  int lineNo = 1;
	  for ( ParamTuple tuple : paramTupleList )
	  {
		  System.out.println( "------------------------------------------------" );
		  System.out.println( "Line no. " + lineNo + ": " );
		  LOGGER.info( "Line no. " + lineNo + ": " + tuple.toString() );
		  
		  try
		  {
			  parseCoreParams( tuple );
		  }
		  catch ( InvalidCommandLineArgsException exc )
		  {
			  System.err.println("Could not validate set of params, line no. " + lineNo);
			  LOGGER.error("Could not validate set of params, line no. " + lineNo);
			  lineNo++;
			  continue;
		  }
		  
		  try
		  {
			  bookSingleTrxPrep();
			  newID = bookSingleTrxCore(outFile);
		  }
		  catch ( Exception exc )
		  {
			  System.err.println("Could not book set of params, line no. " + lineNo);
			  LOGGER.error("Could not book set of params, line no. " + lineNo);
			  lineNo++;
			  continue;
		  }
		  
		  lineNo++;
	  }
  }
  
  // -----------------------------------------------------------------

  @Override
  protected void parseCommandLineArgs(String[] args) throws InvalidCommandLineArgsException
  {
    CommandLineParser parser = new GnuParser();
    CommandLine cmdLine = null;
    try
    {
      cmdLine = parser.parse(options, args);
    }
    catch (ParseException exc)
    {
      System.err.println("Parsing options failed. Reason: " + exc.getMessage());
    }

    // ---

    // <silent>
    if (cmdLine.hasOption("silent"))
    {
      silent = true;
    }
    else
    {
      silent = false;
    }
    if (! silent)
      System.err.println("silent:              " + silent);
    
    // ---

    // <book-mode>
    try
    {
      mode = BookMode.valueOf( cmdLine.getOptionValue("book-mode") );
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <book-mode>");
      throw new InvalidCommandLineArgsException();
    }
    if (! silent)
    	System.err.println("Book mode: " + mode);
    
    // <gnucash-in-file>
    try
    {
      gcshInFileName = cmdLine.getOptionValue("gnucash-in-file");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <gnucash-in-file>");
      throw new InvalidCommandLineArgsException();
    }
    if (! silent)
    	System.err.println("GnuCash file (in):  '" + gcshInFileName + "'");
    
    // <gnucash-out-file>
    try
    {
      gcshOutFileName = cmdLine.getOptionValue("gnucash-out-file");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <gnucash-out-file>");
      throw new InvalidCommandLineArgsException();
    }
    if (! silent)
    	System.err.println("GnuCash file (out): '" + gcshOutFileName + "'");
    
    // <booking-list-file>
    if ( cmdLine.hasOption( "booking-list-file" ) )
    {
    	if ( mode != BookMode.LISTFILE )
    	{
            System.err.println("<booking-list-file> may only be set with <mode> = '" + BookMode.LISTFILE + "'");
            throw new InvalidCommandLineArgsException();
    	}
    	
        try
        {
        	bookingListFileName = cmdLine.getOptionValue("booking-list-file");
        }
        catch ( Exception exc )
        {
          System.err.println("Could not parse <booking-list-file>");
          throw new InvalidCommandLineArgsException();
        }
    }
    else
    {
    	if ( mode == BookMode.LISTFILE )
    	{
            System.err.println("<booking-list-file> must be set with <mode> = '" + BookMode.LISTFILE + "'");
            throw new InvalidCommandLineArgsException();
    	}
    }
    if (! silent)
    	System.err.println("Booking list file:   '" + bookingListFileName + "'");
    
    // ----------------------------
    // BEGIN Core parameters
    
    if ( mode == BookMode.SINGLE_TRX )
    {
        ParamTuple tuple = new ParamTuple();
        
        if ( cmdLine.hasOption( "type" ) )
        	tuple.type = cmdLine.getOptionValue( "type" );

        if ( cmdLine.hasOption( "stock-account-id" ) )
        	tuple.stockAcctID = cmdLine.getOptionValue( "stock-account-id" );
        
        if ( cmdLine.hasOption( "income-account-id" ) )
        	tuple.incomeAcctID = cmdLine.getOptionValue( "income-account-id" );
        
        if ( cmdLine.hasOption( "expense-account-amounts" ) )
        	tuple.expensesAcctAmtList = cmdLine.getOptionValue( "expense-account-amounts" );
        
        if ( cmdLine.hasOption( "offset-account-id" ) )
        	tuple.offsetAcctID = cmdLine.getOptionValue( "offset-account-id" );
        
        if ( cmdLine.hasOption( "nof-stocks" ) )
        	tuple.nofStocks = cmdLine.getOptionValue( "nof-stocks" );
        
        if ( cmdLine.hasOption( "stock-price" ) )
        	tuple.stockPrc = cmdLine.getOptionValue( "stock-price" );
        
        if ( cmdLine.hasOption( "dividend-gross" ) )
        	tuple.divGross = cmdLine.getOptionValue( "dividend-gross" );
        
        if ( cmdLine.hasOption( "stock-split-factor" ) )
        	tuple.stockSplitFactor = cmdLine.getOptionValue( "stock-split-factor" );
        
        if ( cmdLine.hasOption( "date-format" ) )
        	tuple.dateFormat = cmdLine.getOptionValue( "date-format" );

        if ( cmdLine.hasOption( "date-posted" ) )
        	tuple.datPst = cmdLine.getOptionValue( "date-posted" );
        
        if ( cmdLine.hasOption( "description" ) )
        	tuple.descr = cmdLine.getOptionValue( "description" );
        
        parseCoreParams( tuple );        
    }
    
    // END Core parameters
    // ----------------------------
    
    // <batch>
    if (cmdLine.hasOption("batch"))
    {
    	batch = true;
    }
    else
    {
    	batch = false;
    }
    if (! silent)
      System.err.println("batch:         " + batch);

    // <batch-out-file>
    if ( cmdLine.hasOption("batch-out-file") )
    {
    	if ( ! batch )
    	{
            System.err.println("Error: <batch-out-file> must only be set with <batch> option set");
            throw new InvalidCommandLineArgsException();
    	}
    	
    	batchOutFileName = cmdLine.getOptionValue("batch-out-file");
    }
    else
    {
    	if ( batch )
    	{
            System.err.println("Error: <batch-out-file> must be set with <batch> option set");
            throw new InvalidCommandLineArgsException();
    	}
    }
    if (! silent)
    	System.err.println("Batch-out-file:      '" + batchOutFileName + "'");
  }

  private void parseCoreParams(ParamTuple tuple) throws InvalidCommandLineArgsException
  {
	// <type>
	if ( tuple.type != null )
	{
	    try
	    {
	      type = SecuritiesAccountTransactionManager.Type.valueOf( tuple.type );
	    }
	    catch ( Exception exc )
	    {
	      System.err.println("Could not parse <type>");
	      throw new InvalidCommandLineArgsException();
	    }
	}
	else
	{
	      System.err.println("<type> is not set");
	      throw new InvalidCommandLineArgsException();
	}
    if (! silent)
    	System.err.println("Type: " + type);
    
    // --
    
    // <stock-account-id>
    if ( tuple.stockAcctID != null )
    {
        try
        {
          stockAcctID = new GCshID( tuple.stockAcctID );
        }
        catch ( Exception exc )
        {
          System.err.println("Could not parse <stock-account-id>");
          throw new InvalidCommandLineArgsException();
        }
    }
    else
    {
    	System.err.println("<stock-account-id> is not set");
    	throw new InvalidCommandLineArgsException();
    }
    if (! silent)
    	System.err.println("Stock account ID: " + stockAcctID);
    
    // <income-account-id>
    if ( tuple.incomeAcctID != null ) 
    {
    	if ( type != SecuritiesAccountTransactionManager.Type.DIVIDEND )
    	{
    		System.err.println("Error: <income-account-id> may only be set with <type> = '" + SecuritiesAccountTransactionManager.Type.DIVIDEND + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    	
        try
        {
            incomeAcctID = new GCshID( tuple.incomeAcctID );
        }
        catch ( Exception exc )
        {
          System.err.println("Could not parse <income-account-id>");
          throw new InvalidCommandLineArgsException();
        }
    } 
    else 
    {
    	if ( type == SecuritiesAccountTransactionManager.Type.DIVIDEND )
    	{
    		System.err.println("Error: <income-account-id> must be set with <type> = '" + SecuritiesAccountTransactionManager.Type.DIVIDEND + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    }
    if (! silent)
    	System.err.println("Income account ID: " + incomeAcctID);

    // <expense-account-amounts>
    // CAUTION: <expense-account-amounts> must *not necessarily* be set for buy-stock or
    // dividend transactions (although in most cases, it is). However, we have an additional 
    // option: "DUMMY" may be set for easier handling with scripts (i.e., it is always set in 
    // scripts), but when calling interactively, it need not be set.
    if ( tuple.expensesAcctAmtList != null )
    {
//    	if ( type != SecuritiesAccountTransactionManager.Type.BUY_STOCK &&
//       	 type != SecuritiesAccountTransactionManager.Type.DIVIDEND )
//      {
//       	System.err.println("Error: <expense-account-amounts> may only be set with <type> = '" + 
//       					   SecuritiesAccountTransactionManager.Type.BUY_STOCK + "' or '" +
//       					   SecuritiesAccountTransactionManager.Type.DIVIDEND + "'");
//       	throw new InvalidCommandLineArgsException();
//       }

    	expensesAcctAmtList = CmdLineHelper.getExpAcctAmtMulti(tuple.expensesAcctAmtList, "expense-account-amounts");
    }
//    else
//    {
//    	if ( type == SecuritiesAccountTransactionManager.Type.BUY_STOCK ||
//    		 type == SecuritiesAccountTransactionManager.Type.DIVIDEND )
//    	{
//    		System.err.println("Error: <expense-account-amounts> must be set with <type> = '" + 
//    						   SecuritiesAccountTransactionManager.Type.BUY_STOCK + "' or '" +
//    						   SecuritiesAccountTransactionManager.Type.DIVIDEND + "'");
//    		throw new InvalidCommandLineArgsException();
//    	}
//    }
    if (! silent)
    {
        System.err.println("Expenses account/amount pairs");
        for ( AcctIDAmountPair elt : expensesAcctAmtList )
            System.err.println(" - " + elt);
    }
    
    // <offset-account-id>
    if ( tuple.offsetAcctID != null )
    {
    	if ( type != SecuritiesAccountTransactionManager.Type.BUY_STOCK &&
    		 type != SecuritiesAccountTransactionManager.Type.DIVIDEND )
    	{
    		System.err.println("Error: <offset-account-id> may only be set with <type> = '" + 
    						   SecuritiesAccountTransactionManager.Type.BUY_STOCK + "' or '" + 
    						   SecuritiesAccountTransactionManager.Type.DIVIDEND + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    	
        try
        {
          offsetAcctID = new GCshID( tuple.offsetAcctID );
        }
        catch ( Exception exc )
        {
          System.err.println("Could not parse <offset-account-id>");
          throw new InvalidCommandLineArgsException();
        }
    }
    else
    {
    	if ( type == SecuritiesAccountTransactionManager.Type.BUY_STOCK ||
       		 type == SecuritiesAccountTransactionManager.Type.DIVIDEND )
    	{
    		System.err.println("Error: <offset-account-id> must be set with <type> = '" + 
    						   SecuritiesAccountTransactionManager.Type.BUY_STOCK + "' or '" + 
    						   SecuritiesAccountTransactionManager.Type.DIVIDEND + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    }
    if (! silent)
    	System.err.println("Offsetting account ID: " + offsetAcctID);
    
    // --
    
    // <nof-stocks>
    if ( tuple.nofStocks != null ) 
    {
    	if ( mode == BookMode.LISTFILE &&
       		 tuple.nofStocks.trim().equals("") )
       	{
    		// Technically set, but logically unset
        	if ( type == SecuritiesAccountTransactionManager.Type.BUY_STOCK )
        	{
        		System.err.println("Error: <nof-stocks> must be set with <type> = '" + SecuritiesAccountTransactionManager.Type.BUY_STOCK + "'");
        		throw new InvalidCommandLineArgsException();
        	}
       	}
       	else
       	{
        	if ( type != SecuritiesAccountTransactionManager.Type.BUY_STOCK )
        	{
        		System.err.println("Error: <nof-stocks> may only be set with <type> = '" + SecuritiesAccountTransactionManager.Type.BUY_STOCK + "'");
        		throw new InvalidCommandLineArgsException();
        	}
        	
        	try
        	{
        		nofStocks = new FixedPointNumber(Double.parseDouble(tuple.nofStocks));
        	}
        	catch ( Exception exc )
        	{
        		System.err.println("Could not parse <nof-stocks>");
        		throw new InvalidCommandLineArgsException();
        	}
       	}
    } 
    else 
    {
    	if ( type == SecuritiesAccountTransactionManager.Type.BUY_STOCK )
    	{
    		System.err.println("Error: <nof-stocks> must be set with <type> = '" + SecuritiesAccountTransactionManager.Type.BUY_STOCK + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    }
    if (! silent)
    	System.err.println("No. of stocks: " + nofStocks);

    // <stock-price>
    if ( tuple.stockPrc != null ) 
    {
    	if ( mode == BookMode.LISTFILE &&
    		 tuple.stockPrc.trim().equals("") )
    	{
       		// Technically set, but logically unset
        	if ( type == SecuritiesAccountTransactionManager.Type.BUY_STOCK )
        	{
        		System.err.println("Error: <stock-price> must be set with <type> = '" + SecuritiesAccountTransactionManager.Type.BUY_STOCK + "'");
        		throw new InvalidCommandLineArgsException();
        	}
    	}
    	else
    	{
    		if ( type != SecuritiesAccountTransactionManager.Type.BUY_STOCK )
    		{
    			System.err.println("Error: <stock-price> may only be set with <type> = '" + SecuritiesAccountTransactionManager.Type.BUY_STOCK + "'");
    			throw new InvalidCommandLineArgsException();
    		}
               	
    		try
    		{
    			BigMoney betrag = BigMoney.of(CurrencyUnit.EUR, Double.parseDouble(tuple.stockPrc));
    			stockPrc = new FixedPointNumber(betrag.getAmount());
    		}
    		catch ( Exception exc )
    		{
    			System.err.println("Could not parse <stock-price>");
    			throw new InvalidCommandLineArgsException();
    		}
    	}
    } 
    else 
    {
    	if ( type == SecuritiesAccountTransactionManager.Type.BUY_STOCK )
    	{
    		System.err.println("Error: <stock-price> must be set with <type> = '" + SecuritiesAccountTransactionManager.Type.BUY_STOCK + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    }
    if (! silent)
    	System.err.println("Stock price: " + stockPrc);

    // <dividend-gross>
    if ( tuple.divGross != null ) 
    {
    	if ( type != SecuritiesAccountTransactionManager.Type.DIVIDEND )
    	{
    		System.err.println("Error: <dividend-gross> may only be set with <type> = '" + SecuritiesAccountTransactionManager.Type.DIVIDEND + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    	
        try
        {
          BigMoney betrag = BigMoney.of(CurrencyUnit.EUR, Double.parseDouble(tuple.divGross));
          divGross = new FixedPointNumber(betrag.getAmount());
        }
        catch ( Exception exc )
        {
          System.err.println("Could not parse <dividend-gross>");
          throw new InvalidCommandLineArgsException();
        }
    } 
    else 
    {
    	if ( type == SecuritiesAccountTransactionManager.Type.DIVIDEND )
    	{
    		System.err.println("Error: <dividend-gross> must be set with <type> = '" + SecuritiesAccountTransactionManager.Type.DIVIDEND + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    }
    if (! silent)
    	System.err.println("Gross dividend: " + divGross);

    // <stock-split-factor>
    if ( tuple.stockSplitFactor != null ) 
    {
    	if ( type != SecuritiesAccountTransactionManager.Type.STOCK_SPLIT )
    	{
    		System.err.println("Error: <stock-split-factor> may only be set with <type> = '" + SecuritiesAccountTransactionManager.Type.STOCK_SPLIT + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    	
        try
        {
          BigMoney betrag = BigMoney.of(CurrencyUnit.EUR, Double.parseDouble(tuple.stockSplitFactor));
          stockSplitFactor = new FixedPointNumber(betrag.getAmount());
        }
        catch ( Exception exc )
        {
          System.err.println("Could not parse <stock-split-factor>");
          throw new InvalidCommandLineArgsException();
        }
    } 
    else 
    {
    	if ( type == SecuritiesAccountTransactionManager.Type.STOCK_SPLIT )
    	{
    		System.err.println("Error: <stock-split-factor> must be set with <type> = '" + SecuritiesAccountTransactionManager.Type.STOCK_SPLIT + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    }
    if (! silent)
    	System.err.println("Stock split factor: " + stockSplitFactor);

    // --

    // <date-format>
    dateFormat = CmdLineHelper.getDateFormat(tuple.dateFormat, "date-format");
    if (! silent)
    	System.err.println("date-format: " + dateFormat);

    // <date-posted>
    if ( tuple.datPst != null )
    {
        try
        {
          datPst = CmdLineHelper.getDate(tuple.datPst, "date-posted", dateFormat); 
        }
        catch ( Exception exc )
        {
          System.err.println("Could not parse <date-posted>");
          throw new InvalidCommandLineArgsException();
        }
    }
    else
    {
        System.err.println("<date-posted> is not set");
        throw new InvalidCommandLineArgsException();
    }
    if (! silent)
    	System.err.println("Date posted: " + datPst.toString());
    
    // <description>
    if ( tuple.descr != null )
    {
      try
      {
        descr = tuple.descr;
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <description>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      descr = "Generated by GenDepotTrx, " + LocalDateTime.now();
    }
    if (! silent)
    	System.err.println("Description: '" + descr + "'");
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GenDepotTrx", options );
    
    System.err.println("");
    System.err.println("Valid values for <mode>:");
    for ( BookMode elt : BookMode.values() )
    	System.err.println(" - " + elt);

    System.err.println("");
    System.err.println("Valid values for <type>:");
    for ( SecuritiesAccountTransactionManager.Type elt : SecuritiesAccountTransactionManager.Type.values() )
    	System.err.println(" - " + elt);
  }
}
