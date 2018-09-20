package io.backend.project0.entity;

import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;

//import javax.persistence.*;
//import java.util.HashMap;
//import java.util.List;

@Document
public class ObjectStored {


//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long objecttId;

    @Id
    private String objectName;

    private long created;
    private long modified;
    private HashMap<String,String> eTag;
    private HashMap<String,String> metadata;
    private String bucketName;
    private String path;
    private String md5;
    private boolean complete;

    private List<ObjectPart> objectParts;

    public ObjectStored(String objectName, long created, long modified, String bucketName){
        super();
        this.objectName=objectName;
        this.created=created;
        this.modified=modified;
        this.eTag= new HashMap<>();
        this.bucketName = bucketName;
        this.complete=false;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
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

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public List<ObjectPart> getObjectParts() {
        return objectParts;
    }

    public void setObjectParts(List<ObjectPart> objectParts) {
        this.objectParts = objectParts;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
