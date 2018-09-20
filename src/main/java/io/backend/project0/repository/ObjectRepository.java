package io.backend.project0.repository;

import io.backend.project0.entity.Object;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectRepository extends MongoRepository<Object,String> {
    public Object findObjectByObjectNameAndBucketName(String objectName,String bucketName);
    public boolean existsByObjectNameAndBucketName(String objectName,String bucketName);
}
