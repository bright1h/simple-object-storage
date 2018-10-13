package io.backend.project0.controller;

import io.backend.project0.entity.ObjectPart;
import io.backend.project0.entity.ObjectStored;
import io.backend.project0.service.BucketService;
import io.backend.project0.service.ObjectPartService;
import io.backend.project0.service.ObjectStoredService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

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
    public void downloadObject(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            HttpServletRequest request,
            HttpServletResponse response

    ) throws IOException {

        ObjectStored objectStored = objectStoredService.getObject(objectName,bucketName);

        if(objectStored != null && objectStored.isComplete()){
            List<ObjectPart> objectParts = objectPartService.getPartsByBucketNameAndObjectName(bucketName,objectName);
            objectParts.sort(Comparator.comparingInt(ObjectPart::getPartNumber));
            List<InputStream> neededFile = new ArrayList<>();

            String range = request.getHeader("range");
            long from = 0;
            long to = objectStored.getSize();
            if(range==null) range = "0-"+objectStored.getSize();
            else{
                String[] r = range.split("-");
                if(r.length ==2){
                    from = Long.parseLong(r[0]);
                    to = Long.parseLong(r[1]);
                    if (to<from || from<0 || from> objectStored.getSize() || to>objectStored.getSize() )response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }else{
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            }

            long curTotalSize = 0;
            for(ObjectPart op : objectParts){
                if(curTotalSize>to) break;
                InputStream inputstream = new FileInputStream(op.getPath());
                long boundedSize = op.getPartSize();
                if(curTotalSize<from && curTotalSize+op.getPartSize()>=from) {
                    long skip = inputstream.skip(from-curTotalSize);
                    if(curTotalSize+op.getPartSize()>to) {
                        boundedSize = to - from;
                    }
                    else boundedSize += curTotalSize-from;
                }
                else if(curTotalSize+op.getPartSize()>to){
                    boundedSize = to-curTotalSize;
                }

                BoundedInputStream boundedInputStream = new BoundedInputStream(inputstream, boundedSize);
                neededFile.add(boundedInputStream);
                curTotalSize += op.getPartSize();
            }

            if(objectStored.getMetadata().containsKey("content-type"))
                response.setHeader(HttpHeaders.CONTENT_TYPE,objectStored.getMetadata().get("content-type"));

            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + objectStored.getObjectName()+"\"");
            response.setHeader(HttpHeaders.ACCEPT_RANGES,range);
            response.setHeader("eTag", objectStored.geteTag());
            SequenceInputStream sequenceInputStream = new SequenceInputStream(Collections.enumeration(neededFile));
            IOUtils.copyLarge(sequenceInputStream,response.getOutputStream());
            response.getOutputStream().close();
        }
        else response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
    public void requestFile(
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
            String contentType = objectStored.getMetadata().get("content-type");
            if (contentType !=null) response.setContentType(contentType);
            response.getOutputStream().write(outputStream.toByteArray());
            response.getOutputStream().close();
        }
    }

}
