package io.backend.project0;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Bucket {

    @Id
    private String name;


    private long created;
    private long modified;
    private List<Object> objects;

    public Bucket(long created, long modified, String name){
        super();
        this.created = created;
        this.modified =modified;
        this.name= name;
        this.objects = new ArrayList<>();
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public List<Object> getObjects() {
        return objects;
    }

    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }
}
