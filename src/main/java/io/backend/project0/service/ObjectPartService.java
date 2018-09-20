package io.backend.project0.service;

import io.backend.project0.StorageDir;
import io.backend.project0.entity.Object;
import io.backend.project0.entity.ObjectPart;
import io.backend.project0.repository.ObjectPartRepository;
import io.backend.project0.repository.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ObjectPartService {

    @Autowired
    private ObjectPartRepository objectPartRepository;
    
    @Autowired
    private ObjectRepository objectRepository;

    public List<ObjectPart> getAllPart(){
//        List<ObjectPart> objectParts= new ArrayList<>();
//        objectPartRepository.findAll().forEach(objectParts::add);
//        return objectParts;
        return objectPartRepository.findAll();
    }

    public ObjectPart uploadPart(String name, long partSize, String partMd5, String bucketName, int partNumber, byte[] file){
        Object object = objectRepository.findObjectByObjectNameAndBucketName(name,bucketName);
        if(!object.isComplete()) {
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
        Object object = objectRepository.findObjectByObjectNameAndBucketName(name,bucketName);
        if(object == null || object.isComplete()){
            return null;
        }
        ObjectPart objectPart = objectPartRepository.findByBucketNameAndObjectNameAndPartNumber(bucketName,name,partNumber);
        objectPartRepository.delete(objectPart);
        return objectPart;
    }

}
