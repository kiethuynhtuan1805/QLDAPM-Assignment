package vn.restaurant.management;

import com.amazonaws.util.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import vn.restaurant.management.entity.Roles;
import vn.restaurant.management.service.S3FileUploadService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@SpringBootTest
public class S3FileUploadTest {

    @Autowired
    private S3FileUploadService s3FileUploadService;

    @Test
    void TestRole() throws IOException {
        File file = new File("src/test/java/vn/restaurant/management/images.jpeg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));

        String fileS3 = s3FileUploadService.uploadFile(multipartFile);
        Assertions.assertNotNull(fileS3);
    }

}
