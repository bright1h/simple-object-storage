package io.backend.project0.repository;

import io.backend.project0.entity.Bucket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BucketRepository extends MongoRepository<Bucket,String> {
    public Bucket findBucketByBucketName(String bucketName);
}
