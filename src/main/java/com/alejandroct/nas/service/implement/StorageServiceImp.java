package com.alejandroct.nas.service.implement;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alejandroct.nas.exception.DirectoryNotFoundException;
import com.alejandroct.nas.model.DataFile;
import com.alejandroct.nas.service.IStorageService;
import jakarta.annotation.PostConstruct;

@Service
public class StorageServiceImp implements IStorageService{
    
    String root = "src/main/resources/media";
    private Map<String, String> filesExtension;

    @PostConstruct
    public void init(){
        initFilesExtension();
        createDirectories();
    }

    @Override
    public List<DataFile> listFolderFiles(String fileType){
        Path directory = Paths.get(root).resolve(fileType).normalize().toAbsolutePath();
        if (!Files.exists(directory)) {
            throw new DirectoryNotFoundException("Directory does not exist: "+fileType);
        }
        List<DataFile> dataFiles = new ArrayList<>();
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)){
            for(Path path : directoryStream){
                if(Files.isRegularFile(path)){
                    dataFiles.add(new DataFile(path));
                }
            }
        }catch(IOException e){
            throw new RuntimeException("Could not list files.");
            
        }
        return dataFiles;
    }

    @Override
    public List<DataFile> saveListFiles(List<MultipartFile> files) {
        List<DataFile> dataFiles = new ArrayList<>();
        for(MultipartFile file: files){
            dataFiles.add(saveFile(file));
        }
        return dataFiles;
    }

    public DataFile saveFile(MultipartFile multipartFile) {
        try{
            if(multipartFile.isEmpty()){
                throw new RuntimeException("this file is empty");
            }

            String originalFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
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

            try(InputStream inputStream = multipartFile.getInputStream()){
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            DataFile dataFile = new DataFile(destinationFile);
            return dataFile;
        }catch(Exception e){
            throw new RuntimeException("failed to saveFile");
        }
    }

    @Override
    public Resource loadFile(String filename) {
        try {
            String fileType = getFileType(filename);
            Path file;

            if(filename.contains("icon_")){
                file = Paths.get("src/main/resources/static/image/").resolve(filename);
            }else{
                file = Paths.get(root+"/"+fileType).resolve(filename);
            }
            Resource resource = new UrlResource(file.toUri());

            if(resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new RuntimeException("Could not load file: "+filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file.");
        }
    }

    private void initFilesExtension(){
        filesExtension = new HashMap<>();
        filesExtension.put("images", "jpg, jpeg, png, gif");
        filesExtension.put("music", "mp3, wav, m4a, mpeg");
        filesExtension.put("documents", "pdf, doc, docx, pptx, xlsx, txt");
        filesExtension.put("videos", "mp4, avi, mkv");
        filesExtension.put("other", "");
    }

    private void createDirectories(){
        try {
            Files.createDirectories(Paths.get(root).resolve("images").normalize().toAbsolutePath());
            Files.createDirectories(Paths.get(root).resolve("music").normalize().toAbsolutePath());
            Files.createDirectories(Paths.get(root).resolve("videos").normalize().toAbsolutePath());
            Files.createDirectories(Paths.get(root).resolve("documents").normalize().toAbsolutePath());
            Files.createDirectories(Paths.get(root).resolve("other").normalize().toAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create base directories.");
        }
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
        return "other";
    }
}
