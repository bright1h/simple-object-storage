package io.backend.project0.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Bucket {

    @Id
    private String bucketName;
    private long created;
    private long modified;

    public Bucket(long created, long modified, String bucketName){
        this.created = created;
        this.modified =modified;
        this.bucketName= bucketName;
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

}
