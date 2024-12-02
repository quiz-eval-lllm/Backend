package com.ta.llmbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.ta.llmbackend.dto.request.GenerateQuizReq;
import com.ta.llmbackend.service.PackageService;
import com.ta.llmbackend.service.RabbitMQService;
import com.ta.llmbackend.model.*;
import com.ta.llmbackend.model.Package;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Controller
public class TestQuizController {

    private final String uploadDirectory = "pdf_context";

    @Autowired
    private PackageService packageService;

    @Autowired
    private RabbitMQService rabbitMQService;

    @GetMapping("/quiz/generate")
    public String testQuizPage(Model model) {

        model.addAttribute("generateQuizReq", new GenerateQuizReq());
        return "generateQuiz";
    }

    @PostMapping("/quiz/generate")
    public String handleFormSubmit(@ModelAttribute GenerateQuizReq generateQuizReq, Model model) throws IOException {
        MultipartFile file = generateQuizReq.getContext();
        String fileLink = null;
        String packageId = "";

        if (file != null && !file.isEmpty()) {
            try {
                File directory = new File(uploadDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Generate filename & storing file
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(uploadDirectory, fileName);
                Files.write(path, file.getBytes());

                fileLink = "http://34.16.76.175/:8080/pdf_context/" + fileName;

                // Create new package
                generateQuizReq.setContextUrl(fileLink);

                Package quizPackage = packageService.createNewPackage(generateQuizReq);
                packageId = quizPackage.getPackageId().toString();

                // Send message to rabbitMQ queue
                Map<String, Object> rabbitMQResponse = rabbitMQService.sendMsgForGenerate(quizPackage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        model.addAttribute("message", "File uploaded successfully!");
        model.addAttribute("fileLink", fileLink);
        model.addAttribute("packageId", packageId);

        return "generateQuiz";
    }
}