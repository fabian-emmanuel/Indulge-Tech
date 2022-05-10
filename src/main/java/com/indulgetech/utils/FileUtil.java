package com.indulgetech.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;

public class FileUtil {

    public static void copyFile(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    public static void delete(File file) {
        file.delete();
    }


    public static File convertMultiPartToFile(MultipartFile file) {
        try {
            //File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            File convFile = Files.createTempFile(FilenameUtils.getBaseName(file.getOriginalFilename()), "."+FilenameUtils.getExtension(file.getOriginalFilename()))
               .toFile();
            file.transferTo(convFile);
            return convFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
