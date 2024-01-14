package com.alejandroct.nas.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.alejandroct.nas.service.IStorageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/media")
@AllArgsConstructor
public class StorageController {
    private final IStorageService storageService;
    private final HttpServletRequest request;

    @PostMapping("/upload")
    public Map<String, String> uploadFile(@RequestParam("file")MultipartFile multipartFile){
        String path = storageService.saveFile(multipartFile);
        String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String url = ServletUriComponentsBuilder.fromHttpUrl(host).path("/media/").path(path).toUriString();
        return Map.of("url", url);
    }

    @PostMapping("/multiple")
    public Map<String, Object> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        List<String> paths = storageService.saveMultiFile(files);
        String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        List<String> urls = paths.stream()
                .map(path -> ServletUriComponentsBuilder.fromHttpUrl(host).path("/media/").path(path).toUriString())
                .collect(Collectors.toList());

        return Map.of("urls", urls);
    }

    @GetMapping("{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws IOException{
        Resource file = storageService.loadFile(filename);
        String contentType = Files.probeContentType(file.getFile().toPath());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(file);
    }

    @GetMapping("/list/{fileType}")
    public ResponseEntity<Map<String, List<String>>> listFiles(@PathVariable String fileType){
        List<String> fileNames = storageService.listFolderFiles(fileType);
        return ResponseEntity.ok(Map.of("files", fileNames));
    }

}
