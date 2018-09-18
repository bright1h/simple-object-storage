package io.backend.project0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class ObjectController {

    @Autowired
    private ObjectService objectService;

    @RequestMapping(method = RequestMethod.POST,value = "/{bucketName}/{objectName}",params = "create")
    public ResponseEntity createUploadTicket(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam(required = true) String create

    ){
//        bucketService.createUploadTicket(bucketName,objectName);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/{bucketName}/{objectName}", params = "partNumber")
    public ResponseEntity uploadPart(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestHeader(value = "Content-Length") String contentLength,
            @RequestHeader(value = "Content-MD5") String md5,
            @RequestParam(required = true) String partNumber

    ){
//        bucketService.uploadPart(bucketName,objectName);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/{bucketName}/{objectName}",params = "complete")
    public ResponseEntity completeMultiPartUpload(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestHeader(value = "Content-Length") String contentLength,
            @RequestHeader(value = "Content-MD5") String md5,
            @RequestParam(required = true) String complete

    ){
//        bucketService.completeMultiPartUpload(bucketName,objectName);
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
