package com.intuit.craftdemo.service;

import com.intuit.craftdemo.model.Driver;
import com.intuit.craftdemo.model.DriverDocument;
import com.intuit.craftdemo.repository.DriverRepository;
import com.intuit.craftdemo.config.MessageStrings;
import com.intuit.craftdemo.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class DocumentCollection {

    Logger logger = LoggerFactory.getLogger(DriverService.class);
    @Autowired
    DriverRepository driverRepository;
    public boolean collectAndSaveDocuments(Map<String, List<MultipartFile>> groupedFiles, Driver driver) throws CustomException {
        for (Map.Entry<String, List<MultipartFile>> entry : groupedFiles.entrySet()) {
            String documentType = entry.getKey();
            List<MultipartFile> files = entry.getValue();

            for (MultipartFile file : files) {
                try {
                    String fileName = file.getOriginalFilename();
                    String filePath = MessageStrings.FILEPATH;
                    file.transferTo(new File(filePath + fileName));
                    DriverDocument document = new DriverDocument(documentType, filePath+fileName);
                    driver.getDocuments().add(document);
                    driverRepository.save(driver);
                    logger.info("Documents saved for driver: " + driver.getId());
                } catch (IOException e) {
                    // Log the exception and throw a custom exception
                    logger.error("Failed to collect document: " + e.getMessage());
                    throw new CustomException("Failed to collect document: " + e.getMessage());
                }
            }
        }
        return true;
    }
}
