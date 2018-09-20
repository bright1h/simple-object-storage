package io.backend.project0.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Bucket {

    @Id
    private String bucketName;
    private long created;
    private long modified;

    @DBRef
    private List<ObjectStored> objectStoreds;

    public Bucket(long created, long modified, String bucketName){
        super();
        this.created = created;
        this.modified =modified;
        this.bucketName= bucketName;
        this.objectStoreds = new ArrayList<>();
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public List<ObjectStored> getObjectStoreds() {
        return objectStoreds;
    }

    public void setObjectStoreds(List<ObjectStored> objectStoreds) {
        this.objectStoreds = objectStoreds;
    }
}
