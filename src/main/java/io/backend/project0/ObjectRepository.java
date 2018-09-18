package io.backend.project0;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectRepository extends MongoRepository<Object,String> {
    public Object findObjectByNameAndAndBucketName(String name,String bucketName);
    public boolean existsByNameAndBucketName(String name,String bucketName);
}
