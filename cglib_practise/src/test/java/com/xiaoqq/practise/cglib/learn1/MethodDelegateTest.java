package com.xiaoqq.practise.cglib.learn1;

import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.reflect.MethodDelegate;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * See http://mydailyjava.blogspot.no/2013/11/cglib-missing-manual.html.
 */
public class MethodDelegateTest {
    public interface BeanDelegate {
        String getValueFromDelegate();
    }

    @Test
    public void testMethodDelegate() throws Exception {
        SampleBean bean = new SampleBean();
        bean.setValue("Hello cglib!");
        BeanDelegate delegate = (BeanDelegate) MethodDelegate.create(
                bean, "getValue", BeanDelegate.class);
        assertEquals("Hello cglib!", delegate.getValueFromDelegate());
    }
}
