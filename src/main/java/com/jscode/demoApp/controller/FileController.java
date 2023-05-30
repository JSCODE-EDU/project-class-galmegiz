package com.jscode.demoApp.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.jscode.demoApp.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final S3FileService fileService;

    @PostMapping("/file")
    public ResponseEntity fileUpload(@RequestParam MultipartFile file) throws URISyntaxException {
        return ResponseEntity.created(fileService.fileUpload(file).toURI()).body("파일 생성되었습니다.");
    }
}
