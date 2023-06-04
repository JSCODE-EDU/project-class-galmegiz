package com.jscode.demoApp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.ResourceCreationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<URL> fileUpload(List<MultipartFile> multipartFiles){
        List<URL> urlList = new ArrayList<>();
        multipartFiles.forEach(multipartFile -> {
            String fileName = makeFileName(multipartFile);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());

            try(InputStream inputStream = multipartFile.getInputStream()){
                amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

            }catch(IOException e){
                throw new ResourceCreationException(ErrorCode.FILE_UPLOAD_ERROR, "Img Upload Fail");
            }
            urlList.add(amazonS3Client.getUrl(bucket, fileName));
        });

        return urlList;
    }

    private String makeFileName(MultipartFile multipartFile){
        String originalName = multipartFile.getOriginalFilename();
        final String ext = originalName.substring(originalName.lastIndexOf("."));
        final String fileName = UUID.randomUUID().toString() + ext;
        return System.getProperty("user.dir") + fileName;
    }
}
