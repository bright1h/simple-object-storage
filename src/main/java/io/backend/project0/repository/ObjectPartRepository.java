package io.backend.project0.repository;

import io.backend.project0.entity.ObjectPart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectPartRepository extends MongoRepository<ObjectPart, Long> {

    public ObjectPart findByBucketNameAndObjectNameAndPartNumber(String bucketName, String objectName, int partNumber);

    public List<ObjectPart> findAllByBucketNameAndObjectName(String bucketName, String objectName );
}
