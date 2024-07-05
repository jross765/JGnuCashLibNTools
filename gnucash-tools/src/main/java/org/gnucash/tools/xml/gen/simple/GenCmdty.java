package org.gnucash.tools.xml.gen.simple;

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
import org.gnucash.api.write.GnuCashWritableCommodity;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_SecIdType;
import org.gnucash.tools.CommandLineTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

public class GenCmdty extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GenCmdty.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String gcshInFileName = null;
  private static String gcshOutFileName = null;

  private static String  name     = null;
  private static String  symbol   = null;
  private static String  isin     = null;

  public static void main( String[] args )
  {
    try
    {
      GenCmdty tool = new GenCmdty ();
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
      
    Option optISIN = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("isin")
      .withDescription("ISIN")
      .withLongOpt("isin")
      .create("is");
          
    Option optName = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("name")
      .withDescription("Name")
      .withLongOpt("name")
      .create("n");
    
    // The convenient ones
    Option optSymbol = OptionBuilder
      .hasArg()
      .withArgName("symb")
      .withDescription("Symbol (ticker)")
      .withLongOpt("symbol")
      .create("sy");
    	          
          
    options = new Options();
    options.addOption(optFileIn);
    options.addOption(optFileOut);
    options.addOption(optISIN);
    options.addOption(optName);
    options.addOption(optSymbol);
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
    
    GCshCmdtyID_SecIdType qualifID = new GCshCmdtyID_SecIdType(GCshCmdtyCurrNameSpace.SecIdType.ISIN, isin);
    GnuCashWritableCommodity cmdty = gcshFile.createWritableCommodity(qualifID, isin, name);
    // cmdty.setQualifID(qualifID);
    // cmdty.setXCode(isin);
    // cmdty.setName(name);
    
    if ( symbol != null )
    	cmdty.setSymbol(symbol);
    
    System.out.println("Commodity to write: " + cmdty.toString());
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
    
    // <isin>
    try
    {
      isin = cmdLine.getOptionValue("isin");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <isin>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("ISIN: '" + isin + "'");
    
    // <name>
    try
    {
      name = cmdLine.getOptionValue("name");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <name>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Name: '" + name + "'");
    
    // <symbol>
    if ( cmdLine.hasOption("symbol") )
    {
        try
        {
        	symbol = cmdLine.getOptionValue("symbol");
        }
        catch ( Exception exc )
        {
          System.err.println("Could not parse <symbol>");
          throw new InvalidCommandLineArgsException();
        }
    }
    else
    {
    	symbol = null;
    }
    System.err.println("Symbol: '" + symbol + "'");

  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GenCmdty", options );
  }
}
