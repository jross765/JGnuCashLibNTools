package org.gnucash.base.basetypes.complex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fully-qualified (real) commodity ID (name space
 * {@link GCshCmdtyCurrNameSpace.Exchange}).
 */
public class GCshCmdtyID_Exchange extends GCshCmdtyID {

	private static final Logger LOGGER = LoggerFactory.getLogger(GCshCmdtyID_Exchange.class);

	// ---------------------------------------------------------------

	private GCshCmdtyCurrNameSpace.Exchange exchange;

	// ---------------------------------------------------------------

	public GCshCmdtyID_Exchange() {
		super();
		type = Type.SECURITY_EXCHANGE;
		exchange = GCshCmdtyCurrNameSpace.Exchange.UNSET;
	}

	public GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange exchange, String secCode) {

		super(exchange.toString(), secCode);

		setType(Type.SECURITY_EXCHANGE);
		setExchange(exchange);
	}

	public GCshCmdtyID_Exchange(String nameSpace, String code) {

		super(nameSpace, code);

		setType(Type.SECURITY_EXCHANGE);
		setExchange(nameSpace);
	}

	// ---------------------------------------------------------------

	@Override
	public void setType(Type type) {
//        if ( type != Type.SECURITY_EXCHANGE )
//            throw new InvalidCmdtyCurrIDException();

		super.setType(type);
	}

	// ----------------------------

	public GCshCmdtyCurrNameSpace.Exchange getExchange() {
		if ( type != Type.SECURITY_EXCHANGE )
			throw new InvalidCmdtyCurrTypeException();

		return exchange;
	}

	public void setExchange(GCshCmdtyCurrNameSpace.Exchange exchange) {
		if ( type != Type.SECURITY_EXCHANGE )
			throw new InvalidCmdtyCurrTypeException();

		this.exchange = exchange;
	}

	public void setExchange(String exchangeStr) {
		if ( exchangeStr == null )
			throw new IllegalArgumentException("Exchange string is null");

		if ( exchangeStr.trim().equals("") )
			throw new IllegalArgumentException("Exchange string is empty");

		setExchange(GCshCmdtyCurrNameSpace.Exchange.valueOf(exchangeStr.trim()));
	}

	// ---------------------------------------------------------------

	public static GCshCmdtyID_Exchange parse(String str) {
		if ( str == null )
			throw new IllegalArgumentException("Argument string is null");

		if ( str.equals("") )
			throw new IllegalArgumentException("Argument string is empty");

		GCshCmdtyID_Exchange result = new GCshCmdtyID_Exchange();

		int posSep = str.indexOf(SEPARATOR);
		// Plausi ::MAGIC
		if ( posSep <= 3 || 
			 posSep >= str.length() - 2 )
			throw new InvalidCmdtyCurrIDException();

		String nameSpaceLoc = str.substring(0, posSep).trim();
		String currSecCodeLoc = str.substring(posSep + 1, str.length()).trim();

		if ( nameSpaceLoc.equals(GCshCmdtyCurrNameSpace.CURRENCY) ) {
			throw new InvalidCmdtyCurrTypeException();
		} else {
			result.setType(Type.SECURITY_EXCHANGE);
			result.setNameSpace(nameSpaceLoc);
			result.setExchange(nameSpaceLoc);
			result.setCode(currSecCodeLoc);
		}

		return result;
	}

	// ---------------------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((nameSpace == null) ? 0 : nameSpace.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		GCshCmdtyID_Exchange other = (GCshCmdtyID_Exchange) obj;
		if ( type != other.type )
			return false;
		if ( exchange != other.exchange )
			return false;
		if ( nameSpace == null ) {
			if ( other.nameSpace != null )
				return false;
		} else if ( !nameSpace.equals(other.nameSpace) )
			return false;
		if ( code == null ) {
			if ( other.code != null )
				return false;
		} else if ( !code.equals(other.code) )
			return false;
		return true;
	}

	// ---------------------------------------------------------------

	@Override
	public String toString() {
		return toStringShort();
	}

	@Override
	public String toStringShort() {
		if ( type != Type.SECURITY_EXCHANGE )
			return "ERROR";

		String result = exchange.toString() + SEPARATOR + code;

		return result;
	}

	@Override
	public String toStringLong() {
		if ( type != Type.SECURITY_EXCHANGE )
			return "ERROR";

		String result = "GCshCmdtyID_Exchange [";

		result += "namespace='" + getNameSpace() + "'";

		try {
			result += ", exchange='" + getExchange() + "'";
		} catch (InvalidCmdtyCurrTypeException e) {
			result += ", exchange=" + "ERROR";
		}

		result += ", secCode='" + getCode() + "'";

		result += "]";

		return result;
	}

}
