package com.jscode.demoApp.controller;

import com.jscode.demoApp.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final S3FileService fileService;

    @PostMapping("/file")
    public ResponseEntity fileUpload(@RequestParam(name = "file") List<MultipartFile> files) {
        List<URL> createdUrls = fileService.fileUpload(files);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUrls.toString());
    }
}
