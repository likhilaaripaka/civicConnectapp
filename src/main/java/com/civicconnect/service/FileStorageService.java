package com.civicconnect.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:/tmp/uploads}")
    private String uploadDir;

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            System.out.println("❌ File is empty, not saving");
            return null;
        }

        // Create uploads directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("✅ Created uploads directory: " + uploadPath.toAbsolutePath());
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
        Path targetLocation = uploadPath.resolve(uniqueFilename);

        try {
            // Copy file to target location
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ File saved successfully: " + targetLocation.toAbsolutePath());
            
            // Return just the filename for database storage
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
            Path filePath = Paths.get(uploadDir).resolve(filename);
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                System.out.println("✅ File deleted successfully: " + filename);
            } else {
                System.out.println("⚠️ File not found for deletion: " + filename);
            }
            return deleted;
        } catch (IOException ex) {
            System.err.println("❌ Could not delete file " + filename + ". Error: " + ex.getMessage());
            return false;
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
