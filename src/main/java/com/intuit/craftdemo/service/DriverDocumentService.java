package com.intuit.craftdemo.service;

import com.intuit.craftdemo.model.Driver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class DriverDocumentService {
    private DocumentCollection documentCollection;

    public DriverDocumentService(DocumentCollection documentCollection) {
        this.documentCollection = documentCollection;
    }

    public boolean submitDocuments(Driver driver, Map<String, List<MultipartFile>> groupedFiles) {
        return documentCollection.collectAndSaveDocuments(groupedFiles, driver);
    }
}
