package com.alejandroct.nas.service.implement;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alejandroct.nas.service.IStorageService;

import jakarta.annotation.PostConstruct;

public class StorageServiceImp implements IStorageService{
    
    String root = "src/main/resources/media";
    private Map<String, String> filesExtension;
    long maxSize=100*1024*1024;

    @PostConstruct
    public void init(){
        initFilesExtension();
        
    }

    @Override
    public String saveFile(MultipartFile file) {
        try {
            long fileSize = file.getSize();
        
            if(file.isEmpty()){
                throw new RuntimeException("The file is empty.");
            }
            if(fileSize>=maxSize){
                throw new RuntimeException("File size exceeds limit.");
            }

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String filename = originalFilename;
            String fileType = getFileType(originalFilename);

            Path destinationDirectory = Paths.get(root).resolve(fileType).normalize().toAbsolutePath();
            Files.createDirectories(destinationDirectory);

            //checks if the file exists and appends an incremental number to filename

            int counter = 1;
            Path destinationFile = destinationDirectory.resolve(filename).normalize().toAbsolutePath();
            while(Files.exists(destinationFile)){
                int lastDotIndex = originalFilename.lastIndexOf(".");
                String filenameWithoutExtension = (lastDotIndex != -1) ? originalFilename.substring(0, lastDotIndex) : originalFilename;
                String fileExtension = (lastDotIndex != -1) ? originalFilename.substring(lastDotIndex) : "";
                filename = filenameWithoutExtension+"("+counter+")"+fileExtension;
                destinationFile = destinationDirectory.resolve(filename).normalize().toAbsolutePath();
                counter++;
            }

            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        
            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    @Override
    public Resource loadFile(String filename) {
        throw new UnsupportedOperationException("Unimplemented method 'loadFile'");
    }

    

    private void initFilesExtension(){
        filesExtension = new HashMap<>();
        filesExtension.put("/images", "jpg, jpeg, png, gif");
        filesExtension.put("/music", "mp3, wav, m4a");
        filesExtension.put("/documents", "pdf, doc, docx, pptx, xlsx, txt");
        filesExtension.put("/video", "mp4, avi, mkv");
        filesExtension.put("/other", "");
    }

    private String getFileType(String filename){
        String extension = StringUtils.getFilenameExtension(filename);
        if (extension!=null) {
            for(Map.Entry<String, String> entry : filesExtension.entrySet()){
                if(entry.getValue().contains(extension.toLowerCase())){
                    return entry.getKey();
                }
            }
        }
        return "/other";
    }
    
}
