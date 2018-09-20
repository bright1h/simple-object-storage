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

    public ObjectPart uploadPart(String name, long partSize, String partMd5, String bucketName, int partNumber, byte[] file){
        ObjectStored objectStored = objectStoredRepository.findObjectByObjectNameAndBucketName(name,bucketName);
        if(!objectStored.isComplete()) {
            String path = StorageDir.storage + '/' + bucketName + '/';
            String[] splittedName = name.split("\\.");
            System.out.println(splittedName);
            //Don't care about extension(for now?)
            String partName = splittedName[0] + '-' + partNumber;
            ObjectPart objectPart = new ObjectPart(partName, partSize, partMd5, partNumber, path, bucketName, name);
            objectPartRepository.save(objectPart);

            Path fileNameAndPath = Paths.get(path + partName);
            try {
                Files.write(fileNameAndPath, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return objectPart;
        }
        return null;
    }

    public ObjectPart deletePart(String name, String bucketName,int partNumber){
        ObjectStored objectStored = objectStoredRepository.findObjectByObjectNameAndBucketName(name,bucketName);
        if(objectStored == null || objectStored.isComplete()){
            return null;
        }
        ObjectPart objectPart = objectPartRepository.findByBucketNameAndObjectNameAndPartNumber(bucketName,name,partNumber);
        objectPartRepository.delete(objectPart);
        return objectPart;
    }

}
