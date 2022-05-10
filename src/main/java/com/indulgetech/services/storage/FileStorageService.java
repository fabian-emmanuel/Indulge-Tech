package com.indulgetech.services.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;


public interface FileStorageService {

    String storeFile(File file);

    String storeFile(File file, String filenamePath);
    
    boolean deleteFile(String file);
    
    boolean fileExists(String filename);
    
    void emptyDir(String dirName);

	void createDirectory(String uploadDir);

	String storeFile(MultipartFile file);

    String storeFile(MultipartFile file,String filenamePath);

    default boolean deleteDir(String uploadDir){
        return true;
    };

    byte[] getFileContent(String filePath);

    String storeAndRemoveExisting(MultipartFile file, String filenamePath, String existingFilePath);

    String storeAndRemoveExisting(File file, String filenamePath, String existingFilePath);

    String getStorageLocation();

    List<String> getDocuments(String code);
}
