package io.backend.project0.controller;

import io.backend.project0.entity.ObjectPart;
import io.backend.project0.entity.ObjectStored;
import io.backend.project0.service.BucketService;
import io.backend.project0.service.ObjectPartService;
import io.backend.project0.service.ObjectStoredService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
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
        if(!objectStoredService.validateObjectName(objectName))return ResponseEntity.badRequest().body(null);
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

        if(!objectStoredService.isObjectExist(objectName,bucketName)){
            responseJSON.put("error","ObjectNotFound");
            return ResponseEntity.badRequest().body(responseJSON);
        }

        ObjectPart objectPart = objectPartService.uploadPart(objectName,content.length,md5,bucketName,partNumber,content);
        if(objectPart ==null){
            responseJSON.put("error","ObjectUploadComplete");
            return ResponseEntity.badRequest().body(responseJSON);
        }
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
        responseJSON.put("eTag",null);
        responseJSON.put("length",null);
        if (!objectStoredService.validateObjectName(objectName)){
            responseJSON.put("error","InvalidObjectName");
            return ResponseEntity.badRequest().body(responseJSON);
        }
        if (!bucketService.isBucketNameExist(bucketName)){
            responseJSON.put("error","InvalidBucket");
            return ResponseEntity.badRequest().body(responseJSON);
        }
        if(!objectStoredService.isObjectExist(objectName,bucketName)){
            responseJSON.put("error","ObjectNotFound");
            return ResponseEntity.badRequest().body(responseJSON);
        }

        ObjectStored objectStored = objectStoredService.complete(objectName,bucketName);
        if (objectStored==null)return ResponseEntity.badRequest().body(responseJSON);
        responseJSON.put("eTag",objectStored.geteTag());
        responseJSON.put("length",objectStored.getSize());

        return ResponseEntity.ok(responseJSON);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{bucketName}/{objectName}",params = "partNumber")
    public ResponseEntity deletePart(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) int partNumber

    ){

        if(partNumber <1 || partNumber>10000 || !objectPartService.isObjectPartExist(objectName,bucketName,partNumber)){
            return ResponseEntity.badRequest().body(null);
        }

        ObjectPart objectPart = objectPartService.deletePart(objectName,bucketName,partNumber);
        if (objectPart ==null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{bucketName}/{objectName}")
    public ResponseEntity deleteObject(
            @PathVariable String bucketName,
            @PathVariable String objectName
    ){

        if (!objectStoredService.isObjectExist(objectName,bucketName))return ResponseEntity.badRequest().body(null);
        objectStoredService.deleteObject(objectName,bucketName);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{bucketName}/{objectName}")
    @ResponseBody
    public ResponseEntity downloadObject(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            HttpServletRequest request
    ) throws IOException {

        ObjectStored objectStored = objectStoredService.getObject(objectName,bucketName);

        if(objectStored != null && objectStored.isComplete()){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            List<ObjectPart> objectParts = objectPartService.getPartsByBucketNameAndObjectName(bucketName,objectName);
            objectParts.sort(Comparator.comparingInt(ObjectPart::getPartNumber));

            for(ObjectPart op : objectParts){
                InputStream inputstream = new FileInputStream(op.getPath());
                outputStream.write(IOUtils.toByteArray(inputstream));
            }

            String range = request.getHeader("range");
            byte[] bytes = outputStream.toByteArray();
            if(range==null) range = "0-"+bytes.length;
            else{
                String[] r = range.split("-");
                if(r.length ==2){
                    int from = Integer.parseInt(r[0]);
                    int to = Integer.parseInt(r[1]);
                    if (to<from || from<0 || from> bytes.length || to>bytes.length )return ResponseEntity.badRequest().body("Invalid Range");
                    bytes = Arrays.copyOfRange(bytes,from,to+1);

                }else{
                    return ResponseEntity.badRequest().body("Invalid Range");
                }

            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + objectStored.getObjectName()+"\"")
                    .header(HttpHeaders.ACCEPT_RANGES,range)
                    .contentLength(bytes.length)
                    .eTag(objectStored.geteTag())
                    .body(bytes);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/{bucketName}/{objectName}",params = {"metadata","key"})
    public ResponseEntity addAndUpdateMetadataByKey(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String metadata,
            @RequestParam(required = true) String key,
            HttpServletRequest request

    ) throws IOException {
        if(!objectStoredService.isObjectExist(objectName,bucketName)) return ResponseEntity.notFound().build();
        InputStream inputStream = request.getInputStream();
        String value = IOUtils.toString(inputStream,"UTF-8");
        objectStoredService.addAndUpdateMetadata(objectName,bucketName,key,value);
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
        if(!ret.equals("")) responseJSON.put(key,ret);
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

    //Milestone 3
    @RequestMapping(method = RequestMethod.GET,value = "/{bucketName}/{objectName}",params = "display")
    public void displayGIF(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String display,
            HttpServletResponse response
    ) throws IOException {

        ObjectStored objectStored = objectStoredService.getObject(objectName, bucketName);

        if (objectStored != null && objectStored.isComplete()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            List<ObjectPart> objectParts = objectPartService.getPartsByBucketNameAndObjectName(bucketName, objectName);
            objectParts.sort(Comparator.comparingInt(ObjectPart::getPartNumber));

            for (ObjectPart op : objectParts) {
                InputStream inputstream = new FileInputStream(op.getPath());
                outputStream.write(IOUtils.toByteArray(inputstream));
            }
            response.setContentType("image/gif");
            response.getOutputStream().write(outputStream.toByteArray());

            response.getOutputStream().close();
        }
    }

}
