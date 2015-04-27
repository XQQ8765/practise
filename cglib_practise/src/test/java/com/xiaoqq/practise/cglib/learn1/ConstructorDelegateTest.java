package com.xiaoqq.practise.cglib.learn1;

import net.sf.cglib.reflect.ConstructorDelegate;
import net.sf.cglib.util.StringSwitcher;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * See http://mydailyjava.blogspot.no/2013/11/cglib-missing-manual.html.
 */
public class ConstructorDelegateTest {
    interface SampleBeanConstructorDelegate {
        Object newInstance();
    }

    @Test
    public void testConstructorDelegate() throws Exception {
        SampleBeanConstructorDelegate constructorDelegate = (SampleBeanConstructorDelegate) ConstructorDelegate.create(
                SampleBean.class, SampleBeanConstructorDelegate.class);
        SampleBean bean = (SampleBean) constructorDelegate.newInstance();
        assertTrue(SampleBean.class.isAssignableFrom(bean.getClass()));
    }

}
