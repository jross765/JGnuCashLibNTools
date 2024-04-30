package org.gnucash.tools.xml.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.tuples.AcctIDAmountPair;

import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;
import xyz.schnorxoborx.base.dateutils.DateHelpers;
import xyz.schnorxoborx.base.dateutils.LocalDateHelpers;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class CmdLineHelper
{
  // What-String
  final static String __VERSION_STRING = "@(#) $Header: svn://gila_svn_priv/finanzen/GnuCashToolsXML/trunk/GnuCashToolsXML/src/main/java/de/riegelmuenchen/gnucash/tools/xml/helper/CmdLineHelper.java 9350 2024-04-16 08:37:25Z thilo $";

  // final static final String SEPARATOR = ";";

  // -----------------------------------------------------------------
  
  public static Helper.DateFormat getDateFormat(CommandLine cmdLine, String argName) throws InvalidCommandLineArgsException
  {
    Helper.DateFormat dateFormat;
    
    if ( cmdLine.hasOption(argName) )
    {
      try
      {
        dateFormat = Helper.DateFormat.valueOf(cmdLine.getOptionValue(argName));
      }
      catch (Exception exc)
      {
        System.err.println("Error: Could not parse <" + argName + ">");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      dateFormat = Helper.DateFormat.ISO_8601;
    }
    
    return dateFormat;
  }

  public static Helper.DateFormat getDateFormat(String arg, String argName) throws InvalidCommandLineArgsException
  {
    Helper.DateFormat dateFormat;
    
    if ( arg != null )
    {
      try
      {
        dateFormat = Helper.DateFormat.valueOf(arg);
      }
      catch (Exception exc)
      {
        System.err.println("Error: Could not parse <" + argName + ">");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      dateFormat = Helper.DateFormat.ISO_8601;
    }
    
    return dateFormat;
  }
  
  // ------------------------------

  public static LocalDate getDate(CommandLine cmdLine, String argName,
		  						  Helper.DateFormat dateFormat) throws InvalidCommandLineArgsException
  {
    LocalDate datum = LocalDate.now();
    
    try
    {
      if ( dateFormat == Helper.DateFormat.ISO_8601 )
        datum = LocalDateHelpers.parseLocalDate(cmdLine.getOptionValue(argName), DateHelpers.DATE_FORMAT_2);
      else if ( dateFormat == Helper.DateFormat.DE )
        datum = LocalDateHelpers.parseLocalDate(cmdLine.getOptionValue(argName));
    }
    catch (Exception exc)
    {
      System.err.println("Error: Could not parse <" + argName + ">");
      throw new InvalidCommandLineArgsException();
    }
    
    return datum;
  }

  public static LocalDate getDate(String arg, String argName,
			  					  Helper.DateFormat dateFormat) throws InvalidCommandLineArgsException
	{
		LocalDate datum = LocalDate.now();

		try
		{
			if ( dateFormat == Helper.DateFormat.ISO_8601 )
				datum = LocalDateHelpers.parseLocalDate( arg, DateHelpers.DATE_FORMAT_2 );
			else if ( dateFormat == Helper.DateFormat.DE )
				datum = LocalDateHelpers.parseLocalDate( arg );
		} catch ( Exception exc )
		{
			System.err.println( "Error: Could not parse <" + argName + ">" );
			throw new InvalidCommandLineArgsException();
		}

		return datum;
	}

  // -----------------------------------------------------------------
  
  public static Collection<AcctIDAmountPair> getExpAcctAmtMulti(CommandLine cmdLine, String argName) throws InvalidCommandLineArgsException
  {
    List<AcctIDAmountPair> result = new ArrayList<AcctIDAmountPair>();

    if ( cmdLine.hasOption(argName) )
    {
	    try
        {
        	String temp = cmdLine.getOptionValue(argName);
    	    // System.err.println("*** expacctamt: '" + temp + "' ***");

        	String[] pairListArr = temp.split("\\|");
    	    // System.err.println("*** arr-size: " + pairListArr.length);
        	for ( String pairStr : pairListArr )
        	{
        	    // System.err.println("*** pair: '" + pairStr + "'");
        		int pos = pairStr.indexOf(";");
        		if ( pos < 0 )
        		{
        			System.err.println("Error: List element '" + pairStr + "' does not contain the separator");
        			throw new InvalidCommandLineArgsException();
        		}
        		String acctIDStr = pairStr.substring(0, pos);
        		String amtStr    = pairStr.substring(pos + 1);
        		// System.err.println(" - elt1: '" + acctIDStr + "'/'" + amtStr + "'");
        		
        		GCshID acctID = new GCshID(acctIDStr);
        		Double amtDbl = Double.valueOf(amtStr);
        		// System.err.println(" - elt2: " + acctIDStr + " / " + amtStr);
        		
        		AcctIDAmountPair newPair = new AcctIDAmountPair(acctID, new FixedPointNumber(amtDbl));
        		result.add(newPair);
        	}
        }
        catch (Exception e)
        {
        	System.err.println("Could not parse <" + argName + ">");
        	throw new InvalidCommandLineArgsException();
        }
    }
    else
    {
    	// ::EMPTY
    }

    return result;
  }

  public static Collection<AcctIDAmountPair> getExpAcctAmtMulti(String arg, String argName) throws InvalidCommandLineArgsException
  {
    List<AcctIDAmountPair> result = new ArrayList<AcctIDAmountPair>();

    if ( arg != null )
    {
	    try
        {
        	String temp = arg;
    	    // System.err.println("*** expacctamt: '" + temp + "' ***");

        	String[] pairListArr = temp.split("\\|");
    	    // System.err.println("*** arr-size: " + pairListArr.length);
        	for ( String pairStr : pairListArr )
        	{
        	    // System.err.println("*** pair: '" + pairStr + "'");
        		int pos = pairStr.indexOf(";");
        		if ( pos < 0 )
        		{
        			System.err.println("Error: List element '" + pairStr + "' does not contain the separator");
        			throw new InvalidCommandLineArgsException();
        		}
        		String acctIDStr = pairStr.substring(0, pos);
        		String amtStr    = pairStr.substring(pos + 1);
        		// System.err.println(" - elt1: '" + acctIDStr + "'/'" + amtStr + "'");
        		
        		GCshID acctID = new GCshID(acctIDStr);
        		Double amtDbl = Double.valueOf(amtStr);
        		// System.err.println(" - elt2: " + acctIDStr + " / " + amtStr);
        		
        		AcctIDAmountPair newPair = new AcctIDAmountPair(acctID, new FixedPointNumber(amtDbl));
        		result.add(newPair);
        	}
        }
        catch (Exception e)
        {
        	System.err.println("Could not parse <" + argName + ">");
        	throw new InvalidCommandLineArgsException();
        }
    }
    else
    {
    	// ::EMPTY
    }

    return result;
  }

}
