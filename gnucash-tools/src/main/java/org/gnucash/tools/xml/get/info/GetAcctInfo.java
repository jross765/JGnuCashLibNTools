package org.gnucash.tools.xml.get.info;

import java.io.File;
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
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.tools.CommandLineTool;
import org.gnucash.tools.xml.helper.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

public class GetAcctInfo extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetAcctInfo.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String      gcshFileName = null;
  private static Helper.Mode mode         = null;
  private static GCshID      acctID       = null;
  private static String      acctName     = null;
  
  private static boolean showParents  = false;
  private static boolean showChildren = false;
  private static boolean showTrx      = false;
  
  private static boolean scriptMode = false; // ::TODO

  public static void main( String[] args )
  {
    try
    {
      GetAcctInfo tool = new GetAcctInfo ();
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
    // acctID = UUID.randomUUID();

//    cfg = new PropertiesConfiguration(System.getProperty("config"));
//    getConfigSettings(cfg);

    // Options
    // The essential ones
    Option optFile = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("file")
      .withDescription("GnuCash file")
      .withLongOpt("gnucash-file")
      .create("f");
      
    Option optMode = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("mode")
      .withDescription("Selection mode")
      .withLongOpt("mode")
      .create("m");
      
    Option optAcctID = OptionBuilder
      .hasArg()
      .withArgName("UUID")
      .withDescription("Account-ID")
      .withLongOpt("account-id")
      .create("acct");
    
    Option optAcctName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Account name")
      .withLongOpt("name")
      .create("n");
      
    // The convenient ones
    Option optShowPrnt = OptionBuilder
      .withDescription("Show parents")
      .withLongOpt("show-parents")
      .create("sprnt");
        
    Option optShowChld = OptionBuilder
        .withDescription("Show children")
        .withLongOpt("show-children")
        .create("schld");
          
    Option optShowTrx = OptionBuilder
      .withDescription("Show transactions")
      .withLongOpt("show-transactions")
      .create("strx");
          
    options = new Options();
    options.addOption(optFile);
    options.addOption(optMode);
    options.addOption(optAcctID);
    options.addOption(optAcctName);
    options.addOption(optShowPrnt);
    options.addOption(optShowChld);
    options.addOption(optShowTrx);
  }

  @Override
  protected void getConfigSettings(PropertiesConfiguration cs) throws Exception
  {
    // ::EMPTY
  }
  
  @Override
  protected void kernel() throws Exception
  {
    GnuCashFileImpl gcshFile = new GnuCashFileImpl(new File(gcshFileName));
    
    GnuCashAccount acct = null;
    if ( mode == Helper.Mode.ID )
    {
      acct = gcshFile.getAccountByID(acctID);
      if ( acct == null )
      {
        System.err.println("Found no account with that ID");
        throw new NoEntryFoundException();
      }
    }
    else if ( mode == Helper.Mode.NAME )
    {
      Collection <GnuCashAccount> acctList = null; 
      acctList = gcshFile.getAccountsByName(acctName, true, true);
      if ( acctList.size() == 0 ) 
      {
        System.err.println("Found no account with that name.");
        throw new NoEntryFoundException();
      }
      else if ( acctList.size() > 1 ) 
      {
        System.err.println("Found several accounts with that name.");
        System.err.println("Taking first one.");
      }
      acct = acctList.iterator().next();
    }
    
    printAcctInfo(acct, 0);
  }

  private void printAcctInfo(GnuCashAccount acct, int depth)
  {
    System.out.println("Depth:           " + depth);

    try
    {
      System.out.println("ID:              " + acct.getID());
    }
    catch ( Exception exc )
    {
      System.out.println("ID:              " + "ERROR");
    }
    
    try
    {
      System.out.println("toString:        " + acct.toString());
    }
    catch ( Exception exc )
    {
      System.out.println("toString:        " + "ERROR");
    }
    
    try
    {
      System.out.println("Type:            " + acct.getType());
    }
    catch ( Exception exc )
    {
      System.out.println("Type:            " + "ERROR");
    }
    
    try
    {
      System.out.println("Name:            '" + acct.getName() + "'");
    }
    catch ( Exception exc )
    {
      System.out.println("Name:            " + "ERROR");
    }
    
    try
    {
      System.out.println("Qualified name:  '" + acct.getQualifiedName() + "'");
    }
    catch ( Exception exc )
    {
      System.out.println("Qualified name:  " + "ERROR");
    }
    
    try
    {
      System.out.println("Description:     '" + acct.getDescription() + "'");
    }
    catch ( Exception exc )
    {
      System.out.println("Description:     " + "ERROR");
    }
    
    try
    {
      System.out.println("Cmdty/Curr:      '" + acct.getCmdtyCurrID() + "'");
    }
    catch ( Exception exc )
    {
      System.out.println("Cmdty/Curr:      " + "ERROR");
    }
    
    System.out.println("");
    try
    {
      System.out.println("Balance:         " + acct.getBalanceFormatted());
    }
    catch ( Exception exc )
    {
      System.out.println("Balance:         " + "ERROR");
    }

    try
    {
      System.out.println("Balance recurs.: " + acct.getBalanceRecursiveFormatted());
    }
    catch ( Exception exc )
    {
      System.out.println("Balance recurs.: " + "ERROR");
    }

    // ---
        
    if ( showParents )
      showParents(acct, depth);
    
    if ( showChildren )
      showChildren(acct, depth);
    
    if ( showTrx )
      showTransactions(acct);
  }

  // -----------------------------------------------------------------

  private void showParents(GnuCashAccount acct, int depth)
  {
    if ( depth <= 0 &&
         acct.getType() != GnuCashAccount.Type.ROOT )
    {
      System.out.println("");
      System.out.println(">>> BEGIN Parent Account");
      printAcctInfo(acct.getParentAccount(), depth - 1);
      System.out.println("<<< END Parent Account");
    }
  }
  
  private void showChildren(GnuCashAccount acct, int depth)
  {
    System.out.println("");
    System.out.println("Children:");
    
    if ( depth >= 0 )
    {
      System.out.println(">>> BEGIN Child Account");
      for ( GnuCashAccount childAcct : acct.getChildren())
      {
        printAcctInfo(childAcct, depth + 1);
      }
      System.out.println("<<< END Child Account");
    }
  }
  
  private void showTransactions(GnuCashAccount acct)
  {
    System.out.println("");
    System.out.println("Transactions:");
    
    for ( GnuCashTransaction trx : acct.getTransactions() )
    {
      System.out.println(" - " + trx.toString());
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

    // <gnucash-file>
    try
    {
      gcshFileName = cmdLine.getOptionValue("gnucash-file");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <gnucash-file>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("GnuCash file:      '" + gcshFileName + "'");
    
    // <mode>
    try
    {
      mode = Helper.Mode.valueOf(cmdLine.getOptionValue("mode"));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <mode>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("Mode:              " + mode);

    // <account-id>
    try
    {
      acctID = new GCshID( cmdLine.getOptionValue("account-id") );
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <account-id>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("Account ID:        '" + acctID + "'");

    // <name>
    try
    {
      acctName = cmdLine.getOptionValue("name");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <name>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("Name:              '" + acctName + "'");

    // <show-parents>
    if ( cmdLine.hasOption("show-parents"))
    {
      showParents = true;
    }
    else
    {
      showParents = false;
    }
    
    if ( ! scriptMode )
      System.err.println("Show parents:      " + showParents);

    // <show-children>
    if ( cmdLine.hasOption("show-children"))
    {
      showChildren = true;
    }
    else
    {
      showChildren = false;
    }
    
    if ( ! scriptMode )
      System.err.println("Show children:     " + showChildren);

    // <show-transactions>
    if ( cmdLine.hasOption("show-transactions"))
    {
      showTrx = true;
    }
    else
    {
      showTrx = false;
    }
    
    if ( ! scriptMode )
      System.err.println("Show transactions: " + showTrx);
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GetAcctInfo", options );
    
    System.out.println("");
    System.out.println("Valid values for <mode>:");
    for ( Helper.Mode elt : Helper.Mode.values() )
      System.out.println(" - " + elt);
  }
}
