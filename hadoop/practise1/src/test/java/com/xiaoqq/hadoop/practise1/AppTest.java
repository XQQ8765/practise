package com.xiaoqq.hadoop.practise1;

import com.google.common.base.Objects;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public void testToString() {
       String[] args = {"aa", "bb"};
       String str = Objects.toStringHelper(args).toString();
       assertEquals("String;{}", str);
       str = Arrays.toString(args);
       assertEquals("[aa, bb]", str);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
