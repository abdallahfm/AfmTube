package com.afm.MySqlDBservice.controllers;

import com.afm.MySqlDBservice.model.Video;
import com.afm.MySqlDBservice.services.queryService;
import com.afm.MySqlDBservice.services.auth;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/metadata")
public class MainController {

    private final queryService service;
    private final auth auth;

    @Autowired
    public MainController(queryService service, auth auth) {
        this.service = service;
        this.auth=auth;
    }
    private boolean validate(String name,String token){
        token=token.substring(7);
        System.out.println("token is "+token);
        System.out.println("name is "+name);
        boolean res=auth.validateToken(token,name);
        System.out.println("result "+res);
        return res;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Video>> getAll(HttpServletRequest request) {

        String token= request.getHeader("Authorization");
        String name =request.getParameter("username");

        if(validate(name, token)) {
            List<Video> videos = service.findAll();
            return new ResponseEntity<>(videos, HttpStatus.OK);
        } else {
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }
    @GetMapping("/getById")
    public Optional<Video> getVid(HttpServletRequest request){
        String token= request.getHeader("Authorization");
        String name =request.getParameter("username");
        Long id = Long.valueOf(request.getParameter("id"));
        if(validate(name,token))
            return service.findById(id);
        return Optional.empty();
    }
    @PostMapping( "/add")
    public String addVid(@RequestBody Map<String,String> video, HttpServletRequest request){
        String token= request.getHeader("Authorization");
        System.out.println(" ddddd");

        System.out.println(video.get("vidName")+" "+video.get("username"));
        if(validate(video.get("username"),token)) {
            service.addVideo(video.get("vidName"), video.get("username"), "http://localhost:8083/files/download/" + video.get("vidName") + "?username=" + video.get("username"));
            return "done!";
        }
        else return "Error!";
    }


}

