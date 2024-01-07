package com.alejandroct.nas.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {
    String saveFile(MultipartFile file);
    Resource loadFile(String filename);
}
