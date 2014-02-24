package com.xiaoqq.hadoop.practise1;

import com.google.common.base.Strings;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import java.io.IOException;
import java.util.Iterator;

import com.google.common.base.Splitter;

/**
 * Created by IntelliJ IDEA.
 * User: rxiao
 * Date: 2/24/14
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinTemperatureMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>{

    public void map(LongWritable longWritable, Text value,
                    OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String line = value.toString();
        if (Strings.isNullOrEmpty(line)) {
            return ;
        }
        Iterable<String> iterable = Splitter.on('|').split(line);
        Iterator<String> iterator = iterable.iterator();
        iterator.next();
        String cityName = iterator.next();
        iterator.next();
        int temperature =  Integer.parseInt(iterator.next());

        output.collect(new Text(cityName), new IntWritable(temperature));


    }
}
