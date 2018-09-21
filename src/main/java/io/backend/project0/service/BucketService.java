package io.backend.project0.service;

import io.backend.project0.StorageDir;
import io.backend.project0.entity.Bucket;
import io.backend.project0.entity.ObjectPart;
import io.backend.project0.entity.ObjectStored;
import io.backend.project0.repository.BucketRepository;
import io.backend.project0.repository.ObjectPartRepository;
import io.backend.project0.repository.ObjectStoredRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class BucketService {

    @Autowired
    private BucketRepository bucketRepository;

    @Autowired
    private ObjectStoredRepository objectStoredRepository;

    @Autowired
    private ObjectPartRepository objectPartRepository;

    //Get All Buckets
    public List<Bucket> getAllBucket(){
        return bucketRepository.findAll();
    }

    //Create
    public HashMap<String,Object> create(String bucketName){
        HashMap<String,Object> details = new HashMap<>();
        long createdTime = System.currentTimeMillis();
        Bucket bucket = new Bucket(createdTime, createdTime,bucketName);
        bucketRepository.save(bucket);
        new File(StorageDir.storage + '/'+bucketName).mkdirs();

        details.put("created",bucket.getCreated());
        details.put("modified",bucket.getModified());
        details.put("name",bucket.getBucketName());

        return details;
    }

    public void delete(String bucketName){
        Bucket b = bucketRepository.findBucketByBucketName(bucketName);
        bucketRepository.delete(b);
        objectStoredRepository.deleteAllByBucketName(bucketName);
        objectPartRepository.deleteAllByBucketName(bucketName);

        //Waste time
//        new File(StorageDir.storage + '/'+bucketName).delete();

    }

//    public Bucket getBucket(String bucketName){
//        return bucketRepository.findBucketByBucketName(bucketName);
//    }

    public boolean isBucketNameExist(String bucketName){
        return bucketRepository.existsById(bucketName);
    }

    public boolean validateBucketName(String bucketName){
        Pattern pattern = Pattern.compile("^[\\w-_]+$");
        return pattern.matcher(bucketName).find();
    }

    public HashMap<String, Object> getBucketDetail(String bucketName){
        Bucket bucket = bucketRepository.findBucketByBucketName(bucketName);
        HashMap<String,Object> details = new HashMap<>();
        if (bucket!=null){
            details.put("created",bucket.getCreated());
            details.put("modified",bucket.getModified());
            details.put("name",bucket.getBucketName());
            List<ObjectStored> objectStoreds = objectStoredRepository.findAllByBucketName(bucketName);
            details.put("objects",objectStoreds);
        }

        return details;
    }
}
