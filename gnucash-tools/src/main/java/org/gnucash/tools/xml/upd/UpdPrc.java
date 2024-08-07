package org.gnucash.tools.xml.upd;

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
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.write.GnuCashWritablePrice;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.tools.CommandLineTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class UpdPrc extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdPrc.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String gcshInFileName = null;
  private static String gcshOutFileName = null;
  private static GCshID prcID = null;

  private static GnuCashPrice.Type   type = null;
  private static GnuCashPrice.Source source = null;
  private static FixedPointNumber    value = null;

  private static GnuCashWritablePrice prc = null;

  public static void main( String[] args )
  {
    try
    {
      UpdPrc tool = new UpdPrc ();
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
    // prcID = UUID.randomUUID();

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
      
    Option optID = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("UUID")
      .withDescription("Price ID")
      .withLongOpt("price-id")
      .create("id");
            
    Option optType = OptionBuilder
      .hasArg()
      .withArgName("type")
      .withDescription("Price type")
      .withLongOpt("type")
      .create("t");
    	    
    Option optSource = OptionBuilder
      .hasArg()
      .withArgName("source")
      .withDescription("Price source")
      .withLongOpt("source")
      .create("s");
    	    	    
    Option optValue = OptionBuilder
      .hasArg()
      .withArgName("value")
      .withDescription("Price value")
      .withLongOpt("val")
      .create("v");
    
    Option optDescr = OptionBuilder
      .hasArg()
      .withArgName("descr")
      .withDescription("Price description")
      .withLongOpt("description")
      .create("desc");
      
    // The convenient ones
    // ::EMPTY
          
    options = new Options();
    options.addOption(optFileIn);
    options.addOption(optFileOut);
    options.addOption(optID);
    options.addOption(optType);
    options.addOption(optSource);
    options.addOption(optValue);
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

    try 
    {
      prc = gcshFile.getWritablePriceByID(prcID);
      System.err.println("Price before update: " + prc.toString());
    }
    catch ( Exception exc )
    {
      System.err.println("Error: Could not find/instantiate price with ID '" + prcID + "'");
      // ::TODO
//      throw new PriceNotFoundException();
      throw new NoEntryFoundException();
    }
    
    doChanges(gcshFile);
    System.err.println("Price after update: " + prc.toString());
    
    gcshFile.writeFile(new File(gcshOutFileName));
    
    System.out.println("OK");
  }

  private void doChanges(GnuCashWritableFileImpl gcshFile) throws Exception
  {
    if ( type != null )
    {
      System.err.println("Setting type");
      prc.setType(type);
    }

    if ( source != null )
    {
      System.err.println("Setting source");
      prc.setSource(source);
    }

    if ( value != null )
    {
      System.err.println("Setting value");
      prc.setValue(value);
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
    
    // <price-id>
    try
    {
      prcID = new GCshID( cmdLine.getOptionValue("price-id") );
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <price-id>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Price ID: " + prcID);

    // <type>
    if ( cmdLine.hasOption("type") ) 
    {
      try
      {
        type = GnuCashPrice.Type.valueOf( cmdLine.getOptionValue("type") );
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <type>");
        throw new InvalidCommandLineArgsException();
      }
    }
    System.err.println("Type: " + type);

    // <source>
    if ( cmdLine.hasOption("source") ) 
    {
      try
      {
    	source = GnuCashPrice.Source.valueOf( cmdLine.getOptionValue("source") );
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <source>");
        throw new InvalidCommandLineArgsException();
      }
    }
    System.err.println("Source: " + source);

    // <value>
    if ( cmdLine.hasOption("value") ) 
    {
      try
      {
        value = new FixedPointNumber( cmdLine.getOptionValue("value") );
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <value>");
        throw new InvalidCommandLineArgsException();
      }
    }
    System.err.println("Value: " + value);
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "UpdPrc", options );
  }
}
