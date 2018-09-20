package io.backend.project0.repository;

import io.backend.project0.entity.ObjectStored;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectStoredRepository extends MongoRepository<ObjectStored,String> {
    public ObjectStored findObjectByObjectNameAndBucketName(String objectName, String bucketName);
    public boolean existsByObjectNameAndBucketName(String objectName,String bucketName);
}
