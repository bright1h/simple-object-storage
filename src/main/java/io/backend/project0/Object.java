package io.backend.project0;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Document
public class Object {
    @Id
    private String name;

    private long created;
    private long modified;
    private HashMap<String,String> eTag;
    private HashMap<String,String> metadata;
    private String bucketName;

    public Object(String name, long created,long modified,String bucketName){
        super();
        this.name=name;
        this.created=created;
        this.modified=modified;
        this.eTag= new HashMap<>();
        this.bucketName = bucketName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public HashMap<String, String> geteTag() {
        return eTag;
    }

    public void seteTag(HashMap<String, String> eTag) {
        this.eTag = eTag;
    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
