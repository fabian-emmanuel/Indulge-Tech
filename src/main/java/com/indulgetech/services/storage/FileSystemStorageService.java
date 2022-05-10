package com.indulgetech.services.storage;


import com.indulgetech.configurations.FileStorageProperties;
import com.indulgetech.exceptions.FileStorageException;
import com.indulgetech.utils.FilePathUtils;
import com.indulgetech.utils.FileUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service("fileSystemStorageService")
public class FileSystemStorageService implements FileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemStorageService.class);

    private final FileStorageProperties fileStorageProperties;
    @Value("${api.url-domain}")
    private String appUrl;

    @Value("${file.working-dir}")
    private String workingDir;


    public FileSystemStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    /**
     * Store file with uploaded fileName and default(root) location
     *
     * @param file
     * @return
     */
    @Override
    public String storeFile(File file) {
        return storeFile(file, file.getName());
    }


    /**
     * Store file with defined fileName and location
     *
     * @param file
     * @param filenamePath
     * @return
     */
    @Override
    public String storeFile(File file, String filenamePath) {

        InputStream fileInputStream = null;
        try {
            String filePath = FilePathUtils.getPathFromFileNamePath(filenamePath);
            String filename = FilePathUtils.getFileNameFromFileNamePath(filenamePath);

            Path fileStorageLocation = createStorageLocationIfNotExist(this.buildLocalPath(filePath));
            // Normalize file name
            filenamePath = StringUtils.cleanPath(filenamePath);

            fileInputStream = new FileInputStream(file);
            // Check if the file's name contains invalid characters
            //should return absolute web path to the file
            copyFile(fileInputStream, fileStorageLocation, filename);

            return this.buildUrlPath(filenamePath);
        } catch (IOException ex) {
            LOGGER.error("Could not store file", ex);
            throw new FileStorageException("Could not store file " + filenamePath + ". Please try again!", ex);
        } finally {
            FileUtil.delete(file);
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean deleteDir(String dir) {
        //warning: dir to delete must not be empty. if empty
        //the entire application root dir which contains the application will be deleted
        if (org.apache.commons.lang3.StringUtils.isEmpty(dir)) {
            throw new IllegalArgumentException("Attempt to delete an empty dir when deleting a dir");
        }
        String uploadRootDir = this.fileStorageProperties.getUploadDir();
        Path fileStorageLocation = Paths.get(uploadRootDir + File.separator + dir).toAbsolutePath().normalize();
        return FileUtils.deleteQuietly(fileStorageLocation.toFile());
    }

    @Override
    public byte[] getFileContent(String filenamePath) {
        byte[] content = null;
        Path fileStorageLocation = Paths.get(this.buildLocalPath(filenamePath)).toAbsolutePath().normalize();
        try {
            content = Files.readAllBytes(fileStorageLocation);
        } catch (IOException e) {
            LOGGER.info("IO Error Message= " + e.getMessage());
        }
        return content;
    }

    @Override
    public String storeAndRemoveExisting(MultipartFile file, String filenamePath, String existingFilePath) {
        String url = this.storeFile(file, filenamePath);
        if (!org.apache.commons.lang3.StringUtils.isEmpty(url)) {
            this.deleteFile(existingFilePath);
        }
        return url;
    }

    @Override
    public String storeAndRemoveExisting(File file, String filenamePath, String existingFilePath) {
        String url = this.storeFile(file, filenamePath);
        if (!org.apache.commons.lang3.StringUtils.isEmpty(url)) {
            this.deleteFile(existingFilePath);
        }
        return url;
    }

    @Override
    public String getStorageLocation() {
        return appUrl + File.separator +
                this.fileStorageProperties.getUploadDir() +
                File.separator + workingDir;
    }

    @Override
    public List<String> getDocuments(String code) {
        return null;
    }

    /**
     * build full context directory path for a given path
     *
     * @param path (sub)dir or filename or combination of both
     * @return
     */
    private String buildLocalPath(String path) {
        return fileStorageProperties.getUploadDir() + File.separator +
                workingDir +
                File.separator +
                path;
    }

    private String buildUrlPath(String filenamePath) {
        return appUrl + File.separator +
                this.fileStorageProperties.getUploadDir() +
                File.separator + workingDir +
                File.separator + filenamePath;
    }

    @Override
    public boolean deleteFile(String fileNamePath) {
        String localPath = this.buildLocalPath(fileNamePath);
        //warning: localpath to delete must not be empty. if empty, it will resolve to deleting an empty dir
        // and subsequently the entire application root dir which contains the application will be deleted
        if (org.apache.commons.lang3.StringUtils.isEmpty(localPath)) {
            throw new IllegalArgumentException("Attempt to delete an empty dir when deleting file");
        }
        Path fileStorageLocation = Paths.get(localPath).toAbsolutePath().normalize();
        return FileUtils.deleteQuietly(fileStorageLocation.toFile());

    }

    @Override
    public boolean fileExists(String fileNamePath) {
        Path fileStorageLocation = Paths.get(this.buildLocalPath(fileNamePath)).toAbsolutePath().normalize();
        return Files.exists(fileStorageLocation);
    }

    @Override
    public void emptyDir(String dirName) {
        Path uploadLocation = Paths.get(dirName).toAbsolutePath().normalize();
        try {
            FileUtils.cleanDirectory(uploadLocation.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createDirectory(String dir) {

        Path dirPath = Paths.get(dir).toAbsolutePath().normalize();
        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
                    ex);
        }
    }

    /**
     * Store multipartfile with uploade fileName and default(root) location
     *
     * @param file
     * @return
     */
    @Override
    public String storeFile(MultipartFile file) {
        return storeFile(file, file.getOriginalFilename());
    }

    /**
     * Store multipart file with defined fileName and location
     *
     * @param file
     * @param filenamePath
     * @return
     */
    @Override
    public String storeFile(MultipartFile file, String filenamePath) {
        try {
            String filePath = FilePathUtils.getPathFromFileNamePath(filenamePath);
            String filename = FilePathUtils.getFileNameFromFileNamePath(filenamePath);

            Path fileStorageLocation = createStorageLocationIfNotExist(this.buildLocalPath(filePath));
            // Normalize file name
            filename = StringUtils.cleanPath(filename);
            //return fileName;
            copyFile(file.getInputStream(), fileStorageLocation, filename);
            return this.buildUrlPath(filenamePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + filenamePath + ". Please try again!", ex);
        } finally {

        }
    }

    private void copyFile(InputStream inputStream, Path fileStorageLocation, String fileName) throws IOException {
        // Check if the file's name contains invalid characters
        if (fileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }
        // Copy file to the target location (Replacing existing file with the same name)
        Files.copy(inputStream, fileStorageLocation.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
    }


    private Path createStorageLocationIfNotExist(String storageLocation) throws IOException {

        Path fileStorageLocation = Paths.get(storageLocation)
                .toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            Files.createDirectories(fileStorageLocation);
        }
        return fileStorageLocation;
    }

}
