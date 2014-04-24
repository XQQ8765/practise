package com.xiaoqq.hadoop.practise2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.InputStream;
import java.net.URI;

/**
 * Implement Cat via Hadoop FileSystem
 */
public class FileSystemDoubleCat {
    public static void main( String[] args ) throws Exception
    {
        String uri = args[0];
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), conf);
        FSDataInputStream in = null;
        try {
            in = fs.open(new Path(uri));
            System.out.println("--------------------The fist copy:");
            IOUtils.copyBytes(in, System.out, 4096, false);

            in.seek(0);//Go back to the start of the file

            System.out.println("--------------------The second copy:");
            IOUtils.copyBytes(in, System.out, 4096, false);
        }  finally {
            IOUtils.closeStream(in);
        }
    }
}
