package org.gnucash.tools.xml.gen.complex;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;
import xyz.schnorxoborx.base.dateutils.DateHelpers;
import xyz.schnorxoborx.base.dateutils.LocalDateHelpers;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.apiext.secacct.SecuritiesAccountTransactionManager;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.base.tuples.AcctIDAmountPair;
import org.gnucash.tools.CommandLineTool;
import org.gnucash.tools.xml.helper.CmdLineHelper;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.LoggerFactory;

public class GenDepotTrx extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GenDepotTrx.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String           gcshInFileName = null;
  private static String           gcshOutFileName = null;
  
  private static SecuritiesAccountTransactionManager.Type type = null;
  
  private static GCshID           stockAcctID = null;
  private static GCshID           incomeAcctID = null;
  private static Collection<AcctIDAmountPair> expensesAcctAmtList = null;
  private static GCshID           offsetAcctID = null;
  
  private static FixedPointNumber nofStocks = null;
  private static FixedPointNumber stockPrc = null;
  private static FixedPointNumber divGross = null;
  
  private static LocalDate        datPst = null;
  private static String           descr = null;

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
    
    // ---
    
    Option optType = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("type")
      .withDescription("Transaction type")
      .withLongOpt("type")
      .create("tp");
    	      
    // ---
        
    Option optStockAcct = OptionBuilder
      .isRequired()
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
      // .isRequired() // <-- sic, not required!
      .hasArg()
      .withArgName("pair-list")
      .withDescription("Account-ID/amount pairs for expenses (taxes and fees), " + 
                       "list separated by '|', pairs separated by ';'")
      .withLongOpt("expense-account-amounts")
      .create("expacctamt");
      
    Option optOffsetAcct = OptionBuilder
      .isRequired()
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
      .withLongOpt("dividend")
      .create("div");

    // ---
    
    Option optDatePosted = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("datetime")
      .withDescription("Date posted")
      .withLongOpt("date-posted")
      .create("dtp");
            
    // The convenient ones
    Option optDescr = OptionBuilder
      .hasArg()
      .withArgName("descr")
      .withDescription("Description")
      .withLongOpt("description")
      .create("dscr");
              
    options = new Options();
    options.addOption(optFileIn);
    options.addOption(optFileOut);
    
    options.addOption(optType);
    
    options.addOption(optStockAcct);
    options.addOption(optIncomeAcct);
    options.addOption(optExpensesAcctAmtList);
    options.addOption(optOffsetAcct);
    
    options.addOption(optNofStocks);
    options.addOption(optStockPrice);
    options.addOption(optDividend);
    
    options.addOption(optDatePosted);
    options.addOption(optDescr);
  }

  @Override
  protected void getConfigSettings(PropertiesConfiguration cs) throws Exception
  {
    // ::EMPTY
  }
  
  @Override
  protected void kernel() throws Exception
  {
    GnuCashWritableFileImpl gcshFile = new GnuCashWritableFileImpl(new File(gcshInFileName));
    
	GnuCashAccount stockAcct = gcshFile.getAccountByID(stockAcctID);
	if ( stockAcct == null )
		System.err.println("Error: Cannot get account with ID '" + stockAcctID + "'");
	
	GnuCashAccount incomeAcct = null;
	if ( incomeAcctID != null )
	{
		incomeAcct = gcshFile.getAccountByID(incomeAcctID);
		if ( incomeAcct == null )
			System.err.println("Error: Cannot get account with ID '" + incomeAcctID + "'");
	}
	
	for ( AcctIDAmountPair elt : expensesAcctAmtList )
	{
		GnuCashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
		if ( expensesAcct == null )
			System.err.println("Error: Cannot get account with ID '" + elt.accountID() + "'");
	}
	
	GnuCashAccount offsetAcct = gcshFile.getAccountByID(offsetAcctID);
	if ( offsetAcct == null )
		System.err.println("Error: Cannot get account with ID '" + offsetAcctID + "'");

	System.err.println("Account 1 name (stock):      '" + stockAcct.getQualifiedName() + "'");
	if ( incomeAcctID != null )
		System.err.println("Account 2 name (income):     '" + incomeAcct.getQualifiedName() + "'");

	int counter = 1;
	for ( AcctIDAmountPair elt : expensesAcctAmtList )
	{
		GnuCashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
		System.err.println("Account 3." + counter + " name (expenses): '" + expensesAcct.getQualifiedName() + "'");
		counter++;
	}
	
	System.err.println("Account 4 name (offsetting): '" + offsetAcct.getQualifiedName() + "'");
    
    // ---
    
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
    
    // ---
    
    System.out.println("Transaction to write: " + trx.toString());
    gcshFile.writeFile(new File(gcshOutFileName));
    
    System.out.println("OK");
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
    System.err.println("GnuCash file (in): '" + gcshInFileName + "'");
    
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
    System.err.println("GnuCash file (out): '" + gcshOutFileName + "'");
    
    // --
    
    // <type>
    try
    {
      type = SecuritiesAccountTransactionManager.Type.valueOf( cmdLine.getOptionValue("type") );
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <type>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Type: " + type);
    
    // --
    
    // <stock-account-id>
    try
    {
      stockAcctID = new GCshID( cmdLine.getOptionValue("stock-account-id") );
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <stock-account-id>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Stock account ID: " + stockAcctID);
    
    // <income-account-id>
    if ( cmdLine.hasOption("income-account-id") ) 
    {
    	if ( type != SecuritiesAccountTransactionManager.Type.DIVIDEND )
    	{
    		System.err.println("Error: <income-account-id> may only be set with <type> = '" + SecuritiesAccountTransactionManager.Type.DIVIDEND + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    	
        try
        {
            incomeAcctID = new GCshID( cmdLine.getOptionValue("income-account-id") );
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
    System.err.println("Income account ID: " + incomeAcctID);

    // <expense-account-amounts>
    expensesAcctAmtList = CmdLineHelper.getExpAcctAmtMulti(cmdLine);
    System.err.println("Expenses account/amount pairs: " + expensesAcctAmtList);
    
    // <offset-account-id>
    try
    {
      offsetAcctID = new GCshID( cmdLine.getOptionValue("offset-account-id") );
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <offset-account-id>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Offsetting account ID: " + offsetAcctID);
    
    // --
    
    // <nof-stocks>
    if ( cmdLine.hasOption("nof-stocks") ) 
    {
    	if ( type != SecuritiesAccountTransactionManager.Type.BUY_STOCK )
    	{
    		System.err.println("Error: <nof-stocks> may only be set with <type> = '" + SecuritiesAccountTransactionManager.Type.BUY_STOCK + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    	
    	try
    	{
    		nofStocks = new FixedPointNumber(Double.parseDouble(cmdLine.getOptionValue("nof-stocks")));
    	}
    	catch ( Exception exc )
    	{
    		System.err.println("Could not parse <nof-stocks>");
    		throw new InvalidCommandLineArgsException();
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
    System.err.println("No. of stocks: " + nofStocks);

    // <stock-price>
    if ( cmdLine.hasOption("stock-price") ) 
    {
    	if ( type != SecuritiesAccountTransactionManager.Type.BUY_STOCK )
    	{
    		System.err.println("Error: <stock-price> may only be set with <type> = '" + SecuritiesAccountTransactionManager.Type.BUY_STOCK + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    	
        try
        {
          BigMoney betrag = BigMoney.of(CurrencyUnit.EUR, Double.parseDouble(cmdLine.getOptionValue("stock-price")));
          stockPrc = new FixedPointNumber(betrag.getAmount());
        }
        catch ( Exception exc )
        {
          System.err.println("Could not parse <stock-price>");
          throw new InvalidCommandLineArgsException();
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
    System.err.println("Stock price: " + stockPrc);

    // <dividend>
    if ( cmdLine.hasOption("dividend") ) 
    {
    	if ( type != SecuritiesAccountTransactionManager.Type.DIVIDEND )
    	{
    		System.err.println("Error: <dividend> may only be set with <type> = '" + SecuritiesAccountTransactionManager.Type.DIVIDEND + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    	
        try
        {
          BigMoney betrag = BigMoney.of(CurrencyUnit.EUR, Double.parseDouble(cmdLine.getOptionValue("dividend")));
          divGross = new FixedPointNumber(betrag.getAmount());
        }
        catch ( Exception exc )
        {
          System.err.println("Could not parse <dividend>");
          throw new InvalidCommandLineArgsException();
        }
    } 
    else 
    {
    	if ( type == SecuritiesAccountTransactionManager.Type.DIVIDEND )
    	{
    		System.err.println("Error: <dividend> must be set with <type> = '" + SecuritiesAccountTransactionManager.Type.DIVIDEND + "'");
    		throw new InvalidCommandLineArgsException();
    	}
    }
    System.err.println("Gross dividend: " + divGross);

    // --

    // <date-posted>
    try
    {
      datPst = LocalDateHelpers.parseLocalDate(cmdLine.getOptionValue("date-posted"));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <date-posted>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Date posted: " + datPst);

    // <description>
    if ( cmdLine.hasOption("description") )
    {
      try
      {
        descr = cmdLine.getOptionValue("description");
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
    System.err.println("Description: '" + descr + "'");    
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GenDepotTrx", options );
    
    System.err.println("");
    System.err.println("Valid values for <type>:");
    for ( SecuritiesAccountTransactionManager.Type elt : SecuritiesAccountTransactionManager.Type.values() )
    	System.err.println(" - " + elt);
  }
}
