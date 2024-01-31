package com.alejandroct.nas.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.alejandroct.nas.model.DataFile;

public interface IStorageService {
    List<DataFile> listFolderFiles(String folderName);
    Resource loadFile(String filename);
    List<DataFile> saveListFiles(List<MultipartFile> files);
    DataFile saveFile(MultipartFile file);
}
