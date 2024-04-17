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
import org.apache.log4j.Logger;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.tools.CommandLineTool;
import org.gnucash.tools.xml.get.info.GetEmplInfo;
import org.gnucash.tools.xml.helper.Helper;

import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.impl.GnuCashEmployeeImpl;
import org.gnucash.api.write.GnuCashWritableEmployee;
import org.gnucash.api.write.impl.GnuCashWritableEmployeeImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;

public class TestGetEmplInfo extends CommandLineTool
{
  // Logger
  private static Logger logger = Logger.getLogger(TestGetEmplInfo.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String      gcshFileName = null;
  private static Helper.Mode mode         = null;
  private static GCshID      emplID       = null;
  private static String      emplName     = null;
  
  private static boolean showVouchers  = false;
  
  private static boolean scriptMode = false; // ::TODO

  public static void main( String[] args )
  {
    try
    {
      TestGetEmplInfo tool = new TestGetEmplInfo ();
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
      
    Option optMode = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("mode")
      .withDescription("Selection mode")
      .withLongOpt("mode")
      .create("m");
        
    Option optEmplID = OptionBuilder
      .hasArg()
      .withArgName("UUID")
      .withDescription("Employee-ID")
      .withLongOpt("employee-id")
      .create("empl");
    
    Option optEmplName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Employee name")
      .withLongOpt("name")
      .create("n");
      
    // The convenient ones
    Option optShowVch = OptionBuilder
      .withDescription("Show vouchers")
      .withLongOpt("show-vouchers")
      .create("svch");
          
    options = new Options();
    options.addOption(optFile);
    options.addOption(optMode);
    options.addOption(optEmplID);
    options.addOption(optEmplName);
    options.addOption(optShowVch);
  }

  @Override
  protected void getConfigSettings(PropertiesConfiguration cs) throws Exception
  {
    // ::EMPTY
  }
  
  @Override
  protected void kernel() throws Exception
  {
    GnuCashWritableFileImpl gcshFile = new GnuCashWritableFileImpl(new File(gcshFileName));
    
    GnuCashWritableEmployee empl = null;
    if ( mode == Helper.Mode.ID )
    {
      empl = gcshFile.getWritableEmployeeByID(emplID);
      if ( empl == null )
      {
        System.err.println("Found no employee with that name");
        throw new NoEntryFoundException();
      }
    }
    else if ( mode == Helper.Mode.NAME )
    {
      Collection <GnuCashEmployee> emplList = null; 
      emplList = gcshFile.getEmployeesByUserName(emplName);
      if ( emplList.size() == 0 ) 
      {
        System.err.println("Found no employee with that name.");
        throw new NoEntryFoundException();
      }
      else if ( emplList.size() > 1 ) 
      {
        System.err.println("Found several employees with that name.");
        System.err.println("Taking first one.");
      }
      empl = new GnuCashWritableEmployeeImpl( (GnuCashEmployeeImpl) emplList.iterator().next() );
    }
    
    try
    {
      System.out.println("ID:                " + empl.getID());
    }
    catch ( Exception exc )
    {
      System.out.println("ID:                " + "ERROR");
    }
    
    try
    {
      System.out.println("toString:          " + empl.toString());
    }
    catch ( Exception exc )
    {
      System.out.println("toString:          " + "ERROR");
    }
    
    try
    {
      System.out.println("Number:            '" + empl.getNumber() + "'");
    }
    catch ( Exception exc )
    {
      System.out.println("Number:            " + "ERROR");
    }
    
    try
    {
      System.out.println("User name:         '" + empl.getUserName() + "'");
    }
    catch ( Exception exc )
    {
      System.out.println("User name:         " + "ERROR");
    }
    
    try
    {
      System.out.println("Address:           '" + empl.getAddress() + "'");
    }
    catch ( Exception exc )
    {
      System.out.println("Address:           " + "ERROR");
    }
    
    System.out.println("");
    System.out.println("Expenses generated:");
    try
    {
      System.out.println(" - direct:  " + empl.getExpensesGeneratedFormatted());
    }
    catch ( Exception exc )
    {
      System.out.println(" - direct:  " + "ERROR");
    }

    System.out.println("Outstanding value:");
    try
    {
      System.out.println(" - direct: " + empl.getOutstandingValueFormatted());
    }
    catch ( Exception exc )
    {
      System.out.println(" - direct: " + "ERROR");
    }
    
    // ---
    
    if ( showVouchers )
      showInvoices((GnuCashWritableEmployeeImpl) empl);
  }

  // -----------------------------------------------------------------

  private void showInvoices(GnuCashWritableEmployeeImpl empl) throws Exception
  {
    System.out.println("");
    System.out.println("Vouchers:");

    System.out.println("Number of open vouchers: " + empl.getNofOpenVouchers());

    System.out.println("");
    System.out.println("Paid vouchers (direct):");
    for ( GnuCashWritableEmployeeVoucher invc : empl.getPaidWritableVouchers() )
    {
      System.out.println(" - " + invc.toString());
    }

    System.out.println("");
    System.out.println("Unpaid vouchers (direct):");
    for ( GnuCashWritableEmployeeVoucher invc : empl.getUnpaidWritableVouchers() )
    {
      System.out.println(" - " + invc.toString());
    }
    
    // There are no "employee jobs" and thus no paid/unpaid 
    // invoices "via jobs"
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
    
    if ( ! scriptMode )
      System.err.println("GnuCash file: '" + gcshFileName + "'");
    
    // <mode>
    try
    {
      mode = Helper.Mode.valueOf(cmdLine.getOptionValue("mode"));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <mode>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("Mode:         " + mode + "'");

    // <employee-id>
    if ( cmdLine.hasOption("employee-id") )
    {
      if ( mode != Helper.Mode.ID )
      {
        System.err.println("<employee-id> must only be set with <mode> = '" + Helper.Mode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        emplID = new GCshID( cmdLine.getOptionValue("employee-id") );
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <employee-id>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( mode == Helper.Mode.ID )
      {
        System.err.println("<employee-id> must be set with <mode> = '" + Helper.Mode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }      
    }
    
    if ( ! scriptMode )
      System.err.println("Employee ID: '" + emplID + "'");

    // <name>
    if ( cmdLine.hasOption("name") )
    {
      if ( mode != Helper.Mode.NAME )
      {
        System.err.println("<name> must only be set with <mode> = '" + Helper.Mode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        emplName = cmdLine.getOptionValue("name");
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <name>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( mode == Helper.Mode.NAME )
      {
        System.err.println("<name> must be set with <mode> = '" + Helper.Mode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }      
    }
    
    if ( ! scriptMode )
      System.err.println("Name: '" + emplName + "'");

    // <show-vouchers>
    if ( cmdLine.hasOption("show-vouchers"))
    {
      showVouchers = true;
    }
    else
    {
      showVouchers = false;
    }
    
    if ( ! scriptMode )
      System.err.println("Show vouchers: " + showVouchers);
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "TestGetEmplInfo", options );
    
    System.out.println("");
    System.out.println("Valid values for <mode>:");
    for ( Helper.Mode elt : Helper.Mode.values() )
      System.out.println(" - " + elt);
  }
}
