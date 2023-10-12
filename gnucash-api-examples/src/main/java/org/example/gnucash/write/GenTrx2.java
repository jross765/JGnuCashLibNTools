package org.example.gnucash.write;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.GnucashAccount;
import org.gnucash.read.GnucashTransaction;
import org.gnucash.read.GnucashTransactionSplit;
import org.gnucash.write.GnucashWritableTransaction;
import org.gnucash.write.GnucashWritableTransactionSplit;
import org.gnucash.write.impl.GnucashWritableFileImpl;

/**
 * Created by Deniss Larka
 */
public class GenTrx2 {
    // BEGIN Example data -- adapt to your needs
    private static String gcshInFileName  = "example_in.gnucash";
    private static String gcshOutFileName = "example_out.gnucash";
    private static String accountName     = "Root Account::Erträge::Honorar";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) throws IOException {
	try {
	    GenTrx2 tool = new GenTrx2();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    private void kernel() throws Exception {
	GnucashWritableFileImpl gnucashFile = new GnucashWritableFileImpl(new File(gcshInFileName));
	Collection<GnucashAccount> accounts = gnucashFile.getAccounts();
	for (GnucashAccount account : accounts) {
	    System.out.println(account.getQualifiedName());
	}

	GnucashWritableTransaction writableTransaction = gnucashFile.createWritableTransaction();
	writableTransaction.setDescription("check");
	writableTransaction.setCurrencyID("EUR");
	writableTransaction.setDateEntered(LocalDateTime.now());

	GnucashWritableTransactionSplit writingSplit = writableTransaction
		.createWritingSplit(gnucashFile.getAccountByName(accountName));
	writingSplit.setValue(new FixedPointNumber(100));
	writingSplit.setDescription("Generated by GenTrx2 " + LocalDateTime.now().toString());

	Collection<? extends GnucashTransaction> transactions = gnucashFile.getTransactions();
	for (GnucashTransaction transaction : transactions) {
	    System.out.println(transaction.getDatePosted());
	    List<GnucashTransactionSplit> splits = transaction.getSplits();
	    for (GnucashTransactionSplit split : splits) {
		System.out.println("\t" + split.getQuantity());
	    }
	}

	// Caution: output file will always be in uncompressed XML format,
	// regardless of whether the input file was compressed or not.
	gnucashFile.writeFile(new File(gcshOutFileName));
    }
}
