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

import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

import org.gnucash.api.currency.ComplexPriceTable;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.tools.CommandLineTool;

public class GetCurrTabInfo extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetCurrTabInfo.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String  gcshFileName = null;
  
  private static boolean scriptMode = false; // ::TODO

  public static void main( String[] args )
  {
    try
    {
      GetCurrTabInfo tool = new GetCurrTabInfo ();
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

    ComplexPriceTable tab = gcshFile.getCurrencyTable();
    System.out.println(tab.toString());
  }

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

    // <GnuCash file>
    try
    {
      gcshFileName = cmdLine.getOptionValue("GnuCash file");
    }
    catch (Exception exc)
    {
      System.err.println("Could not parse <GnuCash file>");
      throw new InvalidCommandLineArgsException();
    }

    if (!scriptMode)
      System.err.println("GnuCash file: '" + gcshFileName + "'");
  }

  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("GetCurrTabInfo", options);
  }
}
