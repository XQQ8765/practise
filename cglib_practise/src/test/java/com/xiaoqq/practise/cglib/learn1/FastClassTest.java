package com.xiaoqq.practise.cglib.learn1;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import net.sf.cglib.util.ParallelSorter;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * See http://mydailyjava.blogspot.no/2013/11/cglib-missing-manual.html.
 */
public class FastClassTest {
    @Test
    public void testFastClass() throws Exception {
        FastClass fastClass = FastClass.create(SampleBean.class);
        FastMethod fastMethod = fastClass.getMethod(SampleBean.class.getMethod("getValue"));
        SampleBean myBean = new SampleBean();
        myBean.setValue("Hello cglib!");
        assertEquals("Hello cglib!", ((String) fastMethod.invoke(myBean, new Object[0])));
    }
}
