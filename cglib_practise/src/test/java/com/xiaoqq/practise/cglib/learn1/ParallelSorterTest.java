package com.xiaoqq.practise.cglib.learn1;

import net.sf.cglib.util.ParallelSorter;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * See http://mydailyjava.blogspot.no/2013/11/cglib-missing-manual.html.
 */
public class ParallelSorterTest {
    @Test
    public void testParallelSorter() throws Exception {
        Integer[][] value = {
                {4, 3, 9, 0},
                {2, 1, 6, 0}
        };
        ParallelSorter.create(value).mergeSort(0);
        for(Integer[] row : value) {
            int former = -1;
            for(int val : row) {
                assertTrue(former < val);
                former = val;
            }
        }
    }
}
