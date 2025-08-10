package org.gnucash.api.read.aux;

import java.time.LocalDateTime;

import org.gnucash.api.generated.Slot;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.base.basetypes.simple.GCshAcctID;

/**
 * ::TOOD
 */
public interface GCshAcctReconInfo extends GnuCashObject
                                                  // HasUserDefinedAttributes
{
	public class LastInterval {
		public int days;
		public int months;
		
		@Override
		public String toString() {
			return "LastInterval [days=" + days + ", months=" + months + "]";
		}
	}
	
    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    Slot getJwsdpPeer();

    // -----------------------------------------------------------------

    boolean areChildrenIncluded();

    LocalDateTime getLastDateTime();

    LastInterval getLastInterval();

    // -----------------------------------------------------------------

    /**
     * @return null if the account is below the root
     */
    GCshAcctID getAccountID();
    
    /**
     * @return the account this lot belongs to.
     */
    GnuCashAccount getAccount();

}
