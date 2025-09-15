package com.civicconnect.service;

import com.civicconnect.model.FileData;
import com.civicconnect.repository.FileDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    @Autowired
    private FileDataRepository fileDataRepository;

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
            // Save file data to database
            FileData fileData = new FileData(
                uniqueFilename,
                file.getContentType(),
                file.getSize(),
                file.getBytes()
            );
            
            fileDataRepository.save(fileData);
            System.out.println("✅ File saved to database successfully:");
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
            fileDataRepository.findByFilename(filename).ifPresentOrElse(
                fileData -> {
                    fileDataRepository.delete(fileData);
                    System.out.println("✅ File deleted from database successfully: " + filename);
                },
                () -> System.out.println("⚠️ File not found in database for deletion: " + filename)
            );
            return true;
        } catch (Exception ex) {
            System.err.println("❌ Could not delete file " + filename + ". Error: " + ex.getMessage());
            return false;
        }
    }
}
