package io.backend.project0;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BucketRepository extends MongoRepository<Bucket,String> {
    public Bucket findBucketByName(String name);
}
