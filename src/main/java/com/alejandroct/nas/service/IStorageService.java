package com.alejandroct.nas.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.alejandroct.nas.model.DataFile;

public interface IStorageService {
    List<String> listFolderFiles(String folderName);
    String saveFile(MultipartFile file);
    List<String> saveMultiFile(List<MultipartFile> files);
    Resource loadFile(String filename);
    DataFile uploadToObj(MultipartFile multipartFile);
}
