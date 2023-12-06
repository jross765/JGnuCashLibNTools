package org.gnucash.api;

// Cf. https://raw.githubusercontent.com/Gnucash/gnucash/stable/po/de.po
public class Const_DE {

    public static final String TRX_SPLT_ACTION_INCREASE    = "Zunahme";
    public static final String TRX_SPLT_ACTION_DECREASE    = "Abnahme";
    public static final String TRX_SPLT_ACTION_INTEREST    = "Zinsen";
    public static final String TRX_SPLT_ACTION_PAYMENT     = "Zahlung";
    public static final String TRX_SPLT_ACTION_REBATE      = "Erstattung";
    public static final String TRX_SPLT_ACTION_PAYCHECK    = "Gehalt";
    public static final String TRX_SPLT_ACTION_CREDIT      = "Haben";
    public static final String TRX_SPLT_ACTION_ATM_DEPOSIT = "Automateneinzahlung";
    public static final String TRX_SPLT_ACTION_ATM_DRAW    = "Automatenauszahlung";
    public static final String TRX_SPLT_ACTION_ONLINE      = "Online";
    public static final String TRX_SPLT_ACTION_INVOICE     = "Rechnung";
    public static final String TRX_SPLT_ACTION_BILL        = "Lieferantenrechnung";
    public static final String TRX_SPLT_ACTION_VOUCHER     = "Auslagenerstattung";
    public static final String TRX_SPLT_ACTION_BUY         = "Kauf";
    public static final String TRX_SPLT_ACTION_SELL        = "Verkauf";
    public static final String TRX_SPLT_ACTION_EQUITY      = "Eigenkapital";
    public static final String TRX_SPLT_ACTION_PRICE       = "Preis";
    public static final String TRX_SPLT_ACTION_FEE         = "Gebühr";
    public static final String TRX_SPLT_ACTION_DIVIDEND    = "Dividende";
    // Achtung: Die beiden folgenden stehen genau so in der GnuCash-PO-Datei,
    // weshalb ich sie so uebernehme. Aus meiner Sicht aber eine Fehluebersetzung.
    // Sollte eigentlich heissen: "Buchgewinn" (allgemeiner) oder 
    // "Kursgewinn" (speziell f. Wertpapiere).
    public static final String TRX_SPLT_ACTION_LTCG        = "Zinsen aus langfristigen Kapitalanlagen";
    public static final String TRX_SPLT_ACTION_STCG        = "Zinsen aus kurzfristigen Anlagen";
    public static final String TRX_SPLT_ACTION_INCOME      = "Ertrag";
    public static final String TRX_SPLT_ACTION_DIST        = "Ausschüttung";
    public static final String TRX_SPLT_ACTION_SPLIT       = "Aktienteilung";

    // ---

    public static final String INVC_READ_ONLY_SLOT_TEXT = "Aus einer Rechnung erzeugt. " + 
                                                          "Für Änderungen müssen Sie die Buchung der Rechnung löschen.";

    // ---

    public static final String INVC_ENTR_ACTION_JOB      = "Auftrag";
    public static final String INVC_ENTR_ACTION_MATERIAL = "Material";
    public static final String INVC_ENTR_ACTION_HOURS    = "Stunden";
    
    
}
