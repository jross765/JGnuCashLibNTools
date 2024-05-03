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
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.tools.CommandLineTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.Helper;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

public class GetVendInfo extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetVendInfo.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String      gcshFileName = null;
  private static Helper.Mode mode         = null;
  private static GCshID      vendID       = null;
  private static String      vendName     = null;
  
  private static boolean showJobs   = false;
  private static boolean showBills  = false;
  
  private static boolean scriptMode = false; // ::TODO

  public static void main( String[] args )
  {
    try
    {
      GetVendInfo tool = new GetVendInfo ();
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
        
    Option optVendID = OptionBuilder
      .hasArg()
      .withArgName("UUID")
      .withDescription("Vendor-ID")
      .withLongOpt("vendor-id")
      .create("vend");
    
    Option optVendName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Vendor name")
      .withLongOpt("name")
      .create("n");
      
    // The convenient ones
    Option optShowJob = OptionBuilder
      .withDescription("Show jobs")
      .withLongOpt("show-jobs")
      .create("sjob");
              
    Option optShowBll = OptionBuilder
      .withDescription("Show bills")
      .withLongOpt("show-bills")
      .create("sbll");
          
    options = new Options();
    options.addOption(optFile);
    options.addOption(optMode);
    options.addOption(optVendID);
    options.addOption(optVendName);
    options.addOption(optShowJob);
    options.addOption(optShowBll);
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
    
    GnuCashVendor vend = null;
    if ( mode == Helper.Mode.ID )
    {
      vend = gcshFile.getVendorByID(vendID);
      if ( vend == null )
      {
        System.err.println("Found no vendor with that name");
        throw new NoEntryFoundException();
      }
    }
    else if ( mode == Helper.Mode.NAME )
    {
      Collection <GnuCashVendor> vendList = null; 
      vendList = gcshFile.getVendorsByName(vendName);
      if ( vendList.size() == 0 ) 
      {
        System.err.println("Could not find vendors matching that name.");
        throw new NoEntryFoundException();
      }
      else if ( vendList.size() > 1 ) 
      {
          System.err.println("Found " + vendList.size() + " vendors matching that name.");
          System.err.println("Please specify more precisely.");
          throw new TooManyEntriesFoundException();
      }
      vend = vendList.iterator().next();
    }
    
    try
    {
      System.out.println("ID:                " + vend.getID());
    }
    catch ( Exception exc )
    {
      System.out.println("ID:                " + "ERROR");
    }
    
    try
    {
      System.out.println("Number:            '" + vend.getNumber() + "'");
    }
    catch ( Exception exc )
    {
      System.out.println("Number:            " + "ERROR");
    }
    
    try
    {
      System.out.println("Name:              '" + vend.getName() + "'");
    }
    catch ( Exception exc )
    {
      System.out.println("Name:              " + "ERROR");
    }
    
    try
    {
      System.out.println("Address:           '" + vend.getAddress() + "'");
    }
    catch ( Exception exc )
    {
      System.out.println("Address:           " + "ERROR");
    }
    
    System.out.println("");
    try
    {
      GCshID taxTabID = vend.getTaxTableID();
      System.out.println("Tax table ID:      " + taxTabID);
      
      if ( vend.getTaxTableID() != null )
      {
        try 
        {
          GCshTaxTable taxTab = gcshFile.getTaxTableByID(taxTabID);
          System.out.println("Tax table:        " + taxTab.toString());
        }
        catch ( Exception exc2 )
        {
          System.out.println("Tax table:        " + "ERROR");
        }
      }
    }
    catch ( Exception exc )
    {
      System.out.println("Tax table ID:      " + "ERROR");
    }
    
    System.out.println("");
    try
    {
      GCshID bllTrmID = vend.getTermsID();
      System.out.println("Bill terms ID:     " + bllTrmID);
      
      if ( vend.getTermsID() != null )
      {
        try 
        {
          GCshBillTerms bllTrm = gcshFile.getBillTermsByID(bllTrmID);
          System.out.println("Bill Terms:        " + bllTrm.toString());
        }
        catch ( Exception exc2 )
        {
          System.out.println("Bill Terms:        " + "ERROR");
        }
      }
    }
    catch ( Exception exc )
    {
      System.out.println("Bill terms ID:     " + "ERROR");
    }
    
    System.out.println("");
    System.out.println("Expenses generated:");
    try
    {
      System.out.println(" - direct: " + vend.getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant.DIRECT));
    }
    catch ( Exception exc )
    {
      System.out.println(" - direct: " + "ERROR");
    }

    try
    {
      System.out.println(" - via all jobs: " + vend.getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant.VIA_JOB));
    }
    catch ( Exception exc )
    {
      System.out.println(" - via all jobs: " + "ERROR");
    }

    System.out.println("Outstanding value:");
    try
    {
      System.out.println(" - direct: " + vend.getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant.DIRECT));
    }
    catch ( Exception exc )
    {
      System.out.println(" - direct: " + "ERROR");
    }
    
    try
    {
      System.out.println(" - via all jobs: " + vend.getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant.VIA_JOB));
    }
    catch ( Exception exc )
    {
      System.out.println(" - via all jobs: " + "ERROR");
    }
    
    // ---
    
    if ( showJobs )
      showJobs(vend);
        
    if ( showBills )
      showBills(vend);
  }

  // -----------------------------------------------------------------

  private void showJobs(GnuCashVendor vend)
  {
    System.out.println("");
    System.out.println("Jobs:");
    for ( GnuCashVendorJob job : vend.getJobs() )
    {
      System.out.println(" - " + job.toString());
    }
  }

  private void showBills(GnuCashVendor vend) throws Exception
  {
    System.out.println("");
    System.out.println("Bills:");
    
    System.out.println("Number of open bills: " + vend.getNofOpenBills());
    
    System.out.println("");
    System.out.println("Paid bills (direct):");
    for ( GnuCashVendorBill bll: vend.getPaidBills_direct() )
    {
      System.out.println(" - " + bll.toString());
    }

    System.out.println("");
    System.out.println("Paid bills (via all jobs):");
    for ( GnuCashJobInvoice bll: vend.getPaidBills_viaAllJobs() )
    {
      System.out.println(" - " + bll.toString());
    }

    System.out.println("");
    System.out.println("Unpaid bills (direct):");
    for ( GnuCashVendorBill bll : vend.getUnpaidBills_direct() )
    {
      System.out.println(" - " + bll.toString());
    }

    System.out.println("");
    System.out.println("Unpaid bills (via all jobs):");
    for ( GnuCashJobInvoice bll : vend.getUnpaidBills_viaAllJobs() )
    {
      System.out.println(" - " + bll.toString());
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

    // <vendor-id>
    if ( cmdLine.hasOption("vendor-id") )
    {
      if ( mode != Helper.Mode.ID )
      {
        System.err.println("<vendor-id> must only be set with <mode> = '" + Helper.Mode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        vendID = new GCshID( cmdLine.getOptionValue("vendor-id") );
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <vendor-id>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( mode == Helper.Mode.ID )
      {
        System.err.println("<vendor-id> must be set with <mode> = '" + Helper.Mode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }      
    }
    
    if ( ! scriptMode )
      System.err.println("Vendor ID: '" + vendID + "'");

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
        vendName = cmdLine.getOptionValue("name");
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
      System.err.println("Name: '" + vendName + "'");

    // <show-bills>
    if ( cmdLine.hasOption("show-bills"))
    {
      showBills = true;
    }
    else
    {
      showBills = false;
    }
    
    if ( ! scriptMode )
      System.err.println("Show bills: " + showBills);
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GetVendInfo", options );
    
    System.out.println("");
    System.out.println("Valid values for <mode>:");
    for ( Helper.Mode elt : Helper.Mode.values() )
      System.out.println(" - " + elt);
  }
}
