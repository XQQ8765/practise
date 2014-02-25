package com.xiaoqq.hadoop.practise1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: rxiao
 * Date: 2/25/14
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinTemperatureReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>{
    public void reduce(Text key, Iterator<IntWritable> values,
                       OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        System.out.println("-----------------MinTemperatureReducer: key:"+key+", values="+values);
        int minValue = Integer.MIN_VALUE;
        while (values.hasNext()) {
            minValue = Math.min(minValue, values.next().get());
        }
        System.out.println("-----------------MinTemperatureReducer: key:"+key+", minValue="+minValue);
        output.collect(key, new IntWritable(minValue));
    }
}
