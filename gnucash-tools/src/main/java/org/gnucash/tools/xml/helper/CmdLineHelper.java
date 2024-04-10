package org.gnucash.tools.xml.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.base.tuples.AcctIDAmountPair;

import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;
import xyz.schnorxoborx.base.dateutils.DateHelpers;
import xyz.schnorxoborx.base.dateutils.LocalDateHelpers;

public class CmdLineHelper
{
  public static Helper.DateFormat getDateFormat(CommandLine cmdLine) throws InvalidCommandLineArgsException
  {
    Helper.DateFormat dateFormat;
    
    if ( cmdLine.hasOption("date-format") )
    {
      try
      {
        dateFormat = Helper.DateFormat.valueOf(cmdLine.getOptionValue("date-format"));
      }
      catch (Exception exc)
      {
        System.err.println("Error: Could not parse <date-format>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      dateFormat = Helper.DateFormat.ISO_8601;
    }
    
    return dateFormat;
  }

  public static LocalDate getDate(CommandLine cmdLine, Helper.DateFormat dateFormat) throws InvalidCommandLineArgsException
  {
    LocalDate datum = LocalDate.now();
    
    try
    {
      if ( dateFormat == Helper.DateFormat.ISO_8601 )
        datum = LocalDateHelpers.parseLocalDate(cmdLine.getOptionValue("date"), DateHelpers.DATE_FORMAT_2);
      else if ( dateFormat == Helper.DateFormat.DE )
        datum = LocalDateHelpers.parseLocalDate(cmdLine.getOptionValue("date"));
    }
    catch (Exception exc)
    {
      System.err.println("Error: Could not parse <datum>");
      throw new InvalidCommandLineArgsException();
    }
    
    return datum;
  }

  // -----------------------------------------------------------------
  
  public static Collection<AcctIDAmountPair> getExpAcctAmtMulti(CommandLine cmdLine) throws InvalidCommandLineArgsException
  {
    List<AcctIDAmountPair> result = new ArrayList<AcctIDAmountPair>();

    // <expense-account-amounts>
    if ( cmdLine.hasOption("expense-account-amounts") )
    {
        try
        {
        	String temp = cmdLine.getOptionValue("expense-account-amounts");
        	String[] pairListArr = temp.split("\\|");
        	for ( String pairStr : pairListArr )
        	{
        		int pos = pairStr.indexOf(";");
        		if ( pos < 0 )
        		{
        			System.err.println("Error: List element '" + pairStr + "' does not contain the separator");
        			throw new InvalidCommandLineArgsException();
        		}
        		String acctIDStr = pairStr.substring(0, pos);
        		String amtStr    = pairStr.substring(pos + 1);
        		// System.err.println(" - elt1: '" + acctIDStr + "'/'" + amtStr + "' (pos " + pos + ")");
        		
        		GCshID acctID = new GCshID(acctIDStr);
        		Double amtDbl = Double.valueOf(amtStr);
        		// System.err.println(" - elt2: " + acctIDStr + " / " + amtStr);
        		
        		AcctIDAmountPair newPair = new AcctIDAmountPair(acctID, new FixedPointNumber(amtDbl));
        		result.add(newPair);
        	}
        }
        catch (Exception e)
        {
        	System.err.println("Could not parse <expense-account-amounts>");
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
