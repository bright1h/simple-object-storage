package io.backend.project0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ObjectService {

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private BucketRepository bucketRepository;

    //Create
    public Object createTicket(String name,String bucketName){
        if(bucketRepository.existsById(bucketName)){
            //check Alphanumeric, - ,_
            if(!objectRepository.existsByNameAndBucketName(name,bucketName)) {
                Pattern pattern = Pattern.compile("^[\\w-_.]+$");
                boolean allow = pattern.matcher(name).find();
                if (allow) {
                    long createdTime = System.currentTimeMillis();
                    Object object = new Object(name,createdTime,createdTime,bucketName);
                    return objectRepository.save(object);
                }
            }

        }

        return null;
    }

    public Object uploadPart(String name, String bucketName,int partNumber){
        return null;
    }

    public Object complete(String name, String bucketName){
        return null;
    }

    public Object deletePart(String name, String bucketName,int partNumber){
        return null;
    }

    public Object deleteObject(String name, String bucketName){
        return null;
    }

    public Object getObject(){
        //get, download do in controller
        return null;
    }

    public Object addAndupdateMetadata(String name, String bucketName, String key){
        return null;
    }

    public Object deleteMetadata(String name, String bucketName,String key){
        return null;
    }

    public Object getMetadata(String name, String bucketName,String key){
        //get, return string of meta data
        return null;
    }

    public Object getAllMetadata(String name, String bucketName){
        return null;
    }


}
