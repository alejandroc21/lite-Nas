package com.alejandroct.nas.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {
    String saveFile(MultipartFile file);
    List<String> saveMultiFile(List<MultipartFile> files);
    Resource loadFile(String filename);
}
