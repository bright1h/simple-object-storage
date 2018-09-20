package io.backend.project0.service;

import io.backend.project0.StorageDir;
import io.backend.project0.entity.Bucket;
import io.backend.project0.entity.Object;
import io.backend.project0.entity.ObjectPart;
import io.backend.project0.repository.BucketRepository;
import io.backend.project0.repository.ObjectRepository;
import io.backend.project0.repository.ObjectPartRepository;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ObjectService {

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private ObjectPartRepository objectPartRepository;

    @Autowired
    private BucketRepository bucketRepository;

    public List<Object> getAllObject(){
//        List<Object> objects = new ArrayList<>();
//        objectRepository.findAll().forEach(objects::add);
//        return objects;
        return objectRepository.findAll();
    }

    public Object createTicket(String name, String bucketName) {
        if (isObjectExist(name, bucketName)) return null;
        long createdTime = System.currentTimeMillis();
        Object object = new Object(name,createdTime,createdTime,bucketName);
//        Bucket bucket = bucketRepository.findBucketByBucketName(bucketName);
//        List<Object> objects = bucket.getObjects();
//        objects.add(object);
//        bucket.setObjects(objects);
//        bucketRepository.save(bucket);
        objectRepository.save(object);

        return object;
    }

    public boolean isObjectExist(String name, String bucketName){
        //a.k.a. check if this bucket Already Exist
        return objectRepository.existsByObjectNameAndBucketName(name,bucketName);
    }

    public Object complete(String name, String bucketName){
        Bucket bucket = bucketRepository.findBucketByBucketName(bucketName);
        Object object = objectRepository.findObjectByObjectNameAndBucketName(name,bucketName);

        if(!object.isComplete()) {
            List<ObjectPart> objectParts = objectPartRepository.findAllByBucketNameAndObjectName(bucketName, name);
            objectParts.sort(Comparator.comparingInt(ObjectPart::getPartNumber));
            HashMap<String,String> eTag = object.geteTag();
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
                object.setMd5(newMd5);
                long curTime = System.currentTimeMillis();
                System.out.println(eTag);
                object.seteTag(eTag);
                object.setObjectParts(objectParts);
                object.setModified(curTime);
                object.setComplete(true);
                object.setPath(StorageDir.storage + '/' + bucketName + "/" + name);
                objectRepository.save(object);

                List<Object> objects = bucket.getObjects();
                objects.add(object);
                bucket.setObjects(objects);
                bucket.setModified(curTime);
                bucketRepository.save(bucket);
                return object;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (DecoderException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    public void deleteObject(String name, String bucketName){
        Object object = objectRepository.findObjectByObjectNameAndBucketName(name,bucketName);
        objectRepository.delete(object);
    }

    public Object getObject(String name, String bucketName){
        //get, download do in controller
        return objectRepository.findObjectByObjectNameAndBucketName(name,bucketName);
    }

//
//    public Object addAndupdateMetadata(String name, String bucketName, String key){
//        return null;
//    }
//
//    public Object deleteMetadata(String name, String bucketName,String key){
//        return null;
//    }
//
//    public Object getMetadata(String name, String bucketName,String key){
//        //get, return string of meta data
//        return null;
//    }
//
//    public Object getAllMetadata(String name, String bucketName){
//        return null;
//    }

    public boolean validateObjectName(String objectName){
        Pattern pattern = Pattern.compile("^(?![.])(?!.*[-_.]$).*");
        return pattern.matcher(objectName).find();
    }



}
