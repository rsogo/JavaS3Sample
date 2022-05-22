package org.example.basicapp;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.services.s3.model.S3Object;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
            final S3Client s3 = S3Client.builder()
                .region(Region.AP_NORTHEAST_1)
                .build();

            // List buckets
            ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
            ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
            listBucketsResponse.buckets().stream().forEach(x -> System.out.println(x.name()));

            System.out.format("Uploading %s to S3 bucket %s...\n", filePath, bucketName);
            try {
                String keyName = Paths.get(filePath).getFileName().toString();
                PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

                PutObjectResponse response = s3.putObject(
                    putOb,
                    RequestBody.fromBytes(getObjectFile(filePath)));

                System.out.println(response.eTag());
                
            } catch (S3Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.format("Objects in S3 bucket %s:\n", bucketName);

            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();

           for (S3Object myValue : objects) {
               System.out.print("\n The name of the key is " + myValue.key());
               System.out.print("\n The object is " + calKb(myValue.size()) + " KBs");
               System.out.print("\n The owner is " + myValue.owner());
           }
                
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    private static byte[] getObjectFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }

    private static long calKb(Long val) {
        return val/1024;
    }
}
