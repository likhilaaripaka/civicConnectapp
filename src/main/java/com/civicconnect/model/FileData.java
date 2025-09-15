package com.civicconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "file_data")
public class FileData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] data;
    
    // Constructors
    public FileData() {}
    
    public FileData(String filename, String contentType, Long fileSize, byte[] data) {
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.data = data;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public void setData(byte[] data) {
        this.data = data;
    }
}
