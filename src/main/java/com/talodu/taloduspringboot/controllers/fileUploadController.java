package com.talodu.taloduspringboot.controllers;

import com.talodu.taloduspringboot.model.User;
import com.talodu.taloduspringboot.repository.UserRepository;
import com.talodu.taloduspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = {"http://51.68.196.188", "http://localhost:3000"}, allowedHeaders = "*", allowCredentials = "true")
public class fileUploadController  {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Value("${file.upload-dir}")
    String  FILE_DIRECTORY;

    @PostMapping("uploadfile")

    public ResponseEntity<Object> uploadFile(@RequestParam("File") MultipartFile file, @RequestParam("Email") String  email)  throws IOException  {

        //final long limit = 500 * 1024 * 1024; // 500MB

        //if (file.getSize() > limit) {
           // return new ResponseEntity<Object>("File too big", HttpStatus.valueOf(505));
        //}


        File currDir = new File(".");
        String path = currDir.getAbsolutePath();

        //path = path.substring(0, path.length()-1)+"images/";
        path = path.substring(0, path.length()-1)+"img/";


        System.out.println("The path is.." + path+ file.getOriginalFilename());

        //System.out.println( path.length()-1)+ file.getOriginalFilename());
        final String file_path = FILE_DIRECTORY + file.getOriginalFilename();

        try{

            File myFile = new File(file_path);
            //File myFile = new File(path + file.getOriginalFilename());
            //File myFile = new File( file.getOriginalFilename());

            myFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(myFile);
            fos.write(file.getBytes());
            fos.close();

           User user = this.userService.getUserByEmailAddress(email);

            user.setProfileImagePath(file.getOriginalFilename());

            this.userRepository.save(user);




            System.out.println("success...");
            return new ResponseEntity<Object>(user, HttpStatus.CREATED);

        } catch(Exception e){

            System.out.println(e);
            return new ResponseEntity<Object>("File upload failed12..." +e , HttpStatus.EXPECTATION_FAILED);


        }



    }




}
