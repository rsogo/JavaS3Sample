package org.example.basicapp;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.AmazonServiceException;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

    }

    public void go(String bucketName, String filePath) {

        try {
            final AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.AP_NORTHEAST_1)
                .build();
            List<Bucket> buckets = s3.listBuckets();
            System.out.println("Your {S3} buckets are:");
            for (Bucket b : buckets) {
                System.out.println("* " + b.getName());
            }

            System.out.format("Uploading %s to S3 bucket %s...\n", filePath, bucketName);
            try {
                String keyName = Paths.get(filePath).getFileName().toString();
                s3.putObject(bucketName, keyName, new File(filePath));
            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
                System.exit(1);
            }

            System.out.format("Objects in S3 bucket %s:\n", bucketName);
            ListObjectsV2Result result = s3.listObjectsV2(bucketName);
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            for (S3ObjectSummary os : objects) {
                System.out.println("* " + os.getKey());
            }
                
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
