package com.intuit.craftdemo.model;

public class DriverDocument {
    private String documentType;
    private String fileUrl;

    public DriverDocument(String documentType, String fileUrl) {
        this.documentType = documentType;
        this.fileUrl = fileUrl;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
