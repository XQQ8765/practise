package com.xiaoqq.practise.cglib.learn1;

import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.proxy.Mixin;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * See http://mydailyjava.blogspot.no/2013/11/cglib-missing-manual.html.
 */
public class MixinInterfaceTest {

    @Test
    public void testMixin() throws Exception {
        Mixin mixin = Mixin.create(new Class[]{Interface1.class, Interface2.class,
                MixinInterface.class}, new Object[]{new Class1(), new Class2()});
        MixinInterface mixinDelegate = (MixinInterface) mixin;
        assertEquals("first", mixinDelegate.first());
        assertEquals("second", mixinDelegate.second());
    }
}
