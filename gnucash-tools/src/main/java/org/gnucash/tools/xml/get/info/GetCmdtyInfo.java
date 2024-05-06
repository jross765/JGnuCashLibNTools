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
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.tools.CommandLineTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

public class GetCmdtyInfo extends CommandLineTool
{
  enum Mode
  {
    EXCHANGE_TICKER,
    ISIN,
    NAME
  }

  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetCmdtyInfo.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String  gcshFileName = null;
  private static Mode    mode     = null;
  private static String  exchange = null;
  private static String  ticker   = null;
  private static String  name     = null;
  private static String  isin     = null;
  
  private static boolean showQuotes = false;
  
  private static boolean scriptMode = false; // ::TODO

  public static void main( String[] args )
  {
    try
    {
      GetCmdtyInfo tool = new GetCmdtyInfo ();
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
         
    Option optExchange = OptionBuilder
      .hasArg()
      .withArgName("exch")
      .withDescription("Exchange code")
      .withLongOpt("exchange")
      .create("exch");
      
    Option optTicker = OptionBuilder
      .hasArg()
      .withArgName("ticker")
      .withDescription("Ticker")
      .withLongOpt("ticker")
      .create("tkr");
      
    Option optISIN = OptionBuilder
      .hasArg()
      .withArgName("isin")
      .withDescription("ISIN")
      .withLongOpt("isin")
      .create("is");
        
    Option optName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Name (or part of)")
      .withLongOpt("name")
      .create("n");
          
    // The convenient ones
    Option optShowQuote = OptionBuilder
      .withDescription("Show quotes")
      .withLongOpt("show-quotes")
      .create("squt");
            
    options = new Options();
    options.addOption(optFile);
    options.addOption(optMode);
    options.addOption(optExchange);
    options.addOption(optTicker);
    options.addOption(optISIN);
    options.addOption(optName);
    options.addOption(optShowQuote);
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

    GnuCashCommodity cmdty = null;
    if ( mode == Mode.EXCHANGE_TICKER )
    {
      cmdty = gcshFile.getCommodityByQualifID(exchange, ticker);
      if ( cmdty == null )
      {
        System.err.println("Could not find commodities matching that ticker.");
        throw new NoEntryFoundException();
      }
    }
    else if ( mode == Mode.ISIN )
    {
      cmdty = gcshFile.getCommodityByXCode(isin);
      if ( cmdty == null )
      {
        System.err.println("Could not find commodities matching that ISIN.");
        throw new NoEntryFoundException();
      }
    }
    else if ( mode == Mode.NAME )
    {
      Collection<GnuCashCommodity> cmdtyList = gcshFile.getCommoditiesByName(name); 
      if ( cmdtyList.size() == 0 )
      {
        System.err.println("Could not find commodities matching that name.");
        throw new NoEntryFoundException();
      }
      if ( cmdtyList.size() > 1 )
      {
        System.err.println("Found " + cmdtyList.size() + " commodities matching that name.");
        System.err.println("Please specify more precisely.");
        throw new TooManyEntriesFoundException();
      }
      cmdty = cmdtyList.iterator().next(); // first element
    }

    try
    {
      System.out.println("Qualified ID:      '" + cmdty.getQualifID() + "'");
    }
    catch (Exception exc)
    {
      System.out.println("Qualified ID:      " + "ERROR");
    }

    try
    {
      System.out.println("toString:          " + cmdty.toString());
    }
    catch (Exception exc)
    {
      System.out.println("toString:          " + "ERROR");
    }
    
    try
    {
      System.out.println("X-Code:            " + cmdty.getXCode());
    }
    catch (Exception exc)
    {
      System.out.println("X-Code :           " + "ERROR");
    }

    try
    {
      System.out.println("Name:              '" + cmdty.getName() + "'");
    }
    catch (Exception exc)
    {
      System.out.println("Name:              " + "ERROR");
    }

    try
    {
      System.out.println("Fraction:          " + cmdty.getFraction());
    }
    catch (Exception exc)
    {
      System.out.println("Fraction:          " + "ERROR");
    }

    // ---

    showQuotes(cmdty);
  }

  // -----------------------------------------------------------------

  private void showQuotes(GnuCashCommodity cmdty)
  {
    System.out.println("");
    System.out.println("Quotes:");

    System.out.println("");
    System.out.println("Number of quotes: " + cmdty.getQuotes().size());
    
    System.out.println("");
    for (GnuCashPrice prc : cmdty.getQuotes())
    {
      System.out.println(" - " + prc.toString());
    }

    System.out.println("");
    System.out.println("Youngest Quote:");
    System.out.println(cmdty.getYoungestQuote());
  }

  // -----------------------------------------------------------------

  @Override
  protected void parseCommandLineArgs(String[] args)
      throws InvalidCommandLineArgsException
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
    catch (Exception exc)
    {
      System.err.println("Could not parse <gnucash-file>");
      throw new InvalidCommandLineArgsException();
    }

    if (!scriptMode)
      System.err.println("GnuCash file: '" + gcshFileName + "'");

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
    
    if ( ! scriptMode )
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

    if (!scriptMode)
    {
      System.err.println("Exchange: '" + exchange + "'");
      System.err.println("Ticker:   '" + ticker + "'");
    }

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
      if ( mode != Mode.NAME )
      {
        System.err.println("<name> must only be set with <mode> = '" + Mode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        name = cmdLine.getOptionValue("name");
      }
      catch (Exception exc)
      {
        System.err.println("Could not parse <name>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( mode == Mode.NAME )
      {
        System.err.println("<name> must be set with <mode> = '" + Mode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    // <show-quotes>
    if (cmdLine.hasOption("show-quotes"))
    {
      showQuotes = true;
    }
    else
    {
      showQuotes = false;
    }

    if (!scriptMode)
      System.err.println("Show quotes: " + showQuotes);
  }

  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("GetCmdtyInfo", options);
    
    System.out.println("");
    System.out.println("Valid values for <mode>:");
    for ( Mode elt : Mode.values() )
      System.out.println(" - " + elt);
  }
}
