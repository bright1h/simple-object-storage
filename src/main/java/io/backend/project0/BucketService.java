package io.backend.project0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class BucketService {

    @Autowired
    private BucketRepository bucketRepository;

    //Get All Buckets
    public List<Bucket> getAllBucket(){
        return bucketRepository.findAll();
    }

    //Create
    public Bucket create(String bucketName){
        //check Alphanumeric, - ,_
        Pattern pattern = Pattern.compile("^[\\w-_]+$");
        boolean allow = pattern.matcher(bucketName).find();
        if(allow){
            if(!bucketRepository.existsById(bucketName)){
                long createdTime = System.currentTimeMillis();
                Bucket bucket = new Bucket(createdTime, createdTime,bucketName);
                bucketRepository.save(bucket);
                return bucket;
            }

        }

        return null;
    }

    public Bucket delete(String bucketName){
        Bucket b = bucketRepository.findBucketByName(bucketName);
        bucketRepository.delete(b);
        return b;
    }

    public Bucket getBucket(String bucketName){
        return bucketRepository.findBucketByName(bucketName);
    }
}
