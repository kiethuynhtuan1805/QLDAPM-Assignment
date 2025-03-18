package vn.restaurant.management.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.restaurant.management.service.S3FileUploadService;

import java.io.IOException;

@Service
@Slf4j
public class S3FileUploadServiceImpl implements S3FileUploadService {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String key = file.getOriginalFilename();
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, file.getOriginalFilename(), file.getInputStream(), null);
            amazonS3.putObject(putObjectRequest);
            log.error("Upload amazon s3 success - key={}", key);
            return amazonS3.getUrl(bucketName, key).toString();
        } catch (Exception ex) {
            log.error("Upload amazon s3 failed - key={} - cause = {}",key ,ex.getMessage());
            throw new IOException(ex);
        }
    }
}
