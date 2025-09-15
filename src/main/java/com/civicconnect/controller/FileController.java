package com.civicconnect.controller;

import com.civicconnect.model.FileData;
import com.civicconnect.repository.FileDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class FileController {

    @Autowired
    private FileDataRepository fileDataRepository;

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<byte[]> serveFile(@PathVariable String filename) {
        try {
            System.out.println("=== DATABASE FILE CONTROLLER ===");
            System.out.println("Requested filename: " + filename);
            
            Optional<FileData> fileDataOpt = fileDataRepository.findByFilename(filename);
            
            if (fileDataOpt.isPresent()) {
                FileData fileData = fileDataOpt.get();
                System.out.println("File found in database:");
                System.out.println("- Filename: " + fileData.getFilename());
                System.out.println("- Content Type: " + fileData.getContentType());
                System.out.println("- File Size: " + fileData.getFileSize() + " bytes");
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(fileData.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileData.getFilename() + "\"")
                        .body(fileData.getData());
            } else {
                System.err.println("File not found in database: " + filename);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error serving file from database: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}
