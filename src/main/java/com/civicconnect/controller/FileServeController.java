package com.civicconnect.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class FileServeController {

    // In-memory storage for uploaded files (temporary solution)
    private final Map<String, FileInfo> fileStorage = new ConcurrentHashMap<>();

    public void storeFile(String filename, String contentType, byte[] data) {
        fileStorage.put(filename, new FileInfo(contentType, data));
        System.out.println("✅ File stored in memory: " + filename + " (" + data.length + " bytes)");
        System.out.println("✅ Total files in storage: " + fileStorage.size());
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<byte[]> serveFile(@PathVariable String filename) {
        try {
            System.out.println("=== MEMORY FILE CONTROLLER ===");
            System.out.println("Requested filename: " + filename);
            System.out.println("Current storage size: " + fileStorage.size());
            System.out.println("Available files: " + fileStorage.keySet());
            
            FileInfo fileInfo = fileStorage.get(filename);
            
            if (fileInfo != null) {
                System.out.println("✅ File found in memory:");
                System.out.println("- Filename: " + filename);
                System.out.println("- Content Type: " + fileInfo.contentType);
                System.out.println("- File Size: " + fileInfo.data.length + " bytes");
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(fileInfo.contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(fileInfo.data);
            } else {
                System.err.println("❌ File not found in memory: " + filename);
                System.err.println("❌ This file was likely uploaded before the latest deployment");
                System.err.println("❌ Available files in current session: " + fileStorage.keySet());
                
                // Return a placeholder response instead of 404 for better UX
                String placeholderMessage = "File not available - please re-upload";
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(placeholderMessage.getBytes());
            }
        } catch (Exception e) {
            System.err.println("❌ Error serving file from memory: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    private static class FileInfo {
        final String contentType;
        final byte[] data;

        FileInfo(String contentType, byte[] data) {
            this.contentType = contentType;
            this.data = data;
        }
    }
}
