package org.gnucash.tools.xml.get.info;

import java.io.File;
import java.time.LocalDate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshPrcID;
import org.gnucash.tools.CommandLineTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

public class GetPrcInfo extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetPrcInfo.class);
  
  // -----------------------------------------------------------------

  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String     gcshFileName   = null;
  private static GCshPrcID  prcID         = null;
  private static LocalDate  date          = null;
  
  private static boolean showQuotes = false;
  
  private static boolean scriptMode = false; // ::TODO

  // -----------------------------------------------------------------

  public static void main( String[] args )
  {
    try
    {
      GetPrcInfo tool = new GetPrcInfo ();
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
    Option optFile = Option.builder("if")
      .required()
      .hasArg()
      .argName("file")
      .desc("GnuCash file")
      .longOpt("gnucash-file")
      .build();
      
    Option optID = Option.builder("prc")
      .required()
      .hasArg()
      .argName("UUID")
      .desc("Price-ID")
      .longOpt("price-id")
      .build();
    	          
    // The convenient ones
    // ::EMPTY
            
    options = new Options();
    options.addOption(optFile);
    options.addOption(optID);
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
    
    GnuCashPrice prc = null;
    
    prc = gcshFile.getPriceByID(prcID);
    if ( prc == null )
    {
      System.err.println("Could not find a cmdtyurity with this ID.");
      throw new NoEntryFoundException();
    }
    
    // ----------------------------

    try
    {
      System.out.println("toString:          " + prc.toString());
    }
    catch (Exception exc)
    {
      System.out.println("toString:          " + "ERROR");
    }
    
    try
    {
      System.out.println("From cmdty/curr:   " + prc.getFromCmdtyCurrQualifID());
    }
    catch (Exception exc)
    {
      System.out.println("From cmdty/curr:   " + "ERROR");
    }

    try
    {
      System.out.println("To curr:           " + prc.getToCurrencyQualifID());
    }
    catch (Exception exc)
    {
      System.out.println("To curr:           " + "ERROR");
    }

    try
    {
      System.out.println("Date:              " + prc.getDate());
    }
    catch (Exception exc)
    {
      System.out.println("Date:              " + "ERROR");
    }

    try
    {
      System.out.println("Value:             " + prc.getValueFormatted());
    }
    catch (Exception exc)
    {
      System.out.println("Value:             " + "ERROR");
    }

    try
    {
      System.out.println("Type:              " + prc.getType());
    }
    catch (Exception exc)
    {
      System.out.println("Type:              " + "ERROR");
    }

    try
    {
      System.out.println("Source:            " + prc.getSource());
    }
    catch (Exception exc)
    {
      System.out.println("Source:            " + "ERROR");
    }
  }

  // -----------------------------------------------------------------

  @Override
  protected void parseCommandLineArgs(String[] args)
      throws InvalidCommandLineArgsException
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

    // <price-id>
    try
    {
      prcID = new GCshPrcID( cmdLine.getOptionValue("price-id") ); 
      System.err.println("price-ID: " + prcID);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <price-id>");
      throw new InvalidCommandLineArgsException();
    }
    
  }

  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("GetPrcInfo", options);
  }
}
