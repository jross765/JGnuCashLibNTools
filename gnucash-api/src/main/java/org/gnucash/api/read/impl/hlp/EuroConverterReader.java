package org.gnucash.api.read.impl.hlp;

import java.io.IOException;
import java.io.Reader;

/**
 * replaces &#164; by the euro-sign .
 */
public class EuroConverterReader extends Reader {

    /**
     * This is "&#164;".length .
     */
    private static final int REPLACESTRINGLENGTH = 5; // ::MAGIC
    
    // ---------------------------------------------------------------

    /**
     * Where to read from.
     */
    private Reader input;

    // ---------------------------------------------------------------

    /**
     * @param pInput Where to read from.
     */
    public EuroConverterReader(final Reader pInput) {
        super();
        input = pInput;
    }

    // ---------------------------------------------------------------

    /**
     * @return Where to read from.
     */
    public Reader getInput() {
        return input;
    }

    /**
     * @param newInput Where to read from.
     */
    public void setInput(Reader newInput) {
        if (newInput == null) {
    	throw new IllegalArgumentException("null not allowed for field this.input");
        }

        input = newInput;
    }

    /**
     * @see java.io.Reader#read(char[], int, int)
     */
    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {

        int reat = input.read(cbuf, off, len);

        // this does not work if the euro-sign is wrapped around the
        // edge of 2 read-call buffers

        int state = 0;

        for (int i = off; i < off + reat; i++) {

    	switch (state) {

    	case 0: {
    	    if (cbuf[i] == '&') {
    		state++;
    	    }
    	    break;
    	}

    	case 1: {
    	    if (cbuf[i] == '#') {
    		state++;
    	    } else {
    		state = 0;
    	    }
    	    break;
    	}

    	case 2: {
    	    if (cbuf[i] == '1') {
    		state++;
    	    } else {
    		state = 0;
    	    }
    	    break;
    	}

    	case REPLACESTRINGLENGTH - 2: {
    	    if (cbuf[i] == '6') {
    		state++;
    	    } else {
    		state = 0;
    	    }
    	    break;
    	}

    	case REPLACESTRINGLENGTH - 1: {
    	    if (cbuf[i] == '4') {
    		state++;
    	    } else {
    		state = 0;
    	    }
    	    break;
    	}
    	case REPLACESTRINGLENGTH: {
    	    if (cbuf[i] == ';') {
    		// found it!!!
    		cbuf[i - REPLACESTRINGLENGTH] = '�';
    		if (i != reat - 1) {
    		    System.arraycopy(cbuf, (i + 1), cbuf, (i - (REPLACESTRINGLENGTH - 1)), (reat - i - 1));
    		}
    		int reat2 = input.read(cbuf, reat - REPLACESTRINGLENGTH, REPLACESTRINGLENGTH);
    		if (reat2 != REPLACESTRINGLENGTH) {
    		    reat -= (REPLACESTRINGLENGTH - reat2);
    		}
    		i -= (REPLACESTRINGLENGTH - 1);
    		state = 0;
    	    } else {
    		state = 0;
    	    }
    	    break;
    	}

    	default:
    	}

        }
        return reat;
    }

    /**
     * @see java.io.Reader#close()
     */
    @Override
    public void close() throws IOException {
        input.close();
    }
}

