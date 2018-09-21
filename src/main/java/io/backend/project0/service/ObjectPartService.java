package io.backend.project0.service;

import io.backend.project0.StorageDir;
import io.backend.project0.entity.ObjectStored;
import io.backend.project0.entity.ObjectPart;
import io.backend.project0.repository.ObjectPartRepository;
import io.backend.project0.repository.ObjectStoredRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ObjectPartService {

    @Autowired
    private ObjectPartRepository objectPartRepository;
    
    @Autowired
    private ObjectStoredRepository objectStoredRepository;

    public List<ObjectPart> getAllPart(){
        return objectPartRepository.findAll();
    }

    public ObjectPart uploadPart(String objectName, long partSize, String partMd5, String bucketName, int partNumber, byte[] file){
        ObjectStored objectStored = objectStoredRepository.findByObjectNameAndBucketName(objectName,bucketName);
        if(!objectStored.isComplete()) {
            String[] splittedName = objectName.split("\\.");
            String partName = splittedName[0] + '-' + partNumber;
            String path = StorageDir.storage + '/' + bucketName + '/' +partName ;

            ObjectPart objectPart = objectPartRepository.findByBucketNameAndObjectNameAndPartNumber(bucketName,objectName,partNumber);
            if(objectPart !=null){
                objectPartRepository.delete(objectPart);
            }
            objectPart = new ObjectPart(partName, partSize, partMd5, partNumber, path, bucketName, objectName);

            Path fileNameAndPath = Paths.get(path);
            try {
                Files.write(fileNameAndPath, file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            objectPartRepository.save(objectPart);
            return objectPart;

        }
        return null;
    }

    public ObjectPart deletePart(String objectName, String bucketName,int partNumber){
        ObjectStored objectStored = objectStoredRepository.findByObjectNameAndBucketName(objectName,bucketName);
        if(objectStored == null || objectStored.isComplete()){
            return null;
        }
        ObjectPart objectPart = objectPartRepository.findByBucketNameAndObjectNameAndPartNumber(bucketName,objectName,partNumber);
        objectPartRepository.delete(objectPart);
        return objectPart;
    }

    public boolean isObjectPartExist(String name,String bucketName,int partNumber){
        return objectPartRepository.findByBucketNameAndObjectNameAndPartNumber(bucketName,name,partNumber) !=null;
    }

    public List<ObjectPart> getPartsByBucketNameAndObjectName(String bucketName,String objectName){
        return objectPartRepository.findAllByBucketNameAndObjectName(bucketName,objectName);
    }

}
