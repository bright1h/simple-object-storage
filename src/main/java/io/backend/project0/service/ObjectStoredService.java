package io.backend.project0.service;

import io.backend.project0.StorageDir;
import io.backend.project0.entity.ObjectPart;
import io.backend.project0.entity.ObjectStored;
import io.backend.project0.repository.BucketRepository;
import io.backend.project0.repository.ObjectPartRepository;
import io.backend.project0.repository.ObjectStoredRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ObjectStoredService {

    @Autowired
    private ObjectStoredRepository objectStoredRepository;

    @Autowired
    private ObjectPartRepository objectPartRepository;

    @Autowired
    private BucketRepository bucketRepository;

    public List<ObjectStored> getAllObject(){
        return objectStoredRepository.findAll();
    }

    public ObjectStored createTicket(String name, String bucketName) {
        if (isObjectExist(name, bucketName)) return null;
        long createdTime = System.currentTimeMillis();
        ObjectStored objectStored = new ObjectStored(name,createdTime,createdTime,bucketName);
        //Recheck spamming
        if (isObjectExist(name, bucketName)) return null;
        objectStoredRepository.save(objectStored);

        return objectStored;
    }

    public boolean isObjectExist(String name, String bucketName){
        return objectStoredRepository.existsByObjectNameAndBucketName(name,bucketName);
    }

    public ObjectStored complete(String name, String bucketName){
        ObjectStored objectStored = objectStoredRepository.findByObjectNameAndBucketName(name,bucketName);

        if(objectStored!=null && !objectStored.isComplete()) {
            List<ObjectPart> objectParts = objectPartRepository.findAllByBucketNameAndObjectName(bucketName, name);
            objectParts.sort(Comparator.comparingInt(ObjectPart::getPartNumber));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            long size =0;

            try {
                for (ObjectPart p : objectParts) {
                    size+=p.getPartSize();
                    InputStream inputstream = new FileInputStream(p.getPath());
                    outputStream.write(IOUtils.toByteArray(inputstream));
                }

                String newMd5 = DigestUtils.md5Hex(outputStream.toByteArray());
                objectStored.setMd5(newMd5);
                long curTime = System.currentTimeMillis();
                objectStored.seteTag(newMd5+'-'+objectParts.size());
                objectStored.setSize(size);
                objectStored.setModified(curTime);
                objectStored.setComplete(true);
                objectStored.setPath(StorageDir.storage+'/'+bucketName+'/'+name);
                objectStoredRepository.save(objectStored);
                return objectStored;

            } catch (IOException e) {
                e.printStackTrace();
            } 
        }
        return null;
    }


    public void deleteObject(String objectName, String bucketName){
        ObjectStored objectStored = objectStoredRepository.findByObjectNameAndBucketName(objectName,bucketName);
        if (objectStored!=null) {
            objectStoredRepository.delete(objectStored);
            objectPartRepository.deleteAllByBucketNameAndObjectName(bucketName, objectName);
        }
    }

    public ObjectStored getObject(String objectName, String bucketName){
        return objectStoredRepository.findByObjectNameAndBucketName(objectName,bucketName);
    }

    public void addAndUpdateMetadata(String objectName, String bucketName, String key, String value){
        ObjectStored objectStored = objectStoredRepository.findByObjectNameAndBucketName(objectName,bucketName);
        if (objectStored!=null) {
            HashMap<String, String> metadata = objectStored.getMetadata();
            String lowerCaseKey = key.toLowerCase();
            metadata.put(lowerCaseKey, value);
            objectStored.setMetadata(metadata);
            objectStoredRepository.save(objectStored);
        }
    }

    public void deleteMetadata(String objectName, String bucketName,String key){
        ObjectStored objectStored = objectStoredRepository.findByObjectNameAndBucketName(objectName,bucketName);
        if (objectStored!=null) {
            HashMap<String, String> metadata = objectStored.getMetadata();
            metadata.remove(key);
            objectStored.setMetadata(metadata);
            objectStoredRepository.save(objectStored);
        }
    }

    public String getMetadata(String objectName, String bucketName,String key){
        ObjectStored objectStored = objectStoredRepository.findByObjectNameAndBucketName(objectName,bucketName);
        if (objectStored !=null) return objectStored.getMetadata().get(key);
        return "";
    }

    public HashMap<String,String> getAllMetadata(String objectName, String bucketName){
        HashMap<String, String> metadata = new HashMap<>();
        ObjectStored objectStored = objectStoredRepository.findByObjectNameAndBucketName(objectName,bucketName);
        if (objectStored!=null) metadata = objectStored.getMetadata();
        return metadata;
    }

    public boolean validateObjectName(String objectName){
        Pattern pattern = Pattern.compile("^(?![.])(?!.*[-_.]$).*");
        return pattern.matcher(objectName).find();
    }

}
