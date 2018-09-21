package io.backend.project0.repository;

import io.backend.project0.entity.ObjectStored;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectStoredRepository extends MongoRepository<ObjectStored,String> {

    public ObjectStored findByObjectNameAndBucketName(String objectName,String bucketName);

//    public ObjectStored findObjectStoredByObjectNameAndBucketName(String objectName, String bucketName);

    public boolean existsByObjectNameAndBucketName(String objectName,String bucketName);

    public void deleteAllByBucketName(String bucketName);

    public List<ObjectStored> findAllByBucketName(String bucketName);
}
