package com.indulgetech.utils;


import com.indulgetech.constants.Constants;
import com.indulgetech.models.users.client.ClientUser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class FilePathUtils {

    public static String buildClientFileLocation(ClientUser clientUser, String dir) {
        //user dir+resource dir
        return clientUser.getLastName()+File.separator+dir;
    }

    public static String buildUniqueFileName(File file){
        //return LocalDateTime.now()+"."+ FilenameUtils.getExtension(file.getName());
        return RandomStringUtils.randomAlphanumeric(30)+"."+ FilenameUtils.getExtension(file.getName());
    }

    public static String buildUniqueFileName(MultipartFile file){
        //return LocalDateTime.now() +"."+FilenameUtils.getExtension(file.getOriginalFilename());
        return RandomStringUtils.randomAlphanumeric(30)+"."+FilenameUtils.getExtension(file.getOriginalFilename());
    }

    public static String getFileNameFromFileNamePath(String fileNamePath) {
        if(fileNamePath==null){
           return null;
        }
        if(!fileNamePath.contains(File.separator)){
            return fileNamePath;
        }
       return fileNamePath.substring(fileNamePath.lastIndexOf(File.separator) + 1);
    }

    public static String getPathFromFileNamePath(String fileNamePath) {
        if(fileNamePath==null){
            return null;
        }
        if(!fileNamePath.contains(File.separator)){
            return fileNamePath;
        }
        return fileNamePath.substring(0,fileNamePath.lastIndexOf(File.separator));
    }

    public static boolean fileHasNewExtension(String filename1, String filename2) {
       return !FilenameUtils.getExtension(filename1).equals(FilenameUtils.getExtension(filename2));
    }

    public static String buildAdminUserProfilePicUploadPath() {
        return Constants.PROFILE_PIC_DIR;
    }

    public static String buildClientUserProfilePicUploadPath(ClientUser clientUser) {
        return clientUser.getLastName()+File.separator+Constants.PROFILE_PIC_DIR;
    }

    public static String buildFileUrl(String storageLocation, String dir, String fileName) {
        return storageLocation+File.separator+dir+File.separator+fileName;
    }

}
