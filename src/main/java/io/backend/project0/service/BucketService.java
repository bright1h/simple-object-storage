package io.backend.project0.service;

import io.backend.project0.StorageDir;
import io.backend.project0.entity.Bucket;
import io.backend.project0.repository.BucketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
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
        if(!bucketRepository.existsById(bucketName)){
            long createdTime = System.currentTimeMillis();
            Bucket bucket = new Bucket(createdTime, createdTime,bucketName);
            bucketRepository.save(bucket);
            new File(StorageDir.storage + '/'+bucketName).mkdirs();
            return bucket;
        }

        return null;
    }

    public void delete(String bucketName){
        Bucket b = bucketRepository.findBucketByBucketName(bucketName);
        bucketRepository.delete(b);
        new File(StorageDir.storage + '/'+bucketName).delete();
    }

    public Bucket getBucket(String bucketName){
        return bucketRepository.findBucketByBucketName(bucketName);
    }

    public boolean isBucketNameExist(String bucketName){
        return bucketRepository.existsById(bucketName);
    }

    public boolean validateBucketName(String bucketName){
        Pattern pattern = Pattern.compile("^[\\w-_]+$");
        return pattern.matcher(bucketName).find();
    }
}
