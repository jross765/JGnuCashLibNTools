package org.gnucash.tools.xml.gen.simple;

import java.io.File;
import java.time.LocalDate;

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

import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.tools.CommandLineTool;
import org.gnucash.tools.xml.helper.CmdLineHelper;
import org.gnucash.tools.xml.helper.Helper;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.write.GnuCashWritablePrice;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;

public class GenPrc extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GenPrc.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String gcshInFileName = null;
  private static String gcshOutFileName = null;

  private static GCshCmdtyCurrID   fromCmdtyCurrID = null;
  private static GCshCurrID        toCurrID = null;
  private static Helper.DateFormat dateFormat    = null;
  private static LocalDate         date = null;
  private static FixedPointNumber  value = null;
  private static GnuCashPrice.Source  source = null;

  public static void main( String[] args )
  {
    try
    {
      GenPrc tool = new GenPrc ();
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
    // invcID = UUID.randomUUID();

//    cfg = new PropertiesConfiguration(System.getProperty("config"));
//    getConfigSettings(cfg);

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
      
    Option optFromCmdtyCurr= OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("cmdty/curr")
      .withDescription("From-commodity/currency")
      .withLongOpt("from-cmdty-curr")
      .create("f");
          
    Option optToCurr = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("curr")
      .withDescription("To-currency")
      .withLongOpt("to-curr")
      .create("t");
    
    Option optDateFormat = OptionBuilder
      .hasArg()
      .withArgName("date-format")
      .withDescription("Date format")
      .withLongOpt("date-format")
      .create("df");
            
    Option optDate = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("date")
      .withDescription("Date")
      .withLongOpt("date")
      .create("dat");
          
    Option optValue = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("value")
      .withDescription("Value")
      .withLongOpt("value")
      .create("v");
            
              
    // The convenient ones
    Option optSource = OptionBuilder
      .hasArg()
      .withArgName("source")
      .withDescription("Source")
      .withLongOpt("source")
      .create("src");
          
    options = new Options();
    options.addOption(optFileIn);
    options.addOption(optFileOut);
    options.addOption(optFromCmdtyCurr);
    options.addOption(optToCurr);
    options.addOption(optDateFormat);
    options.addOption(optDate);
    options.addOption(optValue);
    options.addOption(optSource);
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
    
    GnuCashWritablePrice prc = gcshFile.createWritablePrice(fromCmdtyCurrID, toCurrID, date);
    // prc.setFromCmdtyCurrQualifID(fromCmdtyCurrID);
    // prc.setToCurrencyQualifID(toCurrID);
    prc.setType(GnuCashPrice.Type.LAST);
    // prc.setDate(date);
    prc.setValue(value);
    prc.setSource(source);
    
    System.out.println("Price to write: " + prc.toString());
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
    
    // <from-cmdty-curr>
    try
    {
      fromCmdtyCurrID = GCshCmdtyCurrID.parse(cmdLine.getOptionValue("from-cmdty-curr")); 
      System.err.println("from-cmdty-curr: " + fromCmdtyCurrID);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <from-cmdty-curr>");
      throw new InvalidCommandLineArgsException();
    }
    
    // <to-curr>
    try
    {
      toCurrID = GCshCurrID.parse(cmdLine.getOptionValue("to-curr")); 
      System.err.println("to-curr: " + toCurrID);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <name>");
      throw new InvalidCommandLineArgsException();
    }
    
    // <date-format>
    dateFormat = CmdLineHelper.getDateFormat(cmdLine);
    System.err.println("date-format: " + dateFormat);

    // <date>
    try
    {
      date = CmdLineHelper.getDate(cmdLine, dateFormat); 
      System.err.println("date: " + date);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <name>");
      throw new InvalidCommandLineArgsException();
    }
    
    // <value>
    try
    {
      value = new FixedPointNumber( Double.parseDouble( cmdLine.getOptionValue("value") ) ) ; 
      System.err.println("value: " + value);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <name>");
      throw new InvalidCommandLineArgsException();
    }
    
    // <source>
    if ( cmdLine.hasOption("source") )
    {
      try
      {
        source = GnuCashPrice.Source.valueOf( cmdLine.getOptionValue("source") ); 
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <name>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      source = GnuCashPrice.Source.USER_PRICE;
    }
    System.err.println("source: " + source);
    
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GenPrc", options );
    
    System.out.println("");
    System.out.println("Valid values for <source>:");
    for ( GnuCashPrice.Source elt : GnuCashPrice.Source.values() )
      System.out.println(" - " + elt);
  }
}
