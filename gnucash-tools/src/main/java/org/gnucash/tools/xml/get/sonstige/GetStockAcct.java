package org.gnucash.tools.xml.get.sonstige;

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
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_SecIdType;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.tools.CommandLineTool;
import org.gnucash.tools.xml.helper.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

public class GetStockAcct extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetStockAcct.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String                gcshFileName = null;
  
  private static Helper.Mode           acctMode     = null;
  private static Helper.CmdtyMode      cmdtyMode      = null;
  private static GCshID                acctID       = null;
  private static String                acctName     = null;
  
  private static GCshCmdtyID_SecIdType cmdtyID      = null;
  private static String                isin         = null;
  private static String                cmdtyName    = null;
  
  private static boolean scriptMode = false;

  public static void main( String[] args )
  {
    try
    {
      GetStockAcct tool = new GetStockAcct ();
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
//    cfg = new PropertiesConfiguration(System.getProperty("config"));
//    getConfigSettings(cfg);

    // Options
    // The essential ones
    Option optFile = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("file")
      .withDescription("GnuCash file")
      .withLongOpt("GnuCash file")
      .create("f");
      
    Option optAcctMode = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("mode")
      .withDescription("Selection mode for account")
      .withLongOpt("account-mode")
      .create("am");
      
    Option optCmdtyMode = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("mode")
      .withDescription("Selection mode for commodity")
      .withLongOpt("commodity-mode")
      .create("cm");
        
    Option optAcctID = OptionBuilder
      .hasArg()
      .withArgName("acctid")
      .withDescription("Account-ID")
      .withLongOpt("account-id")
      .create("acct");
    
    Option optAcctName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Account name (or part of)")
      .withLongOpt("account-name")
      .create("an");
      
    Option optSecID = OptionBuilder
      .hasArg()
      .withArgName("ID")
      .withDescription("Commodity ID")
      .withLongOpt("commodity-id")
      .create("cmdty");
            
    Option optISIN = OptionBuilder
      .hasArg()
      .withArgName("isin")
      .withDescription("ISIN")
      .withLongOpt("isin")
      .create("is");
          
    Option optSecName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Commodity name (or part of)")
      .withLongOpt("commodity-name")
      .create("sn");
            
    // The convenient ones
    Option optScript = OptionBuilder
      .withDescription("Script Mode")
      .withLongOpt("script")
      .create("sl");            
          
    options = new Options();
    options.addOption(optFile);
    options.addOption(optAcctMode);
    options.addOption(optCmdtyMode);
    options.addOption(optAcctID);
    options.addOption(optAcctName);
    options.addOption(optSecID);
    options.addOption(optISIN);
    options.addOption(optSecName);
    options.addOption(optScript);
  }

  @Override
  protected void getConfigSettings(PropertiesConfiguration cs) throws Exception
  {
    // ::EMPTY
  }
  
  @Override
  protected void kernel() throws Exception
  {
    GnuCashFileImpl kmmFile = new GnuCashFileImpl(new File(gcshFileName));

    GnuCashAccount acct = null;
    
    if (acctMode == Helper.Mode.ID)
    {
      acct = kmmFile.getAccountByID(acctID);
      if (acct == null)
      {
        if ( ! scriptMode )
          System.err.println("Found no account with that name");
        throw new NoEntryFoundException();
      }
    }
    else if (acctMode == Helper.Mode.NAME)
    {
      Collection<GnuCashAccount> acctList = null;
      acctList = kmmFile.getAccountsByTypeAndName(GnuCashAccount.Type.ASSET, acctName, 
                                                  true, true);
      if (acctList.size() == 0)
      {
        if ( ! scriptMode )
          System.err.println("Found no account with that name.");
        throw new NoEntryFoundException();
      }
      else if (acctList.size() > 1)
      {
        if ( ! scriptMode )
        {
          System.err.println("Found " + acctList.size() + " accounts with that name.");
          System.err.println("Taking first one.");
        }
      }
      acct = acctList.iterator().next();
    }

    if ( ! scriptMode )
      System.out.println("Account:  " + acct.toString());
    
    // ----------------------------

    GnuCashCommodity cmdty = null;
    
    if ( cmdtyMode == Helper.CmdtyMode.ID )
    {
      cmdty = kmmFile.getCommodityByQualifID(cmdtyID);
      if ( cmdty == null )
      {
        if ( ! scriptMode )
          System.err.println("Could not find a commodity with this ID.");
        throw new NoEntryFoundException();
      }
    }
    else if ( cmdtyMode == Helper.CmdtyMode.ISIN )
    {
      cmdty = kmmFile.getCommodityByXCode(isin);
      if ( cmdty == null )
      {
        if ( ! scriptMode )
          System.err.println("Could not find securities with this ISIN.");
        throw new NoEntryFoundException();
      }
    }
    else if ( cmdtyMode == Helper.CmdtyMode.NAME )
    {
      Collection<GnuCashCommodity> cmdtyList = kmmFile.getCommoditiesByName(cmdtyName); 
      if ( cmdtyList.size() == 0 )
      {
        if ( ! scriptMode )
          System.err.println("Could not find securities matching this name.");
        throw new NoEntryFoundException();
      }
      if ( cmdtyList.size() > 1 )
      {
        if ( ! scriptMode )
        {
          System.err.println("Found " + cmdtyList.size() + "securities matching this name.");
          System.err.println("Please specify more precisely.");
        }
        throw new TooManyEntriesFoundException();
      }
      cmdty = cmdtyList.iterator().next(); // first element
    }
    
    if ( ! scriptMode )
      System.out.println("Commodity: " + cmdty.toString());
    
    // ----------------------------
    
    for ( GnuCashAccount chld : acct.getChildren() ) {
      if ( chld.getType() == GnuCashAccount.Type.STOCK &&
           chld.getCmdtyCurrID().equals(cmdty.getQualifID()) ) {
          System.out.println(chld.getID());
      }
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

    // <script>
    if ( cmdLine.hasOption("script") )
    {
      scriptMode = true; 
    }
    // System.err.println("Script mode: " + scriptMode);
    
    // ---

    // <GnuCash file>
    try
    {
      gcshFileName = cmdLine.getOptionValue("GnuCash file");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <GnuCash file>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("GnuCash file:     '" + gcshFileName + "'");
    
    // <account-mode>
    try
    {
      acctMode = Helper.Mode.valueOf(cmdLine.getOptionValue("account-mode"));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <account-mode>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("Account mode:  " + acctMode);

    // <commodity-mode>
    try
    {
      cmdtyMode = Helper.CmdtyMode.valueOf(cmdLine.getOptionValue("commodity-mode"));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <commodity-mode>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("Commodity mode: " + cmdtyMode);

    // <account-id>
    if ( cmdLine.hasOption("account-id") )
    {
      if ( acctMode != Helper.Mode.ID )
      {
        System.err.println("<account-id> must only be set with <account-mode> = '" + Helper.Mode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        acctID = new GCshID( cmdLine.getOptionValue("account-id") );
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <account-id>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( acctMode == Helper.Mode.ID )
      {
        System.err.println("<account-id> must be set with <account-mode> = '" + Helper.Mode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }      
    }
    
    if ( ! scriptMode )
      System.err.println("Account ID:    '" + acctID + "'");

    // <account-name>
    if ( cmdLine.hasOption("account-name") )
    {
      if ( acctMode != Helper.Mode.NAME )
      {
        System.err.println("<account-name> must only be set with <account-mode> = '" + Helper.Mode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        acctName = cmdLine.getOptionValue("account-name");
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <name>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( acctMode == Helper.Mode.NAME )
      {
        System.err.println("<account-name> must be set with <account-mode> = '" + Helper.Mode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }      
    }
    
    if ( ! scriptMode )
      System.err.println("Account name:  '" + acctName + "'");

    // <commodity-id>
    if ( cmdLine.hasOption("commodity-id") )
    {
      if ( cmdtyMode != Helper.CmdtyMode.ID )
      {
        System.err.println("<commodity-id> must only be set with <commodity-mode> = '" + Helper.CmdtyMode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        cmdtyID = new GCshCmdtyID_SecIdType( GCshCmdtyCurrNameSpace.SecIdType.ISIN, cmdLine.getOptionValue("commodity-id") );
      }
      catch (Exception exc)
      {
        System.err.println("Could not parse <commodity-id>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( cmdtyMode == Helper.CmdtyMode.ID )
      {
        System.err.println("<commodity-id> must be set with <commodity-mode> = '" + Helper.CmdtyMode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    if (!scriptMode)
      System.err.println("Commodity ID:  '" + cmdtyID + "'");

    // <isin>
    if ( cmdLine.hasOption("isin") )
    {
      if ( cmdtyMode != Helper.CmdtyMode.ISIN )
      {
        System.err.println("<isin> must only be set with <commodity-mode> = '" + Helper.CmdtyMode.ISIN.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        isin = cmdLine.getOptionValue("isin");
      }
      catch (Exception exc)
      {
        System.err.println("Could not parse <isin>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( cmdtyMode == Helper.CmdtyMode.ISIN )
      {
        System.err.println("<isin> must be set with <commodity-mode> = '" + Helper.CmdtyMode.ISIN.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    if (!scriptMode)
      System.err.println("ISIN:          '" + isin + "'");

    // <commodity-name>
    if ( cmdLine.hasOption("commodity-name") )
    {
      if ( cmdtyMode != Helper.CmdtyMode.NAME )
      {
        System.err.println("<commodity-name> must only be set with <commodity-mode> = '" + Helper.CmdtyMode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        cmdtyName = cmdLine.getOptionValue("commodity-name");
      }
      catch (Exception exc)
      {
        System.err.println("Could not parse <commodity-name>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( cmdtyMode == Helper.CmdtyMode.NAME )
      {
        System.err.println("<commodity-name> must be set with <commodity-mode> = '" + Helper.CmdtyMode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    if (!scriptMode)
      System.err.println("Commodity name: '" + cmdtyName + "'");
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GetSubAcct", options );
    
    System.out.println("");
    System.out.println("Valid values for <account-mode>:");
    for ( Helper.Mode elt : Helper.Mode.values() )
      System.out.println(" - " + elt);

    System.out.println("");
    System.out.println("Valid values for <commodity-mode>:");
    for ( Helper.CmdtyMode elt : Helper.CmdtyMode.values() )
      System.out.println(" - " + elt);
  }
}
