package com.afm.uploadservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UploadController {

    private final Path fileStorageLocation = Paths.get("uploads");
    private final RestTemplate restTemplate;
    private String name;

    private String fileServiceUrl = "http://filesystem-service:8083/files/upload";
    private String generateTokenURL = "http://authentication-service:8082/authenticate";
    private String validateTokenURL = "http://authentication-service:8082/isTokenValid?token=";
    private String addMetaDataURL = "http://mysql-service:8084/api/metadata/add?username=";
    private String authToken = "your_bearer_token_here"; // Ideally, this token is dynamically obtained or configured
//try to do it as a session or a cookie

    @Autowired
    private WebClient webClient;

    @Autowired
    public UploadController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public void uploadFileToService(MultipartFile file) {

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            webClient.post()
                    .uri(fileServiceUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(response -> {
                        // Handle response
                        System.out.println("Response: " + response);
                    });

        } catch (IOException ex) {
            // Handle the error
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization","Bearer "+authToken);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("vidName", file.getOriginalFilename());
        requestBody.put("username", name);
        requestBody.put("url","http://filesystem-service:8083/files/download/" + file.getOriginalFilename() + "?username=" + name);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            System.out.println(addMetaDataURL+name);
            System.out.println("Bearer "+authToken);
            ResponseEntity<String> response = restTemplate.postForEntity(addMetaDataURL, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Assuming the token is in the response body directly
                // Adjust the parsing logic based on your actual response structure
                System.out.println("yeaaah!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean validateUser(){

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(validateTokenURL+authToken , String.class);

            if (response.getStatusCode() == HttpStatus.OK ) {
                // Assuming the token is in the response body directly
                // Adjust the parsing logic based on your actual response structure
                return true;
            }
            else return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @GetMapping("/")
    public String showUploadForm(Model model) {
        if(validateUser())
         return "upload";
        else return ("logIn");
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        // Ensure the filename is normalized to prevent directory traversal attacks
        String fileName = file.getOriginalFilename() != null ? Paths.get(file.getOriginalFilename()).getFileName().toString() : "unknown";
       if( file.getOriginalFilename() == null) return "upload";
       uploadFileToService(file);
       redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + fileName + "!");

        return "redirect:/";
    }



    @PostMapping("/login")
    public String logIn(@RequestParam Map<String,String> mp,HttpServletRequest req){
        String name=mp.get("username");
        System.out.println("name : "+name);
        System.out.println("pass : "+mp.get("password"));
        authToken=generatToken(mp.get("username"),mp.get("password"));
        HttpSession session=req.getSession();
        session.setAttribute("token",authToken);
        session.setAttribute("username",name);
        System.out.println(authToken);
        if(validateUser()) {
            this.name=name;
            return "upload";
        }
        else
         return "logIn";
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
}

