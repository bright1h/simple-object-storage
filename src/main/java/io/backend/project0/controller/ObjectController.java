package io.backend.project0.controller;

import io.backend.project0.entity.ObjectStored;
import io.backend.project0.entity.ObjectPart;
import io.backend.project0.service.BucketService;
import io.backend.project0.service.ObjectStoredService;
import io.backend.project0.service.ObjectPartService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@RestController
public class ObjectController {

    @Autowired
    private ObjectStoredService objectStoredService;

    @Autowired
    private BucketService bucketService;

    @Autowired
    private ObjectPartService objectPartService;

    @RequestMapping("/parts")
    public List<ObjectPart> getAllPart(){
        return objectPartService.getAllPart();
    }

    @RequestMapping("/objects")
    public List<ObjectStored> getAllObject(){
        return objectStoredService.getAllObject();
    }

    @RequestMapping(method = RequestMethod.POST,value = "/{bucketName}/{objectName}",params = "create")
    public ResponseEntity createUploadTicket(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String create

    ){
        if(!bucketService.isBucketNameExist(bucketName))return ResponseEntity.badRequest().body(null);
        ObjectStored objectStored = objectStoredService.createTicket(objectName,bucketName);
        if(objectStored ==null)return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(null);
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

        HashMap<String, Object> responseJSON = new HashMap<>();
        responseJSON.put("md5",md5);
        responseJSON.put("length",contentLength);
        responseJSON.put("partNumber",partNumber);

        if(contentLength !=  content.length){
            responseJSON.put("error","LengthMismatched");
            return ResponseEntity.badRequest().body(responseJSON);
        }

        String md5Hex = DigestUtils.md5Hex(content);

        if(!md5Hex.equals(md5)){
            responseJSON.put("error","MD5Mismatched");
            return ResponseEntity.badRequest().body(responseJSON);
        }

        if(partNumber <1 || partNumber>10000){
            responseJSON.put("error","InvalidPartNumber");
            return ResponseEntity.badRequest().body(responseJSON);
        }

        if(!objectStoredService.validateObjectName(objectName)){
            responseJSON.put("error","InvalidObjectName");
            return ResponseEntity.badRequest().body(responseJSON);
        }

        if(!bucketService.isBucketNameExist(bucketName)){
            responseJSON.put("error","InvalidBucket");
            return ResponseEntity.badRequest().body(responseJSON);
        }
//
        ObjectPart objectPart = objectPartService.uploadPart(objectName,content.length,md5,bucketName,partNumber,content);
        if(objectPart ==null)return ResponseEntity.badRequest().body("UnexpectedError");
        return ResponseEntity.ok(responseJSON);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/{bucketName}/{objectName}",params = "complete")
    public ResponseEntity completeMultiPartUpload(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String complete

    ){
        HashMap<String, Object> responseJSON = new HashMap<>();
        responseJSON.put("name",objectName);
        if (!objectStoredService.validateObjectName(objectName)){
            responseJSON.put("error","InvalidObjectName");
            return ResponseEntity.badRequest().body(responseJSON);
        }
        if (!bucketService.isBucketNameExist(bucketName)){
            responseJSON.put("error","InvalidBucket");
            return ResponseEntity.badRequest().body(responseJSON);
        }
        ObjectStored objectStored = objectStoredService.complete(objectName,bucketName);
        if (objectStored==null)ResponseEntity.badRequest().body("UnexpectedError");
        responseJSON.put("eTag",objectStored.geteTag());

        return ResponseEntity.ok(responseJSON);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{bucketName}/{objectName}",params = "partNumber")
    public ResponseEntity deletePart(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) int partNumber

    ){
        if (!objectStoredService.validateObjectName(objectName)){
            return ResponseEntity.badRequest().body(null);
        }
        if (!bucketService.isBucketNameExist(bucketName)){
            return ResponseEntity.badRequest().body(null);
        }
        if(partNumber <1 || partNumber>10000){
            return ResponseEntity.badRequest().body(null);
        }

        ObjectPart objectPart = objectPartService.deletePart(objectName,bucketName,partNumber);
        if (objectPart ==null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(null);
    }

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
            @RequestParam(required = true) String key,
            String value

    ){
        if(!objectStoredService.isObjectExist(objectName,bucketName)) return ResponseEntity.notFound().build();
        objectStoredService.addAndupdateMetadata(objectName,bucketName,key,value);
        return ResponseEntity.ok(null);
    }


    @RequestMapping(method = RequestMethod.DELETE,value = "/{bucketName}/{objectName}",params = {"metadata","key"})
    public ResponseEntity deleteMetadataByKey(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String metadata,
            @RequestParam(required = true) String key

    ){
        if(!objectStoredService.isObjectExist(objectName,bucketName)) return ResponseEntity.notFound().build();
        objectStoredService.deleteMetadata(objectName,bucketName,key);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{bucketName}/{objectName}",params = {"metadata","key"})
    public ResponseEntity getMetadataByKey(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(value = "metadata",required = true) String metadata,
            @RequestParam(value = "key",required = true) String key

    ){
        if(!objectStoredService.isObjectExist(objectName,bucketName)) return ResponseEntity.notFound().build();
        HashMap<String, Object> responseJSON = new HashMap<>();
        String ret = objectStoredService.getMetadata(objectName,bucketName,key);
        if(ret != "") responseJSON.put(key,ret);
        return ResponseEntity.ok(responseJSON);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{bucketName}/{objectName}",params = "metadata")
    public ResponseEntity getAllMetadata(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String metadata

    ){
        if(!objectStoredService.isObjectExist(objectName,bucketName)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(objectStoredService.getAllMetadata(objectName,bucketName));
    }

}
