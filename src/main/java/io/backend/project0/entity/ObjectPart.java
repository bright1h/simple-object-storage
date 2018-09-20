package io.backend.project0.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class ObjectPart {

    @Id
    private ObjectId _id;

    private String partName;
    private long partSize;
    private String partMd5;
    private int partNumber;
    private String path;
    private String objectName;
    private String bucketName;


    public ObjectPart(String partName, long partSize, String partMd5, int partNumber, String path, String bucketName, String objectName){
        super();
        this.partName=partName;
        this.partSize=partSize;
        this.partMd5=partMd5;
        this.partNumber=partNumber;
        this.path=path;
        this.bucketName=bucketName;
        this.objectName=objectName;
    }


    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public long getPartSize() {
        return partSize;
    }

    public void setPartSize(long partSize) {
        this.partSize = partSize;
    }

    public String getPartMd5() {
        return partMd5;
    }

    public void setPartMd5(String partMd5) {
        this.partMd5 = partMd5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public String toString(){
        return " partName: " + this.getPartName() +" partSize: " + this.getPartSize()+" partMd5: " + this.getPartMd5()+" path: " + this.getPath() +" bucketName: " + this.getBucketName()+" objectName: " + this.getObjectName();
    }
}
