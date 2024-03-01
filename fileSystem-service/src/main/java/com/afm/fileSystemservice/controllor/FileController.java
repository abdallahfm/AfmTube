package com.afm.fileSystemservice.controllor;

import com.afm.fileSystemservice.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.afm.fileSystemservice.service.auth;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;
    private final auth auth;

    @Autowired
    public FileController(FileStorageService fileStorageService, com.afm.fileSystemservice.service.auth auth) {
        this.fileStorageService = fileStorageService;
        this.auth = auth;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        return ResponseEntity.ok().body("File uploaded successfully: " + fileName);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        String contentType = null;
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        try {
        // Load file as Resource
        String token= (String) request.getHeader("Authorization");
        String name =request.getParameter("username");
        token=token.substring(7);
        System.out.println("token is "+token);
        System.out.println("name is "+name);
        boolean res=auth.validateToken(token,name);
        System.out.println("result "+res);
        if(!res)throw new Exception();

        contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Fallback to the default content type if type could not be determined
            contentType = "application/octet-stream";
        }
        catch (Exception e){
            System.out.println("error!!!!!");
           return new ResponseEntity<>(null , HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/getVideo/{filename:.+}")
    public void getVideo(HttpServletResponse response, @PathVariable String filename) throws IOException {

        Path videoPath =  fileStorageService.returnAdress().resolve(filename);

        //Path videoPath = Paths.get("c:\\Users\\abdal\\OneDrive\\Desktop\\atypon\\upload").resolve(filename);
        if (!Files.exists(videoPath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("video/mp4");
        Files.copy(videoPath, response.getOutputStream());
    }
}
