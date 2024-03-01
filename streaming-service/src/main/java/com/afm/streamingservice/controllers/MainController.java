package com.afm.streamingservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.afm.streamingservice.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class MainController {
    @Autowired
    private RestTemplate restTemplate;

//    public String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYmQiLCJpYXQiOjE3MDkyMzAzNjksImV4cCI6MTcwOTI2NjM2OX0.DudeKVNDwNxMhreRGxfqAYt-B7odq81I0fDVMMWbgKI";//temp only
    public String token ="";
    public String username = "";

    private String generateTokenURL = "http://authentication-service:8082/authenticate";
    private String validateTokenURL = "http://authentication-service:8082/isTokenValid?token=";






    @PostMapping("/login")
    public String logIn(@RequestParam Map<String,String> mp, HttpServletRequest req ,Model model){
        String name=mp.get("username");
        System.out.println("name : "+name);
        System.out.println("pass : "+mp.get("password"));
        token=generatToken(mp.get("username"),mp.get("password"));
        HttpSession session=req.getSession();
        session.setAttribute("token",token);
        session.setAttribute("username",name);
        System.out.println(token);
        if(validateUser()) {
            this.username=name;
           return listVideos(model);
        }
        else
            return "login";
    }
    private boolean validateUser(){

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(validateTokenURL+token , String.class);

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public String generatToken(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(generateTokenURL, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Assuming the token is in the response body directly
                // Adjust the parsing logic based on your actual response structure
                return response.getBody();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ""; // Return an empty string if the response is not 200 OK
    }




    @GetMapping(value = {"/videos","/"})
    public String listVideos(Model model) {
        if(validateUser()) {
            List<Video> videos = fetchVideoList(username, token);
            model.addAttribute("videos", videos);
            return "videos";
        } else return ("login");

    }


    @GetMapping("/stream/video/{videoName}")
    public String streamVideo(@PathVariable String videoName, Model model) {
        if(validateUser()) {
        String videoUrl = "http://localhost:8083/files/getVideo/" + videoName;
        model.addAttribute("videoUrl", videoUrl);
        System.out.println(videoUrl);
        return "video";}
        else return ("login");
    }



    private List<Video> fetchVideoList(String username, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<List<Video>> response = restTemplate.exchange(
                "http://mysql-service:8084/api/metadata/getAll?username=" + username,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );
        ModelAndView modelAndView = new ModelAndView("videos");
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
             return response.getBody();
        }
     return null;
    }


}
