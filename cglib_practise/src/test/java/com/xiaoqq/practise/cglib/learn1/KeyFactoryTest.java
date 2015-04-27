package com.xiaoqq.practise.cglib.learn1;

import net.sf.cglib.beans.*;
import net.sf.cglib.core.KeyFactory;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * See http://mydailyjava.blogspot.no/2013/11/cglib-missing-manual.html.
 */
public class KeyFactoryTest {
    @Test
    public void testKeyFactory() throws Exception {
        SampleKey keyFactory = (SampleKey) KeyFactory.create(SampleKey.class);
        Object key = keyFactory.newInstance("foo", 42);
        Map<Object, String> map = new HashMap<Object, String>();
        map.put(key, "Hello cglib!");
        assertEquals("Hello cglib!", map.get(keyFactory.newInstance("foo", 42)));
    }
}
