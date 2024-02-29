package org.gnucash.base.numbers;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.gnucash.base.ConstTest;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestFixedPointNumber {

    // -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
    }

    @SuppressWarnings("exports")
	public static junit.framework.Test suite() {
    	return new JUnit4TestAdapter(TestFixedPointNumber.class);
    }

    @Before
    public void initialize() throws Exception {
		// ::EMPTY
    }

    // -----------------------------------------------------------------

    @Test
    public void test01_1() throws Exception {
    	FixedPointNumber num = new FixedPointNumber();
    	assertEquals(0.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);

    	num = new FixedPointNumber("0");
    	assertEquals(0.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(0, num.intValue());

    	num = new FixedPointNumber("1");
    	assertEquals(1.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(1, num.intValue());

    	num = new FixedPointNumber("1/1");
    	assertEquals(1.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(1, num.intValue());

    	num = new FixedPointNumber("100/100");
    	assertEquals(1.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(1, num.intValue());

    	num = new FixedPointNumber("100/200");
    	assertEquals(0.5, num.doubleValue(), ConstTest.DIFF_TOLERANCE);

    	num = new FixedPointNumber("200/100");
    	assertEquals(2.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(2, num.intValue());

    	num = new FixedPointNumber("1287472/1000000");
    	assertEquals(1.287472, num.doubleValue(), 0.0000005);
    	
    	num = new FixedPointNumber("355/113");
    	assertEquals(Math.PI, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	
    	// ---

    	num = new FixedPointNumber("-1"); // valid
    	assertEquals(-1.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(-1, num.intValue());

    	num = new FixedPointNumber("-100/100"); // valid
    	assertEquals(-1.0, num.doubleValue(), 0.0000005);
    	assertEquals(-1, num.intValue());

    	num = new FixedPointNumber("100/-100"); // valid
    	assertEquals(-1.0, num.doubleValue(), 0.0000005);
    	assertEquals(-1, num.intValue());

    	num = new FixedPointNumber("-100/-100"); // valid
    	assertEquals(1.0, num.doubleValue(), 0.0000005);
    	assertEquals(1, num.intValue());
    }

    @Test
    public void test01_2() throws Exception {
    	FixedPointNumber num = new FixedPointNumber(0);
    	assertEquals(0.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(0, num.intValue());

    	num = new FixedPointNumber(1);
    	assertEquals(1.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(1, num.intValue());

    	num = new FixedPointNumber(18);
    	assertEquals(18.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(18, num.intValue());

    	num = new FixedPointNumber(-18);
    	assertEquals(-18.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(-18, num.intValue());

    	num = new FixedPointNumber(48238112);
    	assertEquals(48238112.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(48238112, num.intValue());
    }

    @Test
    public void test01_3() throws Exception {
    	FixedPointNumber num = new FixedPointNumber(BigDecimal.valueOf(0));
    	assertEquals(0.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(0, num.intValue());

    	num = new FixedPointNumber(BigDecimal.valueOf(1));
    	assertEquals(1.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(1, num.intValue());

    	num = new FixedPointNumber(BigDecimal.valueOf(18));
    	assertEquals(18.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(18, num.intValue());

    	num = new FixedPointNumber(BigDecimal.valueOf(-18));
    	assertEquals(-18.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(-18, num.intValue());

    	num = new FixedPointNumber(BigDecimal.valueOf(48238112));
    	assertEquals(48238112.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(48238112, num.intValue());
    }

    @Test
    public void test02_1() throws Exception {
    	FixedPointNumber num = new FixedPointNumber();
    	assertEquals(0.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(0, num.intValue());

    	num.add("1");
    	assertEquals(1.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(1, num.intValue());

    	num.add(new FixedPointNumber("1"));
    	assertEquals(2.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(2, num.intValue());

    	num.add(-12);
    	assertEquals(-10.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(-10, num.intValue());

    	num.add(BigDecimal.valueOf(23.127));
    	assertEquals(13.127, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	
    	// ---

    	num.subtract("13");
    	assertEquals(0.127, num.doubleValue(), ConstTest.DIFF_TOLERANCE);

    	num.subtract(new FixedPointNumber("127/1000"));
    	assertEquals(0.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(0, num.intValue());

    	num.subtract(-12);
    	assertEquals(12.0, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(12, num.intValue());

    	num.subtract(BigDecimal.valueOf(12.1));
    	assertEquals(-0.1, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    }


    @Test
    public void test02_2() throws Exception {
    	FixedPointNumber num = new FixedPointNumber(12.1);
    	assertEquals(12.1, num.doubleValue(), ConstTest.DIFF_TOLERANCE);

    	num.multiply(2);
    	assertEquals(24.2, num.doubleValue(), ConstTest.DIFF_TOLERANCE);

    	num.multiply(BigDecimal.valueOf(3.2));
    	assertEquals(77.44, num.doubleValue(), ConstTest.DIFF_TOLERANCE);

    	num.multiply(new FixedPointNumber("3/2"));
    	assertEquals(116.16, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	
    	num.multiply(new FixedPointNumber("2/3"));
    	assertEquals(77.44, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	
    	// ---
    	// CAUTION: Things work a little differently with divide()
    	// THIS IS NOT CONSISTENT, IT HAS TO CHANGE!

    	BigDecimal newNum = num.divide(BigDecimal.valueOf(4));
    	assertEquals(77.44, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(19.36, newNum.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	
    	newNum = num.divide(BigDecimal.TEN);
    	assertEquals(77.44, num.doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(7.744, newNum.doubleValue(), ConstTest.DIFF_TOLERANCE);
    }
}
