package io.backend.project0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class BucketController {

    private String uploadDirectory = "/home/bright/Desktop/backend/test_storage";

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
        Bucket bucket =bucketService.create(bucketName);
        if (bucket==null)return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bucket's name already used or invalid");
        return ResponseEntity.ok(bucket);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{bucketName}")
    public ResponseEntity deleteBucket(
            @PathVariable String bucketName,
            @RequestParam(value = "delete",required = true) String param

    ){
        Bucket bucket =bucketService.delete(bucketName);
        System.out.println(bucket);
        if (bucket==null)return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bucket's name does not exist");
        return ResponseEntity.ok(bucket);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{bucketName}")
    public ResponseEntity getBucketList(
            @PathVariable String bucketName,
            @RequestParam(value = "list",required = true) String param
    ){
        Bucket bucket =bucketService.getBucket(bucketName);
        if (bucket==null)return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bucket's name does not exist");
        return ResponseEntity.ok(bucket);
    }

    @RequestMapping("/upload")
    public String upload(Model model, @RequestParam("files") MultipartFile[] files,@RequestHeader("Content-Length")String length){
        System.out.println(length);
//        System.out.println(md5);
        StringBuilder fileNames = new StringBuilder();
        for(MultipartFile file : files){
            try {
                System.out.println(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Path fileNameAndPath = Paths.get(uploadDirectory,file.getOriginalFilename());
            System.out.println(fileNameAndPath.getParent().toUri());
            fileNames.append(file.getOriginalFilename());
            try{
                Files.write(fileNameAndPath,file.getBytes());
            }catch (IOException e){
                e.printStackTrace();
            }
            model.addAttribute("msg","Successfully upload files " + fileNames.toString());
        }
        return "uploaded";
    }
}
