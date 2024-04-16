package org.gnucash.tools.xml.gen.complex;

public class ParamTuple
{
	// ---------------------------------------------------------------
	
	public String type;
	public String stockAcctID;
	public String incomeAcctID;
	public String expensesAcctAmtList;
	public String offsetAcctID;
	public String nofStocks;
	public String stockPrc;
	public String divGross;
	public String dateFormat;
	public String datPst;
	public String descr;
	
	// ---------------------------------------------------------------
	
	public ParamTuple()
	{
		type = null;
		stockAcctID = null;
		incomeAcctID = null;
		expensesAcctAmtList = null;
		offsetAcctID = null;
		nofStocks = null;
		stockPrc = null;
		divGross = null;
		dateFormat = null;
		datPst = null;
		descr = null;
	}
	
	public ParamTuple(String type,
					  String stockAcctID,
					  String incomeAcctID,
					  String expensesAcctAmtList,
					  String offsetAcctID,
					  String nofStocks,
					  String stockPrc,
					  String divGross,
					  String dateFormat,
					  String datPst,
					  String descr)
	{
		this.type = type;
		this.stockAcctID = stockAcctID;
		this.incomeAcctID = incomeAcctID;
		this.expensesAcctAmtList = expensesAcctAmtList;
		this.offsetAcctID = offsetAcctID;
		this.nofStocks = nofStocks;
		this.stockPrc = stockPrc;
		this.divGross = divGross;
		this.dateFormat = dateFormat;
		this.datPst = datPst;
		this.descr = descr;
	}

	// ---------------------------------------------------------------
	
	@Override
	public String toString()
	{
		return "ParamTuple [type='" + type + "', " +
		            "stockAcctID='" + stockAcctID + "', " +
		           "incomeAcctID='" + incomeAcctID +  "', " +
		    "expensesAcctAmtList='" + expensesAcctAmtList + "', " +
		           "offsetAcctID='" + offsetAcctID +  "', " +
		              "nofStocks='" + nofStocks +  "', " +
		               "stockPrc='" + stockPrc +  "', " +
		               "divGross='" + divGross +  "', " +
		             "dateFormat='" + dateFormat +  "', " +
		                 "datPst='" + datPst +  "', " +
		                  "descr='" + descr + "']";
	}

}
