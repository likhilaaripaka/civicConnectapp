package com.civicconnect.service;

import com.civicconnect.controller.FileServeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    @Autowired
    private FileServeController fileServeController;

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            System.out.println("❌ File is empty, not saving");
            return null;
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            System.out.println("❌ Original filename is null or empty");
            return null;
        }

        // Create unique filename with timestamp and UUID
        String fileExtension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFilename.substring(dotIndex);
        }
        
        String uniqueFilename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + fileExtension;

        try {
            // Store file in memory for immediate access
            fileServeController.storeFile(uniqueFilename, file.getContentType(), file.getBytes());
            
            System.out.println("✅ File saved to memory successfully:");
            System.out.println("- Original: " + originalFilename);
            System.out.println("- Saved as: " + uniqueFilename);
            System.out.println("- Size: " + file.getSize() + " bytes");
            System.out.println("- Content Type: " + file.getContentType());
            
            return uniqueFilename;
        } catch (IOException ex) {
            System.err.println("❌ Could not store file " + uniqueFilename + ". Error: " + ex.getMessage());
            throw new IOException("Could not store file " + uniqueFilename, ex);
        }
    }

    public boolean deleteFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        try {
            // For now, we'll just log the deletion request since files are in memory
            System.out.println("⚠️ File deletion requested for: " + filename);
            return true;
        } catch (Exception ex) {
            System.err.println("❌ Could not delete file " + filename + ". Error: " + ex.getMessage());
            return false;
        }
    }
}
