package com.xiaoqq.hadoop.practise1;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.FileInputFormat;

import java.io.IOException;
import java.util.Arrays;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        if (args == null || args.length == 0) {
            System.out.println("--------App: args[] is empty");
            String[] args2 = {"/input/input.txt", "/output"};
            args = args2;
        }

        System.out.println("--------App: Arrays.toString(args):"+ Arrays.toString(args));

        if (args.length != 2) {
            System.err.println("Usage: MinTemperature <input path> <output path>");
            System.exit(1);
        }

        JobConf conf = new JobConf(App.class);
        conf.setJobName("Min temperature");
        FileInputFormat.addInputPath(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        conf.setMapperClass(MinTemperatureMapper.class);
        conf.setReducerClass(MinTemperatureReducer.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        JobClient.runJob(conf);
    }
}
