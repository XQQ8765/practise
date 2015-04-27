package com.xiaoqq.practise.cglib.learn1;

import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.util.StringSwitcher;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * See http://mydailyjava.blogspot.no/2013/11/cglib-missing-manual.html.
 */
public class StringSwitcherTest {
    @Test
    public void testStringSwitcher() throws Exception {
        String[] strings = new String[]{"one", "two"};
        int[] values = new int[]{10, 20};
        StringSwitcher stringSwitcher = StringSwitcher.create(strings, values, true);
        assertEquals(10, stringSwitcher.intValue("one"));
        assertEquals(20, stringSwitcher.intValue("two"));
        assertEquals(-1, stringSwitcher.intValue("three"));
    }

}
