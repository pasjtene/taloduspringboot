package com.talodu.taloduspringboot.server.controller;

import com.talodu.taloduspringboot.server.Service.ServerService;
import com.talodu.taloduspringboot.server.enumeration.Status;
import com.talodu.taloduspringboot.server.model.Response;
import com.talodu.taloduspringboot.server.model.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/api/server")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://51.68.196.188", "http://localhost:3000","http://localhost:4200"},
        allowedHeaders = "*", allowCredentials = "true")

public class ServerController {
    private final ServerService serverService;

    @GetMapping("/list")
    public ResponseEntity<Response> getServers() throws InterruptedException {
        //add delais of 3secs
        TimeUnit.SECONDS.sleep(2);
        //Throw an error for testing
        //throw new RuntimeException("Something went wrong");

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("servers", serverService.list(30) ))
                        .message("Servers retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());

        /*
        //Return an error for simulation
        return ResponseEntity.internalServerError().body(
                Response.builder()
                .timeStamp(LocalDateTime.now())
                .data(Map.of("servers", serverService.list(30) ))
                .message("Servers retrieved")
                .status(INTERNAL_SERVER_ERROR)
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .build());
            */

      //  );

    }

    @GetMapping("/ping/{ipAddress}")
    public ResponseEntity<Response> pingServer(@PathVariable("ipAddress") String ipAddress) throws IOException {
        Server server = serverService.ping(ipAddress);
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("server", server))
                        .message(server.getStatus() == Status.SERVER_UP? "Ping success":"Ping failed")
                        .status(OK) //200
                        .statusCode(OK.value())
                        .build()

        );
    }

    @PostMapping("/save")
    public ResponseEntity<Response> saveServers(@RequestBody @Valid  Server server) {
       log.info("Adding server with ip , {}", server.getIpAddress());
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("server", serverService.create(server) ))
                        .message("Servers created")
                        .status(CREATED) //201
                        .statusCode(CREATED.value())
                        .build()

        );
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<Response> getServer(@PathVariable("id") Long id) {
        //Server server = serverService.getServer(id);
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();

        //log.info("The server image path {}", System.getProperty(("user.home")+"/Download/images"+"Server.png"+id));
        log.info("The server image path {}", path);

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("server", serverService.getServer(id)))
                        .message("Server found")
                        .status(OK) //200
                        .statusCode(OK.value())
                        .build()

        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteServer(@PathVariable("id") Long id) {
        //Server server = serverService.getServer(id);
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("server deleted", serverService.delete(id)))
                        .message("Server deleted")
                        .status(OK) //200
                        .statusCode(OK.value())
                        .build()

        );
    }

    @GetMapping(path="/image/{fileName}", produces = IMAGE_PNG_VALUE)
    public byte[] getServerImage(@PathVariable("fileName") String fileName) throws IOException {
        //Server server = serverService.getServer(id);
        return Files.readAllBytes(Paths.get(System.getProperty(("user.home")+"/Download/images"+fileName)));

    }

}
