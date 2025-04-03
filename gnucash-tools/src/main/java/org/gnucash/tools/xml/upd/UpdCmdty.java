package org.gnucash.tools.xml.upd;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.gnucash.api.write.GnuCashWritableCommodity;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.tools.CommandLineTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

public class UpdCmdty extends CommandLineTool
{
  enum Mode
  {
    EXCHANGE_TICKER,
    ISIN
    // NAME <-- NO, NOT HERE!
  }
  
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdCmdty.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String gcshInFileName  = null;
  private static String gcshOutFileName = null;
  
  private static Mode   mode     = null;
  private static String exchange = null;
  private static String ticker   = null;
  private static String isin     = null;

  private static String name   = null;

  private static GnuCashWritableCommodity cmdty = null;

  public static void main( String[] args )
  {
    try
    {
      UpdCmdty tool = new UpdCmdty ();
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
    Option optFileIn = Option.builder("if")
      .required()
      .hasArg()
      .argName("file")
      .desc("GnuCash file (in)")
      .longOpt("gnucash-in-file")
      .build();
          
    Option optFileOut = Option.builder("of")
      .required()
      .hasArg()
      .argName("file")
      .desc("GnuCash file (out)")
      .longOpt("gnucash-out-file")
      .build();
      
    Option optMode = Option.builder("m")
      .required()
      .hasArg()
      .argName("mode")
      .desc("Selection mode")
      .longOpt("mode")
      .build();
    	         
    Option optExchange = Option.builder("exch")
      .hasArg()
      .argName("exch")
      .desc("Exchange code")
      .longOpt("exchange")
      .build();
   	      
    Option optTicker = Option.builder("tkr")
      .hasArg()
      .argName("ticker")
      .desc("Ticker")
      .longOpt("ticker")
      .build();
    	      
    Option optISIN = Option.builder("is")
      .hasArg()
      .argName("isin")
      .desc("ISIN")
      .longOpt("isin")
      .build();
            
    Option optName = Option.builder("n")
      .hasArg()
      .argName("name")
      .desc("Account name")
      .longOpt("name")
      .build();
    
//    Option optDescr = Option.builder("desc")
//      .hasArg()
//      .argName("descr")
//      .desc("Account description")
//      .longOpt("description")
//      .build();
      
    Option optType = Option.builder("t")
      .hasArg()
      .argName("type")
      .desc("Account type")
      .longOpt("type")
      .build();
        
//    Option optCmdtyCurr = Option.builder("cmdty")
//      .hasArg()
//      .argName("cmdty/curr-id")
//      .desc("Commodity/currency ID")
//      .longOpt("commodity-currency-id")
//      .create();
      
    // The convenient ones
    // ::EMPTY
          
    options = new Options();
    options.addOption(optFileIn);
    options.addOption(optFileOut);
    options.addOption(optMode);
    options.addOption(optExchange);
    options.addOption(optTicker);
    options.addOption(optISIN);
    options.addOption(optName);
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

    cmdty = null;
    if ( mode == Mode.EXCHANGE_TICKER )
    {
      cmdty = gcshFile.getWritableCommodityByQualifID(exchange, ticker);
      if ( cmdty == null )
      {
        System.err.println("Could not find commodities matching that ticker.");
        throw new NoEntryFoundException();
      }
    }
    else if ( mode == Mode.ISIN )
    {
      cmdty = gcshFile.getWritableCommodityByXCode(isin);
      if ( cmdty == null )
      {
        System.err.println("Could not find commodities matching that ISIN.");
        throw new NoEntryFoundException();
      }
    }
    
    doChanges(gcshFile);
    System.err.println("Account after update: " + cmdty.toString());
    
    gcshFile.writeFile(new File(gcshOutFileName));
    
    System.out.println("OK");
  }

  private void doChanges(GnuCashWritableFileImpl gcshFile) throws Exception
  {
    if ( name != null )
    {
      System.err.println("Setting name");
      cmdty.setName(name);
    }
  }

  // -----------------------------------------------------------------

  @Override
  protected void parseCommandLineArgs(String[] args) throws InvalidCommandLineArgsException
  {
    CommandLineParser parser = new DefaultParser();
    CommandLine cmdLine = null;
    try
    {
      cmdLine = parser.parse(options, args);
    }
    catch (ParseException exc)
    {
      System.err.println("Parsing options failed. Reason: " + exc.getMessage());
      throw new InvalidCommandLineArgsException();
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
    
    // <mode>
    try
    {
      mode = Mode.valueOf(cmdLine.getOptionValue("mode"));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <mode>");
      throw new InvalidCommandLineArgsException();
    }
    
    System.err.println("Mode:         " + mode);

    // <exchange>, <ticker>
    if ( cmdLine.hasOption("exchange") )
    {
      if ( ! cmdLine.hasOption("ticker") )
      {
        System.err.println("Error: <exchange> and <ticker> must both either be set or unset");
        throw new InvalidCommandLineArgsException();
      }

      if ( mode != Mode.EXCHANGE_TICKER )
      {
        System.err.println("<exchange> and <ticker> must only be set with <mode> = '" + Mode.EXCHANGE_TICKER.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        exchange = cmdLine.getOptionValue("exchange");
      }
      catch (Exception exc)
      {
        System.err.println("Could not parse <exchange>");
        throw new InvalidCommandLineArgsException();
      }

      try
      {
        ticker = cmdLine.getOptionValue("ticker");
      }
      catch (Exception exc)
      {
        System.err.println("Could not parse <ticker>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( cmdLine.hasOption("ticker") )
      {
        System.err.println("Error: <exchange> and <ticker> must both either be set or unset");
        throw new InvalidCommandLineArgsException();
      }
    }

    System.err.println("Exchange: '" + exchange + "'");
    System.err.println("Ticker:   '" + ticker + "'");

    // <isin>
    if ( cmdLine.hasOption("isin") )
    {
      if ( mode != Mode.ISIN )
      {
        System.err.println("<isin> must only be set with <mode> = '" + Mode.ISIN.toString() + "'");
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
      if ( mode == Mode.ISIN )
      {
        System.err.println("<isin> must be set with <mode> = '" + Mode.ISIN.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    // <name>
    if ( cmdLine.hasOption("name") ) 
    {
      try
      {
        name = cmdLine.getOptionValue("name");
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <name>");
        throw new InvalidCommandLineArgsException();
      }
    }
    System.err.println("Name: '" + name + "'");
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "UpdCmdty", options );
    
    System.out.println("");
    System.out.println("Valid values for <type>:");
    for ( Mode elt : Mode.values() )
      System.out.println(" - " + elt);
  }
}
