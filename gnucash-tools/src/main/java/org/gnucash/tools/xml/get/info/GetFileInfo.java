package org.gnucash.tools.xml.get.info;

import java.io.File;

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

import xyz.schnorxoborx.base.beanbase.UnknownAccountTypeException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.tools.CommandLineTool;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.impl.GnuCashFileImpl;

public class GetFileInfo extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetFileInfo.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String  gcshFileName = null;

  public static void main( String[] args )
  {
    try
    {
      GetFileInfo tool = new GetFileInfo ();
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
      .withLongOpt("GnuCash file")
      .create("f");

    // The convenient ones
    // ::EMPTY
          
    options = new Options();
    options.addOption(optFile);
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
    
    printStats(gcshFile);
    printRootAcct(gcshFile);
    printTopAccts(gcshFile);
    printStatement(gcshFile);
  }

  // -----------------------------------------------------------------

  private void printStats(GnuCashFileImpl gcshFile)
  {
    System.out.println("");
    System.out.println("Stats:");
    System.out.println(gcshFile.toString());
  }

  private void printRootAcct(GnuCashFileImpl gcshFile)
  {
    System.out.println("");
    System.out.println("Root Account:");
    
    GnuCashAccount acct = gcshFile.getRootAccount();
    System.out.println(acct.toString());
  }

  private void printTopAccts(GnuCashFileImpl gcshFile)
  {
    System.out.println("");
    System.out.println("Top Accounts:");
    
    for ( GnuCashAccount acct : gcshFile.getTopAccounts() ) {
      System.out.println(" - " + acct.toString());
    }
  }

  private void printStatement(GnuCashFileImpl gcshFile)
  {
    System.out.println("");
    System.out.println("Financial Statement:");
    
    for ( GnuCashAccount acct : gcshFile.getTopAccounts() ) {
      if ( acct.getType() == GnuCashAccount.Type.ASSET )
        System.out.print("  Assets:      ");
      else if ( acct.getType() == GnuCashAccount.Type.LIABILITY )
        System.out.print("  Liabilities: ");
      else if ( acct.getType() == GnuCashAccount.Type.INCOME )
        System.out.print("  Income:      ");
      else if ( acct.getType() == GnuCashAccount.Type.EXPENSE )
        System.out.print("  Expenses:    ");
      else if ( acct.getType() == GnuCashAccount.Type.EQUITY )
        System.out.print("  Equity:      ");
        
      System.out.println(acct.getBalanceRecursiveFormatted());
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
    
    System.err.println("GnuCash file:      '" + gcshFileName + "'");
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GetFileInfo", options );
  }
}
