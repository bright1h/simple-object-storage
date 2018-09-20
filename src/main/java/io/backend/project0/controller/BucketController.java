package io.backend.project0.controller;

import io.backend.project0.entity.Bucket;
import io.backend.project0.service.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
public class BucketController {

    @Autowired
    private BucketService bucketService;

    @RequestMapping("/buckets")
    public List<Bucket> getAllBuckets(){
        return bucketService.getAllBucket();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{bucketName}",params = "create")
    public ResponseEntity createBucket(
            @PathVariable String bucketName,
            @RequestParam(required = true) String create

    ){
        if(!bucketService.validateBucketName(bucketName)){
            return ResponseEntity.badRequest().body("Invalid bucket's name");
        }

        Bucket bucket =bucketService.create(bucketName);
        if(bucket == null) return ResponseEntity.badRequest().body("Already used");

        HashMap<String, Object> responseJSON = new HashMap<>();
        responseJSON.put("created",bucket.getCreated());
        responseJSON.put("modified",bucket.getModified());
        responseJSON.put("name",bucket.getBucketName());
        return ResponseEntity.ok(responseJSON);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{bucketName}")
    public ResponseEntity deleteBucket(
            @PathVariable String bucketName,
            @RequestParam(value = "delete",required = true) String param

    ){
        if(!bucketService.isBucketNameExist(bucketName)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        bucketService.delete(bucketName);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{bucketName}")
    public ResponseEntity getBucketList(
            @PathVariable String bucketName,
            @RequestParam(value = "list",required = true) String param
    ){
        Bucket bucket =bucketService.getBucket(bucketName);
        if (bucket==null)return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        HashMap<String, Object> responseJSON = new HashMap<>();
        responseJSON.put("created",bucket.getCreated());
        responseJSON.put("modified",bucket.getModified());
        responseJSON.put("name",bucket.getBucketName());
        responseJSON.put("objects", bucket.getObjectStoreds());

        return ResponseEntity.ok(responseJSON);
    }

}
