package com.alejandroct.nas.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alejandroct.nas.model.DataFile;
import com.alejandroct.nas.service.IStorageService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/media")
@AllArgsConstructor
public class StorageController {
    private final IStorageService storageService;

    @PostMapping("/upload-list")
    public ResponseEntity<List<DataFile>> saveListFiles(@RequestParam("file")List<MultipartFile> files){
        return new ResponseEntity<>(storageService.saveListFiles(files), HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<DataFile> saveSingleFile(@RequestParam("file")MultipartFile file){
        return new ResponseEntity<>(storageService.saveFile(file), HttpStatus.OK);
    }


    @GetMapping("{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws IOException{
        Resource file = storageService.loadFile(filename);
        String contentType = Files.probeContentType(file.getFile().toPath());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(file);
    }

    @GetMapping("/list/{fileType}")
    public ResponseEntity<List<DataFile>> listFiles(@PathVariable String fileType){
        return new ResponseEntity<>(storageService.listFolderFiles(fileType), HttpStatus.OK);
    }

}
