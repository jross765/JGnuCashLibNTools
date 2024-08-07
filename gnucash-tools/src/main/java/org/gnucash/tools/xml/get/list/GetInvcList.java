package org.gnucash.tools.xml.get.list;

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
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.tools.CommandLineTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

public class GetInvcList extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetInvcList.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String         gcshFileName = null;
  private static GCshOwner.Type type         = null;
  
  private static boolean scriptMode = false; // ::TODO

  public static void main( String[] args )
  {
    try
    {
      GetInvcList tool = new GetInvcList ();
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
    Option optFile = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("file")
      .withDescription("GnuCash file")
      .withLongOpt("gnucash-file")
      .create("f");
      
    Option optType = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("type")
      .withDescription("(Generic) invoice type")
      .withLongOpt("type")
      .create("t");
      
    // The convenient ones
    // ::EMPTY
          
    options = new Options();
    options.addOption(optFile);
    options.addOption(optType);
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
    
    Collection<GnuCashGenerInvoice> invcList = null; 
    invcList = gcshFile.getGenerInvoicesByType(type);
    if ( invcList.size() == 0 ) 
    {
    	System.err.println("Found no invoice with that type.");
    	throw new NoEntryFoundException();
    }

    for ( GnuCashGenerInvoice invc : invcList )
    {
    	System.out.println(invc.toString());	
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

    // <gnucash-file>
    try
    {
      gcshFileName = cmdLine.getOptionValue("gnucash-file");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <gnucash-file>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("GnuCash file:      '" + gcshFileName + "'");
    
    // <type>
    try
    {
      type = GCshOwner.Type.valueOf(cmdLine.getOptionValue("type"));
      if ( type != GnuCashGenerInvoice.TYPE_CUSTOMER &&
    	   type != GnuCashGenerInvoice.TYPE_VENDOR &&
    	   type != GnuCashGenerInvoice.TYPE_EMPLOYEE &&
    	   type != GnuCashGenerInvoice.TYPE_JOB )
      {
          System.err.println("Invalid owner type for invoice");
          throw new InvalidCommandLineArgsException();
      }
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <type>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("Type:              " + type);
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GetInvcList", options );
    
    System.out.println("");
    System.out.println("Valid values for <type>:");
    System.out.println(" - " + GnuCashGenerInvoice.TYPE_CUSTOMER);
    System.out.println(" - " + GnuCashGenerInvoice.TYPE_VENDOR);
    System.out.println(" - " + GnuCashGenerInvoice.TYPE_EMPLOYEE);
    System.out.println(" - " + GnuCashGenerInvoice.TYPE_JOB);
  }
}
