package com.xiaoqq.practise.threadmonitor;

import org.objectweb.asm.util.CheckClassAdapter;

/**
 * Created by rxiao on 6/12/15.
 */
public class CheckClassAdapterTest {
    public static void main(String[] args) throws Exception {
        String classPath = "d:\\workspace\\tmp\\generatedclasses\\com\\xiaoqq\\practise\\threadsamplecode\\wait\\WaitNotifyTest_gen.class";
        CheckClassAdapter.main(new String[]{classPath});
    }
}
