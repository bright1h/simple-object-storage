package io.backend.project0.service;

import io.backend.project0.StorageDir;
import io.backend.project0.entity.Bucket;
import io.backend.project0.entity.ObjectStored;
import io.backend.project0.entity.ObjectPart;
import io.backend.project0.repository.BucketRepository;
import io.backend.project0.repository.ObjectStoredRepository;
import io.backend.project0.repository.ObjectPartRepository;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
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
        objectStoredRepository.save(objectStored);

        return objectStored;
    }

    public boolean isObjectExist(String name, String bucketName){
        return objectStoredRepository.existsByObjectNameAndBucketName(name,bucketName);
    }

    public ObjectStored complete(String name, String bucketName){
        Bucket bucket = bucketRepository.findBucketByBucketName(bucketName);
        ObjectStored objectStored = objectStoredRepository.findObjectByObjectNameAndBucketName(name,bucketName);

        if(!objectStored.isComplete()) {
            List<ObjectPart> objectParts = objectPartRepository.findAllByBucketNameAndObjectName(bucketName, name);
            objectParts.sort(Comparator.comparingInt(ObjectPart::getPartNumber));
            HashMap<String,String> eTag = objectStored.geteTag();
            String allPartsMd5 ="";
            try {
                File newFile = new File(StorageDir.storage + '/' + bucketName + "/" + name);
                FileOutputStream fos = new FileOutputStream(newFile);
                for (ObjectPart p : objectParts) {
                    System.out.println(p.toString());
                    File file = new File(p.getPath() + p.getPartName());
                    InputStream inputStream = new FileInputStream(file);
                    byte[] bytes = IOUtils.toByteArray(inputStream);
                    fos.write(bytes);
                    System.out.println(p.getPartMd5());
                    eTag.put("part"+p.getPartNumber(),p.getPartMd5());
                    allPartsMd5+=p.getPartMd5();
                }

                String newMd5 = DigestUtils.md5Hex(Hex.decodeHex(allPartsMd5));
                objectStored.setMd5(newMd5);
                long curTime = System.currentTimeMillis();
                System.out.println(eTag);
                objectStored.seteTag(eTag);
                objectStored.setObjectParts(objectParts);
                objectStored.setModified(curTime);
                objectStored.setComplete(true);
                objectStored.setPath(StorageDir.storage + '/' + bucketName + "/" + name);
                objectStoredRepository.save(objectStored);

                List<ObjectStored> objectStoreds = bucket.getObjectStoreds();
                objectStoreds.add(objectStored);
                bucket.setObjectStoreds(objectStoreds);
                bucket.setModified(curTime);
                bucketRepository.save(bucket);
                return objectStored;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (DecoderException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    public void deleteObject(String name, String bucketName){
        ObjectStored objectStored = objectStoredRepository.findObjectByObjectNameAndBucketName(name,bucketName);
        objectStoredRepository.delete(objectStored);
    }

    public ObjectStored getObject(String name, String bucketName){
        return objectStoredRepository.findObjectByObjectNameAndBucketName(name,bucketName);
    }


    public void addAndupdateMetadata(String name, String bucketName, String key, String value){
        ObjectStored objectStored = objectStoredRepository.findObjectByObjectNameAndBucketName(name,bucketName);
        HashMap<String,String> metadata = objectStored.getMetadata();
        metadata.put(key,value);
        objectStored.setMetadata(metadata);
        objectStoredRepository.save(objectStored);
    }

    public void deleteMetadata(String name, String bucketName,String key){
        ObjectStored objectStored = objectStoredRepository.findObjectByObjectNameAndBucketName(name,bucketName);
        HashMap<String,String> metadata = objectStored.getMetadata();
        metadata.remove(key);
        objectStored.setMetadata(metadata);
        objectStoredRepository.save(objectStored);
    }

    public String getMetadata(String name, String bucketName,String key){
        HashMap<String,String > metadata = objectStoredRepository.findObjectByObjectNameAndBucketName(name,bucketName).getMetadata();
        if (metadata.containsKey(key))return metadata.get(key);
        return "";
    }

    public HashMap<String,String> getAllMetadata(String name, String bucketName){
        return objectStoredRepository.findObjectByObjectNameAndBucketName(name,bucketName).getMetadata();
    }

    public boolean validateObjectName(String objectName){
        Pattern pattern = Pattern.compile("^(?![.])(?!.*[-_.]$).*");
        return pattern.matcher(objectName).find();
    }



}
