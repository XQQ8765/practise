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
public class SampleBeanTest {
    @Test(expected = IllegalStateException.class)
    public void testImmutableBean () {
        SampleBean bean = new SampleBean();
        bean.setValue("Hello world!");
        SampleBean immutableBean = (SampleBean) ImmutableBean.create(bean);
        assertEquals("Hello world!", immutableBean.getValue());
        bean.setValue("Hello world, again!");
        assertEquals("Hello world, again!", immutableBean.getValue());
        immutableBean.setValue("Hello cglib!"); // Causes exception.
    }

    @Test
    public void testBeanGenerator() throws Exception {
        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.addProperty("value", String.class);
        Object myBean = beanGenerator.create();

        Method setter = myBean.getClass().getMethod("setValue", String.class);
        setter.invoke(myBean, "Hello cglib!");
        Method getter = myBean.getClass().getMethod("getValue");
        assertEquals("Hello cglib!", getter.invoke(myBean));
    }

    @Test
    public void testBeanCopier() throws Exception {
        SampleBean bean = new SampleBean();
        bean.setValue("Hello cglib!");
        OtherSampleBean otherBean = new OtherSampleBean();
        BeanCopier copier = BeanCopier.create(SampleBean.class, OtherSampleBean.class, false);
        copier.copy(bean, otherBean, null);
        assertEquals("Hello cglib!", otherBean.getValue());
    }

    @Test
    public void testBulkBean() throws Exception {
        BulkBean bulkBean = BulkBean.create(SampleBean.class,
                new String[]{"getValue"},
                new String[]{"setValue"},
                new Class[]{String.class});
        SampleBean bean = new SampleBean();
        bean.setValue("Hello world!");
        assertEquals(1, bulkBean.getPropertyValues(bean).length);
        assertEquals("Hello world!", bulkBean.getPropertyValues(bean)[0]);
        bulkBean.setPropertyValues(bean, new Object[] {"Hello cglib!"});
        assertEquals("Hello cglib!", bean.getValue());
    }

    @Test
    public void testBeanMap() throws Exception {
        SampleBean bean = new SampleBean();
        BeanMap map = BeanMap.create(bean);
        bean.setValue("Hello cglib!");
        assertEquals("Hello cglib!", map.get("value"));
    }
}
