package com.indulgetech.utils;

import com.indulgetech.validators.ValidationErrors;
import org.apache.commons.io.FilenameUtils;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileUploadValidatorUtils {
    @Deprecated
    public static void validateRequired(MultipartFile file, Errors bindingResult) {
        if (file.isEmpty()) {
            //FieldError fieldError = new FieldError("", "", "");
            bindingResult.reject(file.getName(), "No file uploaded");
        }
    }
    @Deprecated
    public static void validateFileType(MultipartFile file, List<String> mimeTypes, Errors bindingResult) {
        if (!mimeTypes.contains(FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase())) {
            bindingResult.reject(file.getName(), "File not a valid file type");
        }
    }
    @Deprecated
    public static void validateSize(MultipartFile file, long maxSize, Errors bindingResult) {
        if (file!=null&&file.getSize() > maxSize) {
            bindingResult.reject(file.getName(), "Uploaded file size exceeds maximum allowed:" + (double)(maxSize / (1024*1024)) + "mb");
        }
    }

    public static void validateRequired(MultipartFile file, ValidationErrors validationErrors) {
        if (file==null||file.isEmpty()) {
            validationErrors.addError("", file!=null?file.getName():"","","No file uploaded");
        }
    }
    public static void validateFileType(MultipartFile file, List<String> mimeTypes,ValidationErrors validationErrors) {
        if (file!=null&&!mimeTypes.contains(FilenameUtils.getExtension(file.getOriginalFilename()))) {
            validationErrors.addError("",file.getName(),FilenameUtils.getExtension(file.getOriginalFilename()), "File not a valid file type");
        }
    }
    public static void validateSize(MultipartFile file, long maxSize,ValidationErrors validationErrors) {
        if (file!=null&&file.getSize() > maxSize) {
            validationErrors.addError("",file.getName(),file.getSize(), "Uploaded file size exceeds maximum allowed:" + (double)(maxSize / (1024*1024)) + "mb");
        }
    }

    public static boolean isFileUploaded(MultipartFile multipartFile) {
        if (multipartFile != null) {
            return true;
        }
        return false;
    }
}
