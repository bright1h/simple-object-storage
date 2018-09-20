package io.backend.project0.controller;

import io.backend.project0.entity.Object;
import io.backend.project0.entity.ObjectPart;
import io.backend.project0.service.BucketService;
import io.backend.project0.service.ObjectService;
import io.backend.project0.service.ObjectPartService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class ObjectController {

    @Autowired
    private ObjectService objectService;

    @Autowired
    private BucketService bucketService;

    @Autowired
    private ObjectPartService objectPartService;

    @RequestMapping("/parts")
    public List<ObjectPart> getAllPart(){
        return objectPartService.getAllPart();
    }

    @RequestMapping("/objects")
    public List<Object> getAllObject(){
        return objectService.getAllObject();
    }

    @RequestMapping(method = RequestMethod.POST,value = "/{bucketName}/{objectName}",params = "create")
    public ResponseEntity createUploadTicket(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String create

    ){
        if(!bucketService.isBucketNameExist(bucketName))return ResponseEntity.badRequest().body(null);
        Object object = objectService.createTicket(objectName,bucketName);
        if(object==null)return ResponseEntity.badRequest().body("fail");
        return ResponseEntity.ok(object);
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/{bucketName}/{objectName}", params = "partNumber")
    public ResponseEntity uploadPart(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestHeader(value = "Content-Length") long contentLength,
            @RequestHeader(value = "Content-MD5") String md5,
            @RequestParam(required = true) int partNumber,
            HttpServletRequest request

    ) throws IOException{
        InputStream inputStream = request.getInputStream();
        byte[] content = IOUtils.toByteArray(inputStream);

        if(contentLength !=  content.length){
            return ResponseEntity.badRequest().body("Size problem");
        }

        String md5Hex = DigestUtils.md5Hex(content);

        System.out.println("hexmd5: "+md5Hex);
        System.out.println("md5: " +md5);
        if(!md5Hex.equals(md5)){
            return ResponseEntity.badRequest().body("md5 problem");
        }

        System.out.println("partNumber: "+partNumber);
        if(partNumber <1 || partNumber>10000){
            return ResponseEntity.badRequest().body("partNumber problem");
        }

        if(!objectService.validateObjectName(objectName)){
            return ResponseEntity.badRequest().body("objectName");
        }

        if(!bucketService.isBucketNameExist(bucketName)){
            return ResponseEntity.badRequest().body("bucket not exist");
        }
//
        ObjectPart objectPart = objectPartService.uploadPart(objectName,content.length,md5,bucketName,partNumber,content);
        if(objectPart ==null)return ResponseEntity.badRequest().body("fail");
//        json { "md5": {md5}, "length": 1024, "partNumber": 1 }
//        json { "md5": {md5}, "length": 1024, "partNumber": 1 "error": "LengthMismatched|MD5Mismatched|InvalidPartNumber|InvalidObjectName|InvalidBucket" }
        return ResponseEntity.ok("success");
    }

    @RequestMapping(method = RequestMethod.POST,value = "/{bucketName}/{objectName}",params = "complete")
    public ResponseEntity completeMultiPartUpload(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String complete

    ){
//        bucketService.completeMultiPartUpload(bucketName,objectName);
        if (!objectService.validateObjectName(objectName)) return ResponseEntity.badRequest().body(null);
        if (!bucketService.isBucketNameExist(bucketName)) return ResponseEntity.badRequest().body(null);
        Object object =objectService.complete(objectName,bucketName);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{bucketName}/{objectName}",params = "partNumber")
    public ResponseEntity deletePart(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String partNumber

    ){
//        bucketService.uploadAllParts(bucketName,objectName);
        return ResponseEntity.ok(null);
    }
//
//
    @RequestMapping(method = RequestMethod.GET,value = "/{bucketName}/{objectName}")
    @ResponseBody
    public ResponseEntity downloadObject(
            @PathVariable String bucketName,
            @PathVariable String objectName
    ){
//        Resource file = storageService.loadAsResource(filename);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        return null;
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/{bucketName}/{objectName}",params = {"metadata","key"})
    public ResponseEntity addAndUpdateMetadataByKey(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String metadata,
            @RequestParam(required = true) String key

    ){
//        bucketService.uploadAllParts(bucketName,objectName);
        return ResponseEntity.ok(null);
    }


    @RequestMapping(method = RequestMethod.DELETE,value = "/{bucketName}/{objectName}",params = {"metadata","key"})
    public ResponseEntity deleteMetadataByKey(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String metadata,
            @RequestParam(required = true) String key

    ){
//        bucketService.uploadAllParts(bucketName,objectName);
        return ResponseEntity.ok(null);
    }

//
    @RequestMapping(method = RequestMethod.GET,value = "/{bucketName}/{objectName}",params = {"metadata","key"})
    public ResponseEntity getMetadataByKey(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(value = "metadata",required = true) String metadata,
            @RequestParam(value = "key",required = true) String key

    ){
//        bucketService.uploadAllParts(bucketName,objectName);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{bucketName}/{objectName}",params = "metadata")
    public ResponseEntity getAllMetadata(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String metadata

    ){
//        bucketService.uploadAllParts(bucketName,objectName);
        return ResponseEntity.ok(null);
    }

}
