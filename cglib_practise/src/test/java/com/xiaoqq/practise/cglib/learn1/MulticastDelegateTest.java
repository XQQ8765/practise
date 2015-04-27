package com.xiaoqq.practise.cglib.learn1;

import net.sf.cglib.reflect.MethodDelegate;
import net.sf.cglib.reflect.MulticastDelegate;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * See http://mydailyjava.blogspot.no/2013/11/cglib-missing-manual.html.
 */
public class MulticastDelegateTest {
    public interface DelegatationProvider {
        void setValue(String value);
    }

    public class SimpleMulticastBean implements DelegatationProvider {
        private String value;
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testMulticastDelegate() throws Exception {
        MulticastDelegate multicastDelegate = MulticastDelegate.create(
                DelegatationProvider.class);
        SimpleMulticastBean first = new SimpleMulticastBean();
        SimpleMulticastBean second = new SimpleMulticastBean();
        multicastDelegate = multicastDelegate.add(first);
        multicastDelegate = multicastDelegate.add(second);

        DelegatationProvider provider = (DelegatationProvider)multicastDelegate;
        provider.setValue("Hello world!");

        assertEquals("Hello world!", first.getValue());
        assertEquals("Hello world!", second.getValue());
    }
}
