package vn.restaurant.management.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3FileUploadService {

    /**
     * upload file to aws s3
     * @param file
     * @return url from s3
     */
     String uploadFile(MultipartFile file) throws IOException;

}
