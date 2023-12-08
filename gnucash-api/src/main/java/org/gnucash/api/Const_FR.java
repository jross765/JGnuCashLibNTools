package org.gnucash.api;

//Cf. https://raw.githubusercontent.com/Gnucash/gnucash/stable/po/fr.po
public class Const_FR {

    public static final String TRX_SPLT_ACTION_INCREASE    = "Augmenter";
    public static final String TRX_SPLT_ACTION_DECREASE    = "Réduire";
    public static final String TRX_SPLT_ACTION_INTEREST    = "Intérêts";
    public static final String TRX_SPLT_ACTION_PAYMENT     = "Payer";
    public static final String TRX_SPLT_ACTION_REBATE      = "Remise";
    // Attention: Le prochain est mal traduit -- correctement: "Salaire".
    // Pourtant, c'est le terme exact dans le fichier PO, et c'est 
    // la raison pour laquelle nous le gardons.
    public static final String TRX_SPLT_ACTION_PAYCHECK    = "Chèque";
    public static final String TRX_SPLT_ACTION_CREDIT      = "Crédit";
    public static final String TRX_SPLT_ACTION_ATM_DEPOSIT = "Dépôt au guichet automatique";
    public static final String TRX_SPLT_ACTION_ATM_DRAW    = "Retrait au guichet automatique";
    public static final String TRX_SPLT_ACTION_ONLINE      = "En ligne";
    public static final String TRX_SPLT_ACTION_INVOICE     = "Facture";
    public static final String TRX_SPLT_ACTION_BILL        = "Facture fournisseur";
    public static final String TRX_SPLT_ACTION_VOUCHER     = "Bon de dépenses";
    public static final String TRX_SPLT_ACTION_BUY         = "Acheter";
    public static final String TRX_SPLT_ACTION_SELL        = "Vendre";
    public static final String TRX_SPLT_ACTION_EQUITY      = "Capitaux propres";
    public static final String TRX_SPLT_ACTION_PRICE       = "Cours";
    public static final String TRX_SPLT_ACTION_FEE         = "Honoraires";
    public static final String TRX_SPLT_ACTION_DIVIDEND    = "Dividende";
    public static final String TRX_SPLT_ACTION_LTCG        = "PVLT";
    public static final String TRX_SPLT_ACTION_STCG        = "PVCT";
    public static final String TRX_SPLT_ACTION_INCOME      = "Revenus";
    public static final String TRX_SPLT_ACTION_DIST        = "Distrib";
    public static final String TRX_SPLT_ACTION_SPLIT       = "Répartition";

    // ---

    public static final String INVC_READ_ONLY_SLOT_TEXT = "Généré depuis une facture. " + 
                                                          "Essayez de suspendre la facture.";

    // ---

    public static final String INVC_ENTR_ACTION_JOB      = "Projet";
    public static final String INVC_ENTR_ACTION_MATERIAL = "Matières premières";
    public static final String INVC_ENTR_ACTION_HOURS    = "Heures";

}
