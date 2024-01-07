package com.alejandroct.nas.controller;

import java.util.Map;

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
}
