package com.ta.llmbackend.controller;

import java.nio.file.Paths;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/upload_pdf")
public class PDFController {

    private final String uploadDirectory = "pdf_context";

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null && file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDirectory, fileName);
            Files.write(path, file.getBytes());

            // <=============== Deployed ===============>
            String fileLink = "http://34.27.150.5/:8080/pdf_context/" + fileName;

            // <=============== Local ===============>
            // String fileLink = "http://localhost:8080/pdf_context/" + fileName;

            return ResponseEntity.ok(fileLink);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }
}
