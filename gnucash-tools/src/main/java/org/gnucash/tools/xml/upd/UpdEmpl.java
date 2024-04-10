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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.tools.CommandLineTool;
import org.gnucash.api.read.NoEntryFoundException;
// ::TODO
// import org.gnucash.api.read.EmployeeNotFoundException;
import org.gnucash.api.write.GnuCashWritableEmployee;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;

public class UpdEmpl extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdEmpl.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String gcshInFileName = null;
  private static String gcshOutFileName = null;
  private static GCshID emplID = null;

  private static String number = null;
  private static String name = null;

  private static GnuCashWritableEmployee empl = null;

  public static void main( String[] args )
  {
    try
    {
      UpdEmpl tool = new UpdEmpl ();
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
    // emplID = UUID.randomUUID();

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
      .withDescription("Employee ID")
      .withLongOpt("employee-id")
      .create("id");
            
    Option optNumber = OptionBuilder
      .hasArg()
      .withArgName("number")
      .withDescription("Employee number")
      .withLongOpt("number")
      .create("num");
    	    
    Option optName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Employee user name")
      .withLongOpt("name")
      .create("nam");
    
    // The convenient ones
    // ::EMPTY
          
    options = new Options();
    options.addOption(optFileIn);
    options.addOption(optFileOut);
    options.addOption(optID);
    options.addOption(optNumber);
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

    try 
    {
      empl = gcshFile.getWritableEmployeeByID(emplID);
      System.err.println("Employee before update: " + empl.toString());
    }
    catch ( Exception exc )
    {
      System.err.println("Error: Could not find/instantiate employee with ID '" + emplID + "'");
      // ::TODO
//      throw new EmployeeNotFoundException();
      throw new NoEntryFoundException();
    }
    
    doChanges(gcshFile);
    System.err.println("Employee after update: " + empl.toString());
    
    gcshFile.writeFile(new File(gcshOutFileName));
    
    System.out.println("OK");
  }

  private void doChanges(GnuCashWritableFileImpl gcshFile) throws Exception
  {
    if ( number != null )
    {
      System.err.println("Setting number");
      empl.setNumber(number);
    }

    if ( name != null )
    {
      System.err.println("Setting user name");
      empl.setUserName(name);
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
    
    // <employee-id>
    try
    {
      emplID = new GCshID( cmdLine.getOptionValue("employee-id") );
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <employee-id>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Employee ID: " + emplID);

    // <number>
    if ( cmdLine.hasOption("number") ) 
    {
      try
      {
        number = cmdLine.getOptionValue("number");
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <number>");
        throw new InvalidCommandLineArgsException();
      }
    }
    System.err.println("Number: '" + number + "'");

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
    formatter.printHelp( "UpdEmpl", options );
  }
}
